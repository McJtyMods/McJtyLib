package mcjty.lib.varia;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class BlockPosTools {

    public static final BlockPos INVALID = new BlockPos(-1, -1, -1);

    public static boolean isAdjacent(BlockPos c1, BlockPos c2) {
        int dx = Math.abs(c2.getX() - c1.getX())+1;
        int dy = Math.abs(c2.getY() - c1.getY())+1;
        int dz = Math.abs(c2.getZ() - c1.getZ())+1;
        return dx * dy * dz == 2;
    }

    public static int area(BlockPos c1, BlockPos c2) {
        return (c2.getX()-c1.getX()+1) * (c2.getY()-c1.getY()+1) * (c2.getZ()-c1.getZ()+1);
    }

    public static BlockPos center(BlockPos c1, BlockPos c2) {
        return new BlockPos((c1.getX() + c2.getX()) / 2, (c1.getY() + c2.getY()) / 2, (c1.getZ() + c2.getZ()) / 2);
    }

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

    public static ChunkPos getChunkCoordFromPos(BlockPos pos) {
        return new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
    }
}
