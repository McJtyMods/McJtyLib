package mcjty.lib.varia;

import mcjty.lib.blocks.BaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;

import static net.minecraft.core.Direction.*;

public class OrientationTools {

    public static final Direction[] DIRECTION_VALUES = Direction.values();
    public static final Direction[] HORIZONTAL_DIRECTION_VALUES = new Direction[]{NORTH, SOUTH, WEST, EAST};

    public static Direction rotateAround(Direction input, Direction.Axis axis) {
        switch (axis) {
            case X -> {
                if (input != WEST && input != EAST) {
                    return rotateX(input);
                }
                return input;
            }
            case Y -> {
                if (input != UP && input != DOWN) {
                    return input.getClockWise();
                }
                return input;
            }
            case Z -> {
                if (input != NORTH && input != SOUTH) {
                    return rotateZ(input);
                }
                return input;
            }
            default -> throw new IllegalStateException("Unable to get CW facing for axis " + axis);
        }
    }

    private static Direction rotateX(Direction input) {
        return switch (input) {
            case NORTH -> DOWN;
            case SOUTH -> UP;
            case UP -> NORTH;
            case DOWN -> SOUTH;
            case EAST, WEST -> throw new IllegalStateException("Unable to get X-rotated facing of " + input);
        };
    }

    /**
     * Rotate this Facing around the Z axis (EAST => DOWN => WEST => UP => EAST)
     */
    private static Direction rotateZ(Direction input) {
        return switch (input) {
            case EAST -> DOWN;
            case WEST -> UP;
            case UP -> EAST;
            case DOWN -> WEST;
            case SOUTH, NORTH -> throw new IllegalStateException("Unable to get Z-rotated facing of " + input);
        };
    }


    public static Direction getOrientationHoriz(BlockState state) {
        return state.getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    public static Direction getOrientation(BlockState state) {
        return ((BaseBlock) state.getBlock()).getFrontDirection(state);
    }

    public static Direction determineOrientation(BlockPos pos, LivingEntity entity) {
        return determineOrientation(pos.getX(), pos.getY(), pos.getZ(), entity);
    }

    public static Direction determineOrientation(int x, int y, int z, LivingEntity entity) {
        if (Mth.abs((float) entity.getX() - x) < 2.0F && Mth.abs((float) entity.getZ() - z) < 2.0F) {
            double d0 = entity.getY() + 1.82D - entity.getPassengerRidingPosition(entity).y;    // @todo 1.21 check

            if (d0 - y > 2.0D) {
                return Direction.UP;
            }

            if (y - d0 > 0.0D) {
                return DOWN;
            }
        }
        int i = (int) ((entity.getYRot() * 4.0F / 360.0F) + 0.5D);
        int l = ((entity.getYRot()  * 4.0F / 360.0F) + 0.5D < i ? i - 1 : i) & 3;
        if (l == 0) {
            return Direction.NORTH;
        } else if (l == 1) {
            return Direction.EAST;
        } else if (l == 2) {
            return SOUTH;
        } else {
            return Direction.WEST;
        }
    }

    public static Direction determineOrientationHoriz(LivingEntity MobEntity) {
        int i = (int) ((MobEntity.getYRot() * 4.0F / 360.0F) + 0.5D);
        int l = ((MobEntity.getYRot()  * 4.0F / 360.0F) + 0.5D < i ? i - 1 : i) & 3;
        return switch (l) {
            case 0 -> Direction.NORTH;
            case 1 -> Direction.EAST;
            case 2 -> SOUTH;
            default -> Direction.WEST;
        };
    }

    public static Direction getTopDirection(Direction rotation) {
        return switch (rotation) {
            case DOWN -> SOUTH;
            case UP -> Direction.NORTH;
            default -> Direction.UP;
        };
    }

    public static Direction getBottomDirection(Direction rotation) {
        return switch (rotation) {
            case DOWN -> Direction.NORTH;
            case UP -> SOUTH;
            default -> DOWN;
        };
    }

    public static Direction getFacingFromEntity(BlockPos clickedBlock, @Nullable Entity entityIn) {
        if (entityIn == null) {
            return UP;
        }
        if (Mth.abs((float) entityIn.getX() - clickedBlock.getX()) < 2.0F && Mth.abs((float) entityIn.getZ() - clickedBlock.getZ()) < 2.0F) {
            double d0 = entityIn.getY() + entityIn.getEyeHeight();

            if (d0 - clickedBlock.getY() > 2.0D) {
                return UP;
            }

            if (clickedBlock.getY() - d0 > 0.0D) {
                return DOWN;
            }
        }

        return entityIn.getDirection().getOpposite();
    }
}
