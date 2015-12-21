package mcjty.lib.varia;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.ByteBufConverter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@SuppressWarnings("deprecation")
@Deprecated
public class Coordinate extends BlockPos implements ByteBufConverter {
    @Deprecated
    public static final Coordinate INVALID = new Coordinate(-1, -1, -1);

    @Deprecated
    public Coordinate(ByteBuf buf) {
        this(buf.readInt(), buf.readInt(), buf.readInt());
    }

    @Deprecated
    public Coordinate(BlockPos pos){
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    @Deprecated
    public Coordinate(int x, int y, int z) {
        super(x, y, z);
    }

    @Deprecated
    public float squaredDistance(BlockPos c) {
        return (c.getX()-getX()) * (c.getX()-getX()) + (c.getY()-getY()) * (c.getY()-getY()) + (c.getZ()-getZ()) * (c.getZ()-getZ());
    }

    @Deprecated
    public float squaredDistance(int x1, int y1, int z1) {
        return (x1-getX()) * (x1-getX()) + (y1-getY()) * (y1-getY()) + (z1-getZ()) * (z1-getZ());
    }

    @Deprecated
    public static int area(BlockPos c1, BlockPos c2) {
        return (c2.getX()-c1.getX()+1) * (c2.getY()-c1.getY()+1) * (c2.getZ()-c1.getZ()+1);
    }

    @Deprecated
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(getX());
        buf.writeInt(getY());
        buf.writeInt(getZ());
    }

    @Deprecated
    public boolean isValid() {
        return getY() >= 0;
    }

    @Deprecated
    public BlockPos addDirection(EnumFacing direction) {
        return this.offset(direction);
    }

    @Deprecated
    public static BlockPos center(BlockPos c1, BlockPos c2) {
        return new BlockPos((c1.getX() + c2.getX()) / 2, (c1.getY() + c2.getY()) / 2, (c1.getZ() + c2.getZ()) / 2);
    }

    @Deprecated
    public static BlockPos readFromNBT(NBTTagCompound tagCompound, String tagName) {
        int[] array = tagCompound.getIntArray(tagName);
        if (array.length == 0) {
            return null;
        } else {
            return new Coordinate(array[0], array[1], array[2]);
        }
    }

    @Deprecated
    public static void writeToNBT(NBTTagCompound tagCompound, String tagName, BlockPos coordinate) {
        if (coordinate == null) {
            tagCompound.setIntArray(tagName, new int[] { });
        } else {
            tagCompound.setIntArray(tagName, new int[] { coordinate.getX(), coordinate.getY(), coordinate.getZ() });
        }
    }

    @Deprecated
    public static NBTTagCompound writeToNBT(BlockPos coordinate) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        writeToNBT(tagCompound, "c", coordinate);
        return tagCompound;
    }

}
