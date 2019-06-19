package mcjty.lib.varia;

import mcjty.lib.blocks.BaseBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static net.minecraft.util.Direction.*;

public class OrientationTools {

    // Use these flags if you want to support a single redstone signal and 3 bits for orientation.
    public static final int MASK_ORIENTATION = 0x7;
    public static final int MASK_REDSTONE = 0x8;
    // Use these flags if you want to support both redstone in and output and only 2 bits for orientation.
    public static final int MASK_ORIENTATION_HORIZONTAL = 0x3;          // Only two bits for orientation
    public static final int MASK_REDSTONE_IN = 0x8;                     // Redstone in
    public static final int MASK_REDSTONE_OUT = 0x4;                    // Redstone out
    public static final int MASK_STATE = 0xc;                           // If redstone is not used: state

    public static int setOrientation(int metadata, Direction orientation) {
        return (metadata & ~MASK_ORIENTATION) | orientation.ordinal();
    }

    public static Direction getOrientationHoriz(BlockState state) {
        return state.get(BlockStateProperties.HORIZONTAL_FACING);
    }

    public static int setOrientationHoriz(int metadata, Direction orientation) {
        return (metadata & ~MASK_ORIENTATION_HORIZONTAL) | getHorizOrientationMeta(orientation);
    }

    public static int getHorizOrientationMeta(Direction orientation) {
        return orientation.ordinal()-2;
    }

    public static Direction getOrientation(BlockState state) {
        return ((BaseBlock)state.getBlock()).getFrontDirection(state);
    }

    public static Direction determineOrientation(BlockPos pos, MobEntity MobEntity) {
        return determineOrientation(pos.getX(), pos.getY(), pos.getZ(), MobEntity);
    }

    public static Direction determineOrientation(int x, int y, int z, MobEntity MobEntity) {
        if (MathHelper.abs((float) MobEntity.posX - x) < 2.0F && MathHelper.abs((float) MobEntity.posZ - z) < 2.0F) {
            double d0 = MobEntity.posY + 1.82D - MobEntity.getYOffset();

            if (d0 - y > 2.0D) {
                return Direction.UP;
            }

            if (y - d0 > 0.0D) {
                return DOWN;
            }
        }
        int i = (int) ((MobEntity.rotationYaw * 4.0F / 360.0F) + 0.5D);
        int l = ((MobEntity.rotationYaw * 4.0F / 360.0F) + 0.5D < i ? i - 1 : i) & 3;
        return l == 0 ? Direction.NORTH : (l == 1 ? Direction.EAST : (l == 2 ? SOUTH : (l == 3 ? Direction.WEST : DOWN)));
    }

    public static Direction determineOrientationHoriz(MobEntity MobEntity) {
        int i = (int) ((MobEntity.rotationYaw * 4.0F / 360.0F) + 0.5D);
        int l = ((MobEntity.rotationYaw * 4.0F / 360.0F) + 0.5D < i ? i - 1 : i) & 3;
        return l == 0 ? Direction.NORTH : (l == 1 ? Direction.EAST : (l == 2 ? SOUTH : (l == 3 ? Direction.WEST : DOWN)));
    }

    // Given the metavalue of a block, reorient the world direction to the internal block direction
    // so that the front side will be SOUTH.
    public static Direction worldToBlockSpaceHoriz(Direction side, BlockState state) {
        return worldToBlockSpace(side, getOrientationHoriz(state));
    }

