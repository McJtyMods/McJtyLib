package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.tools.ItemStackTools;
import mcjty.lib.tools.PacketBufferTools;
import mcjty.lib.varia.Logging;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

import java.io.IOException;
import java.util.Collection;

public class NetworkTools {

    public static FluidStack readFluidStack(ByteBuf dataIn) {
        PacketBuffer buf = new PacketBuffer(dataIn);
        try {
            NBTTagCompound nbt = PacketBufferTools.readCompoundTag(buf);
            return FluidStack.loadFluidStackFromNBT(nbt);
        } catch (IOException e) {
            Logging.logError("Error reading fluid stack", e);
        }
        return null;
    }

    public static void writeFluidStack(ByteBuf dataOut, FluidStack fluidStack) {
        PacketBuffer buf = new PacketBuffer(dataOut);
        NBTTagCompound nbt = new NBTTagCompound();
        fluidStack.writeToNBT(nbt);
        try {
            PacketBufferTools.writeCompoundTag(buf, nbt);
        } catch (Exception e) {
            Logging.logError("Error writing fluid stack", e);
        }
    }

    public static NBTTagCompound readTag(ByteBuf dataIn) {
        PacketBuffer buf = new PacketBuffer(dataIn);
        try {
            return PacketBufferTools.readCompoundTag(buf);
        } catch (IOException e) {
            Logging.logError("Error reading tag", e);
        }
        return null;
    }

    public static void writeTag(ByteBuf dataOut, NBTTagCompound tag) {
        PacketBuffer buf = new PacketBuffer(dataOut);
        try {
            PacketBufferTools.writeCompoundTag(buf, tag);
        } catch (Exception e) {
            Logging.logError("Error writing tag", e);
        }
    }

    /// This function supports itemstacks with more then 64 items.
    public static ItemStack readItemStack(ByteBuf dataIn) {
        PacketBuffer buf = new PacketBuffer(dataIn);
        try {
            NBTTagCompound nbt = PacketBufferTools.readCompoundTag(buf);
            ItemStack stack = ItemStackTools.loadFromNBT(nbt);
            ItemStackTools.setStackSize(stack, buf.readInt());
            return stack;
        } catch (IOException e) {
            Logging.logError("Error reading item stack", e);
        }
        return ItemStackTools.getEmptyStack();
    }

    /// This function supports itemstacks with more then 64 items.
    public static void writeItemStack(ByteBuf dataOut, ItemStack itemStack) {
        PacketBuffer buf = new PacketBuffer(dataOut);
        NBTTagCompound nbt = new NBTTagCompound();
        itemStack.writeToNBT(nbt);
        try {
            PacketBufferTools.writeCompoundTag(buf, nbt);
            buf.writeInt(ItemStackTools.getStackSize(itemStack));
        } catch (Exception e) {
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


    public static BlockPos readPos(ByteBuf dataIn) {
        return BlockPos.fromLong(dataIn.readLong());
    }

    public static void writePos(ByteBuf dataOut, BlockPos pos) {
        dataOut.writeLong(pos.toLong());
    }

    public static <T extends Enum> void writeEnum(ByteBuf buf, T value, T nullValue) {
        if (value == null) {
            buf.writeInt(nullValue.ordinal());
        } else {
            buf.writeInt(value.ordinal());
        }
    }

    public static <T extends Enum> T readEnum(ByteBuf buf, T[] values) {
        return values[buf.readInt()];
    }

    public static void writeEnumCollection(ByteBuf buf, Collection<? extends Enum> collection) {
        buf.writeInt(collection.size());
        for (Enum type : collection) {
            buf.writeInt(type.ordinal());
        }
    }

    public static <T extends Enum> void readEnumCollection(ByteBuf buf, Collection<T> collection, T[] values) {
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
}
