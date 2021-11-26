package mcjty.lib.tileentity;

import mcjty.lib.McJtyLib;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.bindings.Value;
import mcjty.lib.blockcommands.*;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.NamedEnum;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class AnnotationTools {

    static AnnotationHolder createAnnotationHolder(TileEntityType ttype, Class<? extends GenericTileEntity> clazz) {
        AnnotationHolder holder;
        holder = new AnnotationHolder();
        AnnotationHolder.annotations.put(ttype, holder);
        Field[] commandFields = FieldUtils.getFieldsWithAnnotation(clazz, ServerCommand.class);
        for (Field field : commandFields) {
            ServerCommand serverCommand = field.getAnnotation(ServerCommand.class);
            try {
                Object o = field.get(null);
                if (o instanceof Command) {
                    Command cmd = (Command) o;
                    holder.serverCommands.put(cmd.getName(), cmd.getCmd());
                } else if (o instanceof ResultCommand) {
                    ResultCommand cmd = (ResultCommand) o;
                    holder.serverCommandsWithResult.put(cmd.getName(), cmd.getCmd());
                    holder.clientCommands.put(cmd.getName(), cmd.getClientCommand());
                } else if (o instanceof ListCommand) {
                    ListCommand cmd = (ListCommand) o;
                    holder.serverCommandsWithListResult.put(cmd.getName(), cmd.getCmd());
                    holder.clientCommandsWithList.put(cmd.getName(), cmd.getClientCommand());
                    if (serverCommand.type() != void.class) {
                        ISerializer instance = getSerializer(serverCommand);
                        McJtyLib.registerListCommandInfo(cmd.getName(), serverCommand.type(), instance.getDeserializer(), instance.getSerializer());
                    }
                } else {
                    throw new IllegalStateException("Only use @ServerCommand with either a Command, a ListCommand or a ResultCommand!");
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

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
//                holder.valueMap.put(value.getKey().getName(), new ValueHolder<>(value.getKey(), te -> value.getSupplier().apply(te), (te, o) -> value.getConsumer().accept(te, o)));
                holder.valueMap.put(value.getKey().getName(), new ValueHolder<>(value.getKey(), value.getSupplier(), value.getConsumer()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        holder.valueMap.put(GenericTileEntity.VALUE_RSMODE.getName(), new ValueHolder<>(GenericTileEntity.VALUE_RSMODE, GenericTileEntity::getRSModeInt, GenericTileEntity::setRSModeInt));
        return holder;
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
