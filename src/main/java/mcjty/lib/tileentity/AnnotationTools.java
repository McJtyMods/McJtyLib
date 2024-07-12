package mcjty.lib.tileentity;

import mcjty.lib.McJtyLib;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.bindings.Value;
import mcjty.lib.blockcommands.*;
import mcjty.lib.container.AutomationFilterItemHander;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.NamedEnum;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class AnnotationTools {

    public static AnnotationHolder createAnnotationHolder(Class<? extends GenericTileEntity> clazz) {
        AnnotationHolder holder;
        holder = new AnnotationHolder();
        AnnotationHolder.annotations.put(clazz, holder);
        scanServerCommands(clazz, holder);
        scanGuiValues(clazz, holder);
        scanCaps(clazz, holder);

        holder.valueMap.put(GenericTileEntity.VALUE_RSMODE.name(), new ValueHolder<>(GenericTileEntity.VALUE_RSMODE, GenericTileEntity::getRSModeInt, GenericTileEntity::setRSModeInt));
        return holder;
    }

    private static void scanCaps(Class<? extends GenericTileEntity> clazz, AnnotationHolder holder) {
        Field[] caps = FieldUtils.getFieldsWithAnnotation(clazz, Cap.class);
        for (Field cap : caps) {
            Cap annotation = cap.getAnnotation(Cap.class);
            CapType type = annotation.type();
            Object instance = null;
            try {
                instance = FieldUtils.readField(cap, clazz, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            Object finalInstance = instance;
            Lazy<?> lazy;
            if (instance instanceof Lazy<?>) {
                lazy = Lazy.of(() -> ((Lazy<?>) finalInstance).get());
            } else if (type == CapType.ITEMS_AUTOMATION) {
                lazy = Lazy.of(() -> new AutomationFilterItemHander((GenericItemHandler) finalInstance));
            } else {
                lazy = Lazy.of(() -> finalInstance);
            }

            holder.caps.add(new AnnotationHolder.CapHolder<>(type.getCapability(), new IBlockCapabilityProvider<Object, Object>() {
                @Override
                public @Nullable Object getCapability(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Object context) {
                    return lazy.get();
                }
            }, block));
        }
    }

    private static void scanServerCommands(Class<? extends GenericTileEntity> clazz, AnnotationHolder holder) {
        Field[] commandFields = FieldUtils.getFieldsWithAnnotation(clazz, ServerCommand.class);
        for (Field field : commandFields) {
            ServerCommand serverCommand = field.getAnnotation(ServerCommand.class);
            try {
                Object o = field.get(null);
                if (o instanceof Command cmd) {
                    holder.serverCommands.put(cmd.name(), cmd.cmd());
                } else if (o instanceof ResultCommand cmd) {
                    holder.serverCommandsWithResult.put(cmd.name(), cmd.getCmd());
                    holder.clientCommands.put(cmd.name(), cmd.getClientCommand());
                } else if (o instanceof ListCommand cmd) {
                    holder.serverCommandsWithListResult.put(cmd.name(), cmd.cmd());
                    holder.clientCommandsWithList.put(cmd.name(), cmd.clientCommand());
                    if (serverCommand.type() != void.class) {
                        ISerializer instance = getSerializer(serverCommand);
                        McJtyLib.registerListCommandInfo(cmd.name(), serverCommand.type(), instance.getDeserializer(), instance.getSerializer());
                    }
                } else {
                    throw new IllegalStateException("Only use @ServerCommand with either a Command, a ListCommand or a ResultCommand!");
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void scanGuiValues(Class<? extends GenericTileEntity> clazz, AnnotationHolder holder) {
        Field[] valFields = FieldUtils.getFieldsWithAnnotation(clazz, GuiValue.class);
        for (Field field : valFields) {
            GuiValue val = field.getAnnotation(GuiValue.class);
            try {
                Value value;
                if (Modifier.isStatic(field.getModifiers())) {
                    value = (Value) field.get(null);
                } else {
                    // The field is not static. We assume it is a regular instance field
                    value = setupInstanceValue(field, val);
                }
                holder.valueMap.put(value.key().name(), new ValueHolder<>(value.key(), value.supplier(), value.consumer()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nonnull
    private static Value setupInstanceValue(Field field, GuiValue val) {
        Value value;
        String name;
        if (val.name().isEmpty()) {
            name = field.getName();
        } else {
            name = val.name();
        }
        if (field.getType().isEnum()) {
            if (NamedEnum.class.isAssignableFrom(field.getType())) {
                value = Value.createEnum(name, getEnumFields(field.getType().asSubclass(NamedEnum.class)), te -> {
                    try {
                        return (NamedEnum) FieldUtils.readField(field, te, true);
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException("Problem accessing field '" + name + "'!");
                    }
                }, (te, v) -> {
                    try {
                        FieldUtils.writeField(field, te, v, true);
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException("Problem accessing field '" + name + "'!");
                    }
                });
            } else {
                throw new RuntimeException("Field " + field.getName() + " is an enum but the enum doesn't extend NamedEnum!");
            }
        } else {
            Type type = guessType(field);
            value = Value.create(name, type, te -> {
                try {
                    return FieldUtils.readField(field, te, true);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Problem accessing field '" + name + "'!");
                }
            }, (te, v) -> {
                try {
                    FieldUtils.writeField(field, te, v, true);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Problem accessing field '" + name + "'!");
                }
                te.setChanged();
            });
        }
        return value;
    }

    private static NamedEnum[] getEnumFields(Class<? extends NamedEnum> clazz) {
        Field[] fields = FieldUtils.getAllFields(clazz);
        return Arrays.stream(fields).filter(Field::isEnumConstant).map(f -> {
            try {
                return (NamedEnum) f.get(null);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Problem getting enum field '" + f.getName() + "'!");
            }
        }).<NamedEnum>toArray(NamedEnum[]::new);

    }

    private static Type guessType(Field field) {
        if (field.getType().isPrimitive()) {
            if (field.getType() == int.class || field.getType() == Integer.class) {
                return Type.INTEGER;
            }
            if (field.getType() == short.class || field.getType() == Short.class) {
                return Type.INTEGER;
            }
            if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                return Type.BOOLEAN;
            }
            if (field.getType() == float.class || field.getType() == Float.class) {
                return Type.FLOAT;
            }
            if (field.getType() == double.class || field.getType() == Double.class) {
                return Type.DOUBLE;
            }
        }
        if (field.getType() == String.class) {
            return Type.STRING;
        }
        throw new RuntimeException("Can't guess type for field " + field.getName() + "!");
    }

    @Nonnull
    private static ISerializer getSerializer(ServerCommand serverCommand) throws IllegalAccessException {
        if (serverCommand.type() == Integer.class) {
            return new ISerializer.IntegerSerializer();
        } else if (serverCommand.type() == String.class) {
            return new ISerializer.StringSerializer();
        } else if (serverCommand.type() == BlockPos.class) {
            return new ISerializer.BlockPosSerializer();
        } else if (serverCommand.type() == ItemStack.class) {
            return new ISerializer.ItemStackSerializer();
        } else if (serverCommand.type() == FluidStack.class) {
            return new ISerializer.FluidStackSerializer();
        }
        Class<? extends ISerializer> serializer = serverCommand.serializer();
        try {
            return serializer.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException("Can't instantiate serializer!", e);
        }
    }
}
