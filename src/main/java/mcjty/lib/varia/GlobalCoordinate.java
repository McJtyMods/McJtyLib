package mcjty.lib.varia;


import net.minecraft.util.math.BlockPos;

public class GlobalCoordinate {
    private final BlockPos coordinate;
    private final int dimension;

    public GlobalCoordinate(BlockPos coordinate, int dimension) {
        this.coordinate = coordinate;
        this.dimension = dimension;
    }

    public BlockPos getCoordinate() {
        return coordinate;
    }

    public int getDimension() {
        return dimension;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GlobalCoordinate that = (GlobalCoordinate) o;

        if (dimension != that.dimension) return false;
        return coordinate != null ? coordinate.equals(that.coordinate) : that.coordinate == null;

    }

    @Override
    public int hashCode() {
        int result = coordinate != null ? coordinate.hashCode() : 0;
        result = 31 * result + dimension;
        return result;
    }
}

