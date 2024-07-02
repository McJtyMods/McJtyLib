package mcjty.lib.blockcommands;

import mcjty.lib.network.NetworkTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
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
    Function<RegistryFriendlyByteBuf, T> getDeserializer();

    BiConsumer<RegistryFriendlyByteBuf, T> getSerializer();

    public static class IntegerSerializer implements ISerializer<Integer> {
        @Override
        public Function<RegistryFriendlyByteBuf, Integer> getDeserializer() {
            return FriendlyByteBuf::readInt;
        }

        @Override
        public BiConsumer<RegistryFriendlyByteBuf, Integer> getSerializer() {
            return FriendlyByteBuf::writeInt;
        }
    }

    public static class StringSerializer implements ISerializer<String> {
        @Override
        public Function<RegistryFriendlyByteBuf, String> getDeserializer() {
            return buf -> buf.readUtf(32767);
        }

        @Override
        public BiConsumer<RegistryFriendlyByteBuf, String> getSerializer() {
            return FriendlyByteBuf::writeUtf;
        }
    }

    public static class BlockPosSerializer implements ISerializer<BlockPos> {
        @Override
        public Function<RegistryFriendlyByteBuf, BlockPos> getDeserializer() {
            return buf -> buf.readBlockPos();
        }

        @Override
        public BiConsumer<RegistryFriendlyByteBuf, BlockPos> getSerializer() {
            return (buf, pos) -> buf.writeBlockPos(pos);
        }
    }

    public static class ItemStackSerializer implements ISerializer<ItemStack> {
        @Override
        public Function<RegistryFriendlyByteBuf, ItemStack> getDeserializer() {
            return buf -> NetworkTools.readItemStack(buf);
        }

        @Override
        public BiConsumer<RegistryFriendlyByteBuf, ItemStack> getSerializer() {
            return (buf, item) -> NetworkTools.writeItemStack(buf, item);
        }
    }

    public static class FluidStackSerializer implements ISerializer<FluidStack> {
        @Override
        public Function<RegistryFriendlyByteBuf, FluidStack> getDeserializer() {
            return buf -> NetworkTools.readFluidStack(buf);
        }

        @Override
        public BiConsumer<RegistryFriendlyByteBuf, FluidStack> getSerializer() {
            return (buf, fluid) -> NetworkTools.writeFluidStack(buf, fluid);
        }
    }
}
