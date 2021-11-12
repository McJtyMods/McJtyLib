package mcjty.lib.varia;

import mcjty.lib.api.container.IContainerDataListener;
import mcjty.lib.bindings.IValue;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Key;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Tools {

    /**
     * A data listener that listens to the values of the given tile entity. Use this
     * in a GenericContainer (using dataListener) to sync data for your gui
     */
    public static IContainerDataListener values(ResourceLocation id, GenericTileEntity te) {
        return new IContainerDataListener() {

            private Map<Key, Object> oldValues = new HashMap<>();

            @Override
            public ResourceLocation getId() {
                return id;
            }

            private void copyToOld() {
                oldValues.clear();
                for (IValue<?> value : te.getValues()) {
                    Object v = value.getter().get();
                    oldValues.put(value.getKey(), v);
                }
            }

            @Override
            public boolean isDirtyAndClear() {
                for (IValue<?> value : te.getValues()) {
                    Object v = value.getter().get();
                    Key<?> key = value.getKey();
                    if (!oldValues.containsKey(key) || !Objects.equals(oldValues.get(key), v)) {
                        // Dirty
                        copyToOld();
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void toBytes(PacketBuffer buf) {
                for (IValue<?> value : te.getValues()) {
                    Object v = value.getter().get();
                    value.getKey().getType().serialize(buf, v);
                }
            }

            @Override
            public void readBuf(PacketBuffer buf) {
                for (IValue value : te.getValues()) {
                    value.getKey().getType().deserialize(buf, value);
                }
            }
        };
    }

    /**
     * A data listener for a string. Use this
     * in a GenericContainer (using dataListener) to sync data for your gui
     */
    public static IContainerDataListener string(ResourceLocation id, Supplier<String> getter, Consumer<String> setter) {
        return new IContainerDataListener() {

            private String oldString = null;

            @Override
            public ResourceLocation getId() {
                return id;
            }

            @Override
            public boolean isDirtyAndClear() {
                String newValue = getter.get();
                if (!Objects.equals(newValue, oldString)) {
                    oldString = newValue;
                    return true;
                }
                return false;
            }

            @Override
            public void toBytes(PacketBuffer buf) {
                buf.writeUtf(getter.get());
            }

            @Override
            public void readBuf(PacketBuffer buf) {
                setter.accept(buf.readUtf(32767));
            }
        };
    }

    public static IntReferenceHolder holder(Supplier<Integer> getter, Consumer<Integer> setter) {
        return new IntReferenceHolder() {
            @Override
            public int get() {
                return getter.get();
            }

            @Override
            public void set(int v) {
                setter.accept(v);
            }
        };
    }

    public static IntReferenceHolder bool(Supplier<Boolean> getter, Consumer<Boolean> setter) {
        return new IntReferenceHolder() {
            @Override
            public int get() {
                return getter.get() ? 1 : 0;
            }

            @Override
            public void set(int v) {
                setter.accept(v != 0);
            }
        };
    }

    public static String getModid(ItemStack stack) {
        if (!stack.isEmpty()) {
            return stack.getItem().getRegistryName().getNamespace();
        } else {
            return "";
        }
    }

    public static String getModName(IForgeRegistryEntry<?> entry) {
        ResourceLocation registryName = entry.getRegistryName();
        String modId = registryName == null ? "minecraft" : registryName.getNamespace();
        return ModList.get().getModContainerById(modId)
                .map(mod -> mod.getModInfo().getDisplayName())
                .orElse(StringUtils.capitalize(modId));
    }

    public static <INPUT extends BASE, BASE> void safeConsume(BASE o, Consumer<INPUT> consumer, String error) {
        try {
            consumer.accept((INPUT) o);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(error, e);
        }
    }

    public static <INPUT extends BASE, BASE> void safeConsume(BASE o, Consumer<INPUT> consumer) {
        try {
            consumer.accept((INPUT) o);
        } catch (ClassCastException ignore) {
        }
    }

    public static <INPUT extends BASE, BASE, RET> RET safeMap(BASE o, Function<INPUT, RET> consumer, String error) {
        try {
            return consumer.apply((INPUT) o);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(error, e);
        }
    }

    public static String getReadableName(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return getReadableName(state.getBlock().getCloneItemStack(world, pos, state));
    }

    public static String getReadableName(ItemStack stack) {
        return stack.getHoverName().getString() /* was getFormattedText() */;
    }

    @Nullable
    public static BlockState placeStackAt(PlayerEntity player, ItemStack blockStack, World world, BlockPos pos, @Nullable BlockState origState) {
        BlockRayTraceResult trace = new BlockRayTraceResult(new Vector3d(0, 0, 0), Direction.UP, pos, false);
        BlockItemUseContext context = new BlockItemUseContext(new ItemUseContext(player, Hand.MAIN_HAND, trace));
        if (blockStack.getItem() instanceof BlockItem) {
            BlockItem itemBlock = (BlockItem) blockStack.getItem();
            if (origState == null) {
                origState = itemBlock.getBlock().getStateForPlacement(context);
                if (origState == null) {
                    // Cannot place!
                    return null;
                }
            }
            if (itemBlock.place(context).consumesAction()) {
                blockStack.shrink(1);
            }
            return origState;
        } else {
            player.setItemInHand(Hand.MAIN_HAND, blockStack);
            player.setPos(pos.getX()+.5, pos.getY()+1.5, pos.getZ()+.5);
            blockStack.getItem().useOn(context);
            return world.getBlockState(pos);
        }
    }
}
