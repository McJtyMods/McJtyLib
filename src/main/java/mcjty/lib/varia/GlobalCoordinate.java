package mcjty.lib.varia;


import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GlobalCoordinate {
    private final BlockPos coordinate;
    private final DimensionId dimension;

    public GlobalCoordinate(BlockPos coordinate, DimensionId dimension) {
        this.coordinate = coordinate;
        this.dimension = dimension;
    }

    public GlobalCoordinate(BlockPos coordinate, World world) {
        this.coordinate = coordinate;
        this.dimension = DimensionId.fromWorld(world);
    }


    public BlockPos getCoordinate() {
        return coordinate;
    }

    public DimensionId getDimension() {
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
        result = 31 * result + dimension.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return BlockPosTools.toString(coordinate) + " (" + dimension.getName() + ")";
    }
}

