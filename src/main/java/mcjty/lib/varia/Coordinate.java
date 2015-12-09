package mcjty.lib.varia;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.ByteBufConverter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@SuppressWarnings("deprecation")
@Deprecated
public class Coordinate implements ByteBufConverter {
    private final int x;
    private final int y;
    private final int z;

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
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Deprecated
    public float squaredDistance(Coordinate c) {
        return (c.x-x) * (c.x-x) + (c.y-y) * (c.y-y) + (c.z-z) * (c.z-z);
    }

    @Deprecated
    public float squaredDistance(int x1, int y1, int z1) {
        return (x1-x) * (x1-x) + (y1-y) * (y1-y) + (z1-z) * (z1-z);
    }

    @Deprecated
    public static int area(Coordinate c1, Coordinate c2) {
        return (c2.getX()-c1.getX()+1) * (c2.getY()-c1.getY()+1) * (c2.getZ()-c1.getZ()+1);
    }

    @Deprecated
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Deprecated
    public boolean isValid() {
        return y >= 0;
    }

    @Deprecated
    public Coordinate addDirection(EnumFacing direction) {
        return new Coordinate(x + direction.getFrontOffsetX(), y + direction.getFrontOffsetY(), z + direction.getFrontOffsetZ());
    }

    @Deprecated
    public static Coordinate center(Coordinate c1, Coordinate c2) {
        return new Coordinate((c1.getX() + c2.getX()) / 2, (c1.getY() + c2.getY()) / 2, (c1.getZ() + c2.getZ()) / 2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Coordinate that = (Coordinate) o;

        if (x != that.x) {
            return false;
        }
        if (y != that.y) {
            return false;
        }
        if (z != that.z) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    @Deprecated
    public int getX() {
        return x;
    }

    @Deprecated
    public int getY() {
        return y;
    }

    @Deprecated
    public int getZ() {
        return z;
    }

    @Override
    public String toString() {
        return x + "," + y + "," + z;
    }

    @Deprecated
    public static Coordinate readFromNBT(NBTTagCompound tagCompound, String tagName) {
        int[] array = tagCompound.getIntArray(tagName);
        if (array.length == 0) {
            return null;
        } else {
            return new Coordinate(array[0], array[1], array[2]);
        }
    }

    @Deprecated
    public static void writeToNBT(NBTTagCompound tagCompound, String tagName, Coordinate coordinate) {
        if (coordinate == null) {
            tagCompound.setIntArray(tagName, new int[] { });
        } else {
            tagCompound.setIntArray(tagName, new int[] { coordinate.getX(), coordinate.getY(), coordinate.getZ() });
        }
    }

    @Deprecated
    public static NBTTagCompound writeToNBT(Coordinate coordinate) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        writeToNBT(tagCompound, "c", coordinate);
        return tagCompound;
    }

}
