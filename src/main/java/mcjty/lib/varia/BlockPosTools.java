package mcjty.lib.varia;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;

public class BlockPosTools {

    public static final BlockPos INVALID = new BlockPos(-1, -1, -1);

    public static BlockPos read(CompoundTag tagCompound, String tagName) {
        int[] array = tagCompound.getIntArray(tagName);
        if (array.length == 0) {
            return null;
        } else {
            return new BlockPos(array[0], array[1], array[2]);
        }
    }

    public static void write(CompoundTag tagCompound, String tagName, BlockPos coordinate) {
        if (coordinate == null) {
            tagCompound.putIntArray(tagName, new int[] { });
        } else {
            tagCompound.putIntArray(tagName, new int[] { coordinate.getX(), coordinate.getY(), coordinate.getZ() });
        }
    }

    public static CompoundTag write(BlockPos coordinate) {
        CompoundTag tagCompound = new CompoundTag();
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