    public static Direction worldToBlockSpace(Direction worldSide, Direction blockDirection) {
        switch (blockDirection) {
            case DOWN:
                switch (worldSide) {
                    case DOWN: return SOUTH;
                    case UP: return NORTH;
                    case NORTH: return UP;
                    case SOUTH: return DOWN;
                    case WEST: return EAST;
                    case EAST: return WEST;
                    default: return worldSide;
                }
            case UP:
                switch (worldSide) {
                    case DOWN: return NORTH;
                    case UP: return SOUTH;
                    case NORTH: return UP;
                    case SOUTH: return DOWN;
                    case WEST: return WEST;
                    case EAST: return EAST;
                    default: return worldSide;
                }
            case NORTH:
                if (worldSide == DOWN || worldSide == UP) {
                    return worldSide;
                }
                return worldSide.getOpposite();
            case SOUTH:
                return worldSide;
            case WEST:
                if (worldSide == DOWN || worldSide == UP) {
                    return worldSide;
                } else if (worldSide == WEST) {
                    return SOUTH;
                } else if (worldSide == NORTH) {
                    return WEST;
                } else if (worldSide == EAST) {
                    return NORTH;
                } else {
                    return EAST;
                }
            case EAST:
                if (worldSide == DOWN || worldSide == UP) {
                    return worldSide;
                } else if (worldSide == WEST) {
                    return NORTH;
                } else if (worldSide == NORTH) {
                    return EAST;
                } else if (worldSide == EAST) {
                    return SOUTH;
                } else {
                    return WEST;
                }
            default:
                return worldSide;
        }
    }

    public static Direction blockToWorldSpace(Direction blockSide, Direction blockDirection) {
        switch (blockDirection) {
            case DOWN:
                switch (blockSide) {
                    case SOUTH: return DOWN;
                    case NORTH: return UP;
                    case UP: return NORTH;
                    case DOWN: return SOUTH;
                    case EAST: return WEST;
                    case WEST: return EAST;
                    default: return blockSide;
                }
            case UP:
                switch (blockSide) {
                    case NORTH: return DOWN;
                    case SOUTH: return UP;
                    case UP: return NORTH;
                    case DOWN: return SOUTH;
                    case WEST: return WEST;
                    case EAST: return EAST;
                    default: return blockSide;
                }
            case NORTH:
                if (blockSide == DOWN || blockSide == UP) {
                    return blockSide;
                }
                return blockSide.getOpposite();
            case SOUTH:
                return blockSide;
            case WEST:
                if (blockSide == DOWN || blockSide == UP) {
                    return blockSide;
                } else if (blockSide == SOUTH) {
                    return WEST;
                } else if (blockSide == WEST) {
                    return NORTH;
                } else if (blockSide == NORTH) {
                    return EAST;
                } else {
                    return SOUTH;
                }
            case EAST:
                if (blockSide == DOWN || blockSide == UP) {
                    return blockSide;
                } else if (blockSide == NORTH) {
                    return WEST;
                } else if (blockSide == EAST) {
                    return NORTH;
                } else if (blockSide == SOUTH) {
                    return EAST;
                } else {
                    return SOUTH;
                }
            default:
                return blockSide;
        }
    }

    public static Vec3d blockToWorldSpace(Vec3d v, BlockState state) {
        return blockToWorldSpace(v, getOrientation(state));
    }

    public static Vec3d worldToBlockSpace(Vec3d v, BlockState state) {
        return worldToBlockSpace(v, getOrientation(state));
    }

    // Given the metavalue of a block, reorient the world direction to the internal block direction
    // so that the front side will be SOUTH.
    public static Vec3d blockToWorldSpaceHoriz(Vec3d v, BlockState state) {
        return blockToWorldSpace(v, getOrientationHoriz(state));
    }

    public static Vec3d worldToBlockSpaceHoriz(Vec3d v, BlockState state) {
        return worldToBlockSpace(v, getOrientationHoriz(state));
    }

    public static Vec3d blockToWorldSpace(Vec3d v, Direction side) {
        switch (side) {
            case DOWN: return new Vec3d(v.x, v.z, v.y);        // @todo check: most likely wrong
            case UP:  return new Vec3d(v.x, v.z, v.y);         // @todo check: most likely wrong
            case NORTH: return new Vec3d(1-v.x, v.y, 1-v.z);
            case SOUTH: return v;
            case WEST: return new Vec3d(1-v.z, v.y, v.x);
            case EAST: return new Vec3d(v.z, v.y, 1-v.x);
            default: return v;
        }
    }

