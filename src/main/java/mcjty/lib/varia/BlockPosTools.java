package mcjty.lib.varia;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class BlockPosTools {
    public static final BlockPos INVALID = new BlockPos(-1, -1, -1);

    public static int area(BlockPos c1, BlockPos c2) {
        return (c2.getX()-c1.getX()+1) * (c2.getY()-c1.getY()+1) * (c2.getZ()-c1.getZ()+1);
    }

    public static BlockPos center(BlockPos c1, BlockPos c2) {
        return new BlockPos((c1.getX() + c2.getX()) / 2, (c1.getY() + c2.getY()) / 2, (c1.getZ() + c2.getZ()) / 2);
    }

    public static BlockPos readFromNBT(NBTTagCompound tagCompound, String tagName) {
        int[] array = tagCompound.getIntArray(tagName);
        if (array.length == 0) {
            return null;
        } else {
            return new BlockPos(array[0], array[1], array[2]);
        }
    }

    public static void writeToNBT(NBTTagCompound tagCompound, String tagName, BlockPos coordinate) {
        if (coordinate == null) {
            tagCompound.setIntArray(tagName, new int[] { });
        } else {
            tagCompound.setIntArray(tagName, new int[] { coordinate.getX(), coordinate.getY(), coordinate.getZ() });
        }
    }

    public static NBTTagCompound writeToNBT(BlockPos coordinate) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        writeToNBT(tagCompound, "c", coordinate);
        return tagCompound;
    }

    public static String toString(BlockPos pos) {
        return pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }
}
