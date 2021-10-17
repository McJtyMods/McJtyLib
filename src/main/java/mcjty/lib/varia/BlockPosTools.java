package mcjty.lib.varia;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;

public class BlockPosTools {

    public static final BlockPos INVALID = new BlockPos(-1, -1, -1);

    public static BlockPos read(CompoundNBT tagCompound, String tagName) {
        int[] array = tagCompound.getIntArray(tagName);
        if (array.length == 0) {
            return null;
        } else {
            return new BlockPos(array[0], array[1], array[2]);
        }
    }

    public static void write(CompoundNBT tagCompound, String tagName, BlockPos coordinate) {
        if (coordinate == null) {
            tagCompound.putIntArray(tagName, new int[] { });
        } else {
            tagCompound.putIntArray(tagName, new int[] { coordinate.getX(), coordinate.getY(), coordinate.getZ() });
        }
    }

    public static CompoundNBT write(BlockPos coordinate) {
        CompoundNBT tagCompound = new CompoundNBT();
        write(tagCompound, "c", coordinate);
        return tagCompound;
    }

    public static String toString(BlockPos pos) {
        return pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    public static String toString(GlobalPos pos) {
        return BlockPosTools.toString(pos.pos()) + " (" + pos.dimension().location().getPath() + ")";
    }
}
