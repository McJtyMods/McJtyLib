package mcjty.lib.varia;


import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class GlobalCoordinate {
    private final BlockPos coordinate;
    private final DimensionType dimension;

    public GlobalCoordinate(BlockPos coordinate, DimensionType dimension) {
        this.coordinate = coordinate;
        this.dimension = dimension;
    }

    public BlockPos getCoordinate() {
        return coordinate;
    }

    public DimensionType getDimension() {
        return dimension;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GlobalCoordinate that = (GlobalCoordinate) o;

        if (!dimension.equals(that.dimension)) return false;
        return coordinate != null ? coordinate.equals(that.coordinate) : that.coordinate == null;

    }

    @Override
    public int hashCode() {
        int result = coordinate != null ? coordinate.hashCode() : 0;
        result = 31 * result + dimension.getId();
        return result;
    }

    @Override
    public String toString() {
        return BlockPosTools.toString(coordinate) + " (" + WorldTools.getDimensionName(dimension) + ")";
    }
}

