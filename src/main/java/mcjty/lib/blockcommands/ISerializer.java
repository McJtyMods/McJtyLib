package mcjty.lib.blockcommands;

import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Implement this interface in a small helper object that knows how to serialize something. Used
 * for registered ListCommands
 */
public interface ISerializer<T> {
    Function<FriendlyByteBuf, T> getDeserializer();

    BiConsumer<FriendlyByteBuf, T> getSerializer();

    public static class IntegerSerializer implements ISerializer<Integer> {
        @Override
        public Function<FriendlyByteBuf, Integer> getDeserializer() {
            return FriendlyByteBuf::readInt;
        }

        @Override
        public BiConsumer<FriendlyByteBuf, Integer> getSerializer() {
            return FriendlyByteBuf::writeInt;
        }
    }

    public static class StringSerializer implements ISerializer<String> {
        @Override
        public Function<FriendlyByteBuf, String> getDeserializer() {
            return buf -> buf.readUtf(32767);
        }

        @Override
        public BiConsumer<FriendlyByteBuf, String> getSerializer() {
            return FriendlyByteBuf::writeUtf;
        }
    }

    public static class BlockPosSerializer implements ISerializer<BlockPos> {
        @Override
        public Function<FriendlyByteBuf, BlockPos> getDeserializer() {
            return FriendlyByteBuf::readBlockPos;
        }

        @Override
        public BiConsumer<FriendlyByteBuf, BlockPos> getSerializer() {
            return FriendlyByteBuf::writeBlockPos;
        }
    }

    public static class ItemStackSerializer implements ISerializer<ItemStack> {
        @Override
        public Function<FriendlyByteBuf, ItemStack> getDeserializer() {
            return FriendlyByteBuf::readItem;
        }

        @Override
        public BiConsumer<FriendlyByteBuf, ItemStack> getSerializer() {
            return FriendlyByteBuf::writeItem;
        }
    }

    public static class FluidStackSerializer implements ISerializer<FluidStack> {
        @Override
        public Function<FriendlyByteBuf, FluidStack> getDeserializer() {
            return FriendlyByteBuf::readFluidStack;
        }

        @Override
        public BiConsumer<FriendlyByteBuf, FluidStack> getSerializer() {
            return FriendlyByteBuf::writeFluidStack;
        }
    }
}
