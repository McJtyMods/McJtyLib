package mcjty.lib.varia;

import mcjty.lib.blocks.BaseBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import static net.minecraft.util.Direction.*;

public class OrientationTools {

    public static Direction[] DIRECTION_VALUES = Direction.values();
    public static Direction[] HORIZONTAL_DIRECTION_VALUES = new Direction[]{NORTH, SOUTH, WEST, EAST};

    public static Direction rotateAround(Direction input, Direction.Axis axis) {
        switch (axis) {
            case X:
                if (input != WEST && input != EAST) {
                    return rotateX(input);
                }

                return input;
            case Y:
                if (input != UP && input != DOWN) {
                    return input.rotateY();
                }

                return input;
            case Z:
                if (input != NORTH && input != SOUTH) {
                    return rotateZ(input);
                }

                return input;
            default:
                throw new IllegalStateException("Unable to get CW facing for axis " + axis);
        }
    }

    private static Direction rotateX(Direction input) {
        switch(input) {
            case NORTH:
                return DOWN;
            case EAST:
            case WEST:
            default:
                throw new IllegalStateException("Unable to get X-rotated facing of " + input);
            case SOUTH:
                return UP;
            case UP:
                return NORTH;
            case DOWN:
                return SOUTH;
        }
    }

    /**
     * Rotate this Facing around the Z axis (EAST => DOWN => WEST => UP => EAST)
     */
    private static Direction rotateZ(Direction input) {
        switch(input) {
            case EAST:
                return DOWN;
            case SOUTH:
            default:
                throw new IllegalStateException("Unable to get Z-rotated facing of " + input);
            case WEST:
                return UP;
            case UP:
                return EAST;
            case DOWN:
                return WEST;
        }
    }


    public static Direction getOrientationHoriz(BlockState state) {
        return state.get(BlockStateProperties.HORIZONTAL_FACING);
    }

    public static Direction getOrientation(BlockState state) {
        return ((BaseBlock) state.getBlock()).getFrontDirection(state);
    }

    public static Direction determineOrientation(BlockPos pos, LivingEntity entity) {
        return determineOrientation(pos.getX(), pos.getY(), pos.getZ(), entity);
    }

    public static Direction determineOrientation(int x, int y, int z, LivingEntity entity) {
        if (MathHelper.abs((float) entity.getPosX() - x) < 2.0F && MathHelper.abs((float) entity.getPosZ() - z) < 2.0F) {
            double d0 = entity.getPosY() + 1.82D - entity.getYOffset();

            if (d0 - y > 2.0D) {
                return Direction.UP;
            }

            if (y - d0 > 0.0D) {
                return DOWN;
            }
        }
        int i = (int) ((entity.rotationYaw * 4.0F / 360.0F) + 0.5D);
        int l = ((entity.rotationYaw * 4.0F / 360.0F) + 0.5D < i ? i - 1 : i) & 3;
        return l == 0 ? Direction.NORTH : (l == 1 ? Direction.EAST : (l == 2 ? SOUTH : (l == 3 ? Direction.WEST : DOWN)));
    }

    public static Direction determineOrientationHoriz(LivingEntity MobEntity) {
        int i = (int) ((MobEntity.rotationYaw * 4.0F / 360.0F) + 0.5D);
        int l = ((MobEntity.rotationYaw * 4.0F / 360.0F) + 0.5D < i ? i - 1 : i) & 3;
        return l == 0 ? Direction.NORTH : (l == 1 ? Direction.EAST : (l == 2 ? SOUTH : (l == 3 ? Direction.WEST : DOWN)));
    }

    public static Direction getTopDirection(Direction rotation) {
        switch (rotation) {
            case DOWN:
                return SOUTH;
            case UP:
                return Direction.NORTH;
            default:
                return Direction.UP;
        }
    }

    public static Direction getBottomDirection(Direction rotation) {
        switch (rotation) {
            case DOWN:
                return Direction.NORTH;
            case UP:
                return SOUTH;
            default:
                return DOWN;
        }
    }

    public static Direction getFacingFromEntity(BlockPos clickedBlock, Entity entityIn) {
        if (MathHelper.abs((float) entityIn.getPosX() - clickedBlock.getX()) < 2.0F && MathHelper.abs((float) entityIn.getPosZ() - clickedBlock.getZ()) < 2.0F) {
            double d0 = entityIn.getPosY() + entityIn.getEyeHeight();

            if (d0 - clickedBlock.getY() > 2.0D) {
                return UP;
            }

            if (clickedBlock.getY() - d0 > 0.0D) {
                return DOWN;
            }
        }

        return entityIn.getHorizontalFacing().getOpposite();
    }
}
