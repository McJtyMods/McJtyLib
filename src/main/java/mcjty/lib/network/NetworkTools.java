package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.McJtyLib;
import mcjty.lib.varia.Logging;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NetworkTools {

    public static FluidStack readFluidStack(ByteBuf dataIn) {
        PacketBuffer buf = new PacketBuffer(dataIn);
        CompoundNBT nbt = buf.readCompoundTag();
        return FluidStack.loadFluidStackFromNBT(nbt);
    }

    public static void writeFluidStack(ByteBuf dataOut, FluidStack fluidStack) {
        PacketBuffer buf = new PacketBuffer(dataOut);
        CompoundNBT nbt = new CompoundNBT();
        fluidStack.writeToNBT(nbt);
        try {
            buf.writeCompoundTag(nbt);
        } catch (RuntimeException e) {
            Logging.logError("Error writing fluid stack", e);
        }
    }

    public static String readStringUTF8(PacketBuffer dataIn) {
        if (!dataIn.readBoolean()) {
            return null;
        }
        return dataIn.readString(32767);
    }

    public static void writeStringUTF8(PacketBuffer dataOut, String str) {
        if (str == null) {
            dataOut.writeBoolean(false);
            return;
        }
        dataOut.writeBoolean(true);
        dataOut.writeString(str);
    }

    public static void writeStringList(PacketBuffer dataOut, @Nonnull List<String> list) {
        dataOut.writeInt(list.size());
        list.stream().forEach(s -> writeStringUTF8(dataOut, s));
    }

    @Nonnull
    public static List<String> readStringList(PacketBuffer dataIn) {
        int size = dataIn.readInt();
        List<String> list = new ArrayList<>(size);
        for (int i = 0 ; i < size ; i++) {
            list.add(readStringUTF8(dataIn));
        }
        return list;
    }

    /// This function supports itemstacks with more then 64 items.
    public static ItemStack readItemStack(PacketBuffer buf) {
        CompoundNBT nbt = buf.readCompoundTag();
        ItemStack stack = ItemStack.read(nbt);
        stack.setCount(buf.readInt());
        return stack;
    }

    /// This function supports itemstacks with more then 64 items.
    public static void writeItemStack(PacketBuffer buf, ItemStack itemStack) {
        CompoundNBT nbt = new CompoundNBT();
        itemStack.write(nbt);
        try {
            buf.writeCompoundTag(nbt);
            buf.writeInt(itemStack.getCount());
        } catch (Exception e) {
            Logging.logError("Error", e);
        }
    }


    public static <T extends Enum<T>> void writeEnum(ByteBuf buf, T value, T nullValue) {
        if (value == null) {
            buf.writeInt(nullValue.ordinal());
        } else {
            buf.writeInt(value.ordinal());
        }
    }

    public static <T extends Enum<T>> T readEnum(ByteBuf buf, T[] values) {
        return values[buf.readInt()];
    }

    public static <T extends Enum<T>> void writeEnumCollection(ByteBuf buf, Collection<T> collection) {
        buf.writeInt(collection.size());
        for (T type : collection) {
            buf.writeInt(type.ordinal());
        }
    }

    public static <T extends Enum<T>> void readEnumCollection(ByteBuf buf, Collection<T> collection, T[] values) {
        collection.clear();
        int size = buf.readInt();
        for (int i = 0 ; i < size ; i++) {
            collection.add(values[buf.readInt()]);
        }
    }

    @Nonnull
    public static List<ItemStack> readItemStackList(PacketBuffer buf) {
        int size = buf.readInt();
        List<ItemStack> outputs = new ArrayList<>(size);
        for (int i = 0 ; i < size ; i++) {
            outputs.add(buf.readItemStack());
        }
        return outputs;
    }

    public static void writeItemStackList(PacketBuffer buf, @Nonnull List<ItemStack> outputs) {
        buf.writeInt(outputs.size());
        for (ItemStack output : outputs) {
            buf.writeItemStack(output);
        }
    }
}
