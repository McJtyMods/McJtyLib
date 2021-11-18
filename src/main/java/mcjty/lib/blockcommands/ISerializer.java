package mcjty.lib.blockcommands;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Implement this interface in a small helper object that knows how to serialize something. Used
 * for registered ListCommands
 */
public interface ISerializer<T> {
    Function<PacketBuffer, T> getDeserializer();

    BiConsumer<PacketBuffer, T> getSerializer();

    public static class IntegerSerializer implements ISerializer<Integer> {
        @Override
        public Function<PacketBuffer, Integer> getDeserializer() {
            return PacketBuffer::readInt;
        }

        @Override
        public BiConsumer<PacketBuffer, Integer> getSerializer() {
            return PacketBuffer::writeInt;
        }
    }

    public static class StringSerializer implements ISerializer<String> {
        @Override
        public Function<PacketBuffer, String> getDeserializer() {
            return buf -> buf.readUtf(32767);
        }

        @Override
        public BiConsumer<PacketBuffer, String> getSerializer() {
            return PacketBuffer::writeUtf;
        }
    }

    public static class BlockPosSerializer implements ISerializer<BlockPos> {
        @Override
        public Function<PacketBuffer, BlockPos> getDeserializer() {
            return PacketBuffer::readBlockPos;
        }

        @Override
        public BiConsumer<PacketBuffer, BlockPos> getSerializer() {
            return PacketBuffer::writeBlockPos;
        }
    }

    public static class ItemStackSerializer implements ISerializer<ItemStack> {
        @Override
        public Function<PacketBuffer, ItemStack> getDeserializer() {
            return PacketBuffer::readItem;
        }

        @Override
        public BiConsumer<PacketBuffer, ItemStack> getSerializer() {
            return PacketBuffer::writeItem;
        }
    }

    public static class FluidStackSerializer implements ISerializer<FluidStack> {
        @Override
        public Function<PacketBuffer, FluidStack> getDeserializer() {
            return PacketBuffer::readFluidStack;
        }

        @Override
        public BiConsumer<PacketBuffer, FluidStack> getSerializer() {
            return PacketBuffer::writeFluidStack;
        }
    }
}