    public static Vec3d worldToBlockSpace(Vec3d v, Direction side) {
        switch (side) {
            case DOWN: return new Vec3d(v.x, v.z, v.y);        // @todo check: most likely wrong
            case UP:  return new Vec3d(v.x, v.z, v.y);         // @todo check: most likely wrong
            case NORTH: return new Vec3d(1-v.x, v.y, 1-v.z);
            case SOUTH: return v;
            case WEST: return new Vec3d(v.z, v.y, 1-v.x);
            case EAST: return new Vec3d(1-v.z, v.y, v.x);
            default: return v;
        }
    }

    public static Direction getTopDirection(Direction rotation) {
        switch(rotation) {
            case DOWN:
                return SOUTH;
            case UP:
                return Direction.NORTH;
            default:
                return Direction.UP;
        }
    }

    public static Direction getBottomDirection(Direction rotation) {
        switch(rotation) {
            case DOWN:
                return Direction.NORTH;
            case UP:
                return SOUTH;
            default:
                return DOWN;
        }
    }

    // Given the metavalue of a block, reorient the world direction to the internal block direction
    // so that the front side will be SOUTH.
    public static Direction worldToBlockSpace(Direction side, BlockState state) {
        return worldToBlockSpace(side, getOrientation(state));
    }

    public static Direction getFacingFromEntity(BlockPos clickedBlock, Entity entityIn) {
        if (MathHelper.abs((float) entityIn.posX - clickedBlock.getX()) < 2.0F && MathHelper.abs((float) entityIn.posZ - clickedBlock.getZ()) < 2.0F) {
            double d0 = entityIn.posY + entityIn.getEyeHeight();

            if (d0 - clickedBlock.getY() > 2.0D) {
                return UP;
            }

            if (clickedBlock.getY() - d0 > 0.0D) {
                return DOWN;
            }
        }

        return entityIn.getHorizontalFacing().getOpposite();
    }

    // Given the metavalue of a block, reorient the world direction to the internal block direction
    // so that the front side will be SOUTH.
    public static Direction reorient(Direction side, BlockState state) {
        return reorient(side, getOrientation(state));
    }

    // Given the metavalue of a block, reorient the world direction to the internal block direction
    // so that the front side will be SOUTH.
    public static Direction reorientHoriz(Direction side, BlockState state) {
        return reorient(side, getOrientationHoriz(state));
    }

    public static Direction reorient(Direction side, Direction blockDirection) {
        switch (blockDirection) {
            case DOWN:
                switch (side) {
                    case DOWN: return SOUTH;
                    case UP: return NORTH;
                    case NORTH: return UP;
                    case SOUTH: return DOWN;
                    case WEST: return EAST;
                    case EAST: return WEST;
                    default: return side;
                }
            case UP:
                switch (side) {
                    case DOWN: return NORTH;
                    case UP: return SOUTH;
                    case NORTH: return UP;
                    case SOUTH: return DOWN;
                    case WEST: return WEST;
                    case EAST: return EAST;
                    default: return side;
                }
            case NORTH:
                if (side == DOWN || side == UP) {
                    return side;
                }
                return side.getOpposite();
            case SOUTH:
                return side;
            case WEST:
                if (side == DOWN || side == UP) {
                    return side;
                } else if (side == WEST) {
                    return SOUTH;
                } else if (side == NORTH) {
                    return WEST;
                } else if (side == EAST) {
                    return NORTH;
                } else {
                    return EAST;
                }
            case EAST:
                if (side == DOWN || side == UP) {
                    return side;
                } else if (side == WEST) {
                    return NORTH;
                } else if (side == NORTH) {
                    return EAST;
                } else if (side == EAST) {
                    return SOUTH;
                } else {
                    return WEST;
                }
            default:
                return side;
        }
        //return side;
    }
}
