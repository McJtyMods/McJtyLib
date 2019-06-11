package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.varia.Logging;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
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
        fluidStack.write(nbt);
        try {
            buf.writeCompoundTag(nbt);
        } catch (RuntimeException e) {
            Logging.logError("Error writing fluid stack", e);
        }
    }

    public static CompoundNBT readTag(ByteBuf dataIn) {
        PacketBuffer buf = new PacketBuffer(dataIn);
        return buf.readCompoundTag();
    }

    public static void writeTag(ByteBuf dataOut, CompoundNBT tag) {
        PacketBuffer buf = new PacketBuffer(dataOut);
        try {
            buf.writeCompoundTag(tag);
        } catch (RuntimeException e) {
            Logging.logError("Error writing tag", e);
        }
    }

    /// This function supports itemstacks with more then 64 items.
    public static ItemStack readItemStack(ByteBuf dataIn) {
        PacketBuffer buf = new PacketBuffer(dataIn);
        CompoundNBT nbt = buf.readCompoundTag();
        ItemStack stack = ItemStack.read(nbt);
        int amount = buf.readInt();
        if (amount <= 0) {
            stack.setCount(0);
        } else {
            stack.setCount(amount);
        }
        return stack;
    }

    /// This function supports itemstacks with more then 64 items.
    public static void writeItemStack(ByteBuf dataOut, @Nonnull ItemStack itemStack) {
        PacketBuffer buf = new PacketBuffer(dataOut);
        CompoundNBT nbt = new CompoundNBT();
        itemStack.write(nbt);
        try {
            buf.writeCompoundTag(nbt);
            buf.writeInt(itemStack.getCount());
        } catch (RuntimeException e) {
            Logging.logError("Error writing item stack", e);
        }
    }

    public static String readString(ByteBuf dataIn) {
        int s = dataIn.readInt();
        if (s == -1) {
            return null;
        }
        if (s == 0) {
            return "";
        }
        byte[] dst = new byte[s];
        dataIn.readBytes(dst);
        return new String(dst);
    }

    public static void writeString(ByteBuf dataOut, String str) {
        if (str == null) {
            dataOut.writeInt(-1);
            return;
        }
        byte[] bytes = str.getBytes();
        dataOut.writeInt(bytes.length);
        if (bytes.length > 0) {
            dataOut.writeBytes(bytes);
        }
    }

    public static String readStringUTF8(ByteBuf dataIn) {
        int s = dataIn.readInt();
        if (s == -1) {
            return null;
        }
        if (s == 0) {
            return "";
        }
        byte[] dst = new byte[s];
        dataIn.readBytes(dst);
        return new String(dst, java.nio.charset.StandardCharsets.UTF_8);
    }

    public static void writeStringUTF8(ByteBuf dataOut, String str) {
        if (str == null) {
            dataOut.writeInt(-1);
            return;
        }
        byte[] bytes = str.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        dataOut.writeInt(bytes.length);
        if (bytes.length > 0) {
            dataOut.writeBytes(bytes);
        }
    }

    public static void writeStringList(ByteBuf dataOut, @Nonnull List<String> list) {
        dataOut.writeInt(list.size());
        list.stream().forEach(s -> writeStringUTF8(dataOut, s));
    }

    @Nonnull
    public static List<String> readStringList(ByteBuf dataIn) {
        int size = dataIn.readInt();
        List<String> list = new ArrayList<>(size);
        for (int i = 0 ; i < size ; i++) {
            list.add(readStringUTF8(dataIn));
        }
        return list;
    }


    public static BlockPos readPos(ByteBuf dataIn) {
        if (GeneralConfig.tallChunkFriendly) {
            return new BlockPos(dataIn.readInt(), dataIn.readInt(), dataIn.readInt());
        } else {
            return BlockPos.fromLong(dataIn.readLong());
        }
    }

    public static void writePos(ByteBuf dataOut, BlockPos pos) {
        if (GeneralConfig.tallChunkFriendly) {
            dataOut.writeInt(pos.getX());
            dataOut.writeInt(pos.getY());
            dataOut.writeInt(pos.getZ());
        } else {
            dataOut.writeLong(pos.toLong());
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

    public static void writeFloat(ByteBuf buf, Float f) {
        if (f != null) {
            buf.writeBoolean(true);
            buf.writeFloat(f);
        } else {
            buf.writeBoolean(false);
        }
    }

    public static Float readFloat(ByteBuf buf) {
        if (buf.readBoolean()) {
            return buf.readFloat();
        } else {
            return null;
        }
    }

    @Nullable
    public static List<BlockPos> readPosList(ByteBuf buf) {
        List<BlockPos> list = null;
        int size = buf.readInt();
        if (size != -1) {
            list = new ArrayList<>(size);
            for (int i = 0 ; i < size ; i++) {
                BlockPos item = readPos(buf);
                list.add(item);
            }
        }
        return list;
    }

    public static void writePosList(ByteBuf buf, @Nullable List<BlockPos> list) {
        if (list == null) {
            buf.writeInt(-1);
        } else {
            buf.writeInt(list.size());
            for (BlockPos item : list) {
                writePos(buf, item);
            }
        }
    }

    @Nonnull
    public static List<ItemStack> readItemStackList(ByteBuf buf) {
        int size = buf.readInt();
        List<ItemStack> outputs = new ArrayList<>(size);
        for (int i = 0 ; i < size ; i++) {
            outputs.add(NetworkTools.readItemStack(buf));
        }
        return outputs;
    }

    public static void writeItemStackList(ByteBuf buf, @Nonnull List<ItemStack> outputs) {
        buf.writeInt(outputs.size());
        for (ItemStack output : outputs) {
            NetworkTools.writeItemStack(buf, output);
        }
    }
}
