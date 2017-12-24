package mcjty.lib.varia;


import net.minecraft.util.math.BlockPos;

public class GlobalCoordinate extends BlockPos {
    private final int dimension;

    public GlobalCoordinate(BlockPos coordinate, int dimension) {
        super(coordinate);
        this.dimension = dimension;
    }

    public BlockPos getCoordinate() {
        return this;
    }

    public int getDimension() {
        return dimension;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GlobalCoordinate)) return false;
        if (!super.equals(o)) return false;

        GlobalCoordinate that = (GlobalCoordinate) o;

        return dimension == that.dimension;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + dimension;
        return result;
    }
}
