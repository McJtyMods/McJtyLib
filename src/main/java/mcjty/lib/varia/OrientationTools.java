package mcjty.lib.varia;

import mcjty.lib.container.BaseBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static net.minecraft.util.EnumFacing.*;

public class OrientationTools {

    // Use these flags if you want to support a single redstone signal and 3 bits for orientation.
    public static final int MASK_ORIENTATION = 0x7;
    public static final int MASK_REDSTONE = 0x8;
    // Use these flags if you want to support both redstone in and output and only 2 bits for orientation.
    public static final int MASK_ORIENTATION_HORIZONTAL = 0x3;          // Only two bits for orientation
    public static final int MASK_REDSTONE_IN = 0x8;                     // Redstone in
    public static final int MASK_REDSTONE_OUT = 0x4;                    // Redstone out
    public static final int MASK_STATE = 0xc;                           // If redstone is not used: state

    public static int setOrientation(int metadata, EnumFacing orientation) {
        return (metadata & ~MASK_ORIENTATION) | orientation.ordinal();
    }

    public static EnumFacing getOrientationHoriz(IBlockState state) {
        return state.getValue(BaseBlock.FACING_HORIZ);
    }

    public static int setOrientationHoriz(int metadata, EnumFacing orientation) {
        return (metadata & ~MASK_ORIENTATION_HORIZONTAL) | getHorizOrientationMeta(orientation);
    }

    public static int getHorizOrientationMeta(EnumFacing orientation) {
        return orientation.ordinal()-2;
    }

    public static EnumFacing getOrientation(IBlockState state) {
        return ((BaseBlock)state.getBlock()).getFrontDirection(state);
    }

    public static EnumFacing determineOrientation(BlockPos pos, EntityLivingBase entityLivingBase) {
        return determineOrientation(pos.getX(), pos.getY(), pos.getZ(), entityLivingBase);
    }

    public static EnumFacing determineOrientation(int x, int y, int z, EntityLivingBase entityLivingBase) {
        if (MathHelper.abs((float) entityLivingBase.posX - x) < 2.0F && MathHelper.abs((float) entityLivingBase.posZ - z) < 2.0F) {
            double d0 = entityLivingBase.posY + 1.82D - entityLivingBase.getYOffset();

            if (d0 - y > 2.0D) {
                return EnumFacing.UP;
            }

            if (y - d0 > 0.0D) {
                return DOWN;
            }
        }
        int i = (int) ((entityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D);
        int l = ((entityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D < i ? i - 1 : i) & 3;
        return l == 0 ? EnumFacing.NORTH : (l == 1 ? EnumFacing.EAST : (l == 2 ? SOUTH : (l == 3 ? EnumFacing.WEST : DOWN)));
    }

    public static EnumFacing determineOrientationHoriz(EntityLivingBase entityLivingBase) {
        int i = (int) ((entityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D);
        int l = ((entityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D < i ? i - 1 : i) & 3;
        return l == 0 ? EnumFacing.NORTH : (l == 1 ? EnumFacing.EAST : (l == 2 ? SOUTH : (l == 3 ? EnumFacing.WEST : DOWN)));
    }

    // Given the metavalue of a block, reorient the world direction to the internal block direction
    // so that the front side will be SOUTH.
    public static EnumFacing worldToBlockSpaceHoriz(EnumFacing side, IBlockState state) {
        return worldToBlockSpace(side, getOrientationHoriz(state));
    }

    public static EnumFacing worldToBlockSpace(EnumFacing worldSide, EnumFacing blockDirection) {
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

    public static EnumFacing blockToWorldSpace(EnumFacing blockSide, EnumFacing blockDirection) {
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

    public static Vec3d blockToWorldSpace(Vec3d v, IBlockState state) {
        return blockToWorldSpace(v, getOrientation(state));
    }

    public static Vec3d worldToBlockSpace(Vec3d v, IBlockState state) {
        return worldToBlockSpace(v, getOrientation(state));
    }

    // Given the metavalue of a block, reorient the world direction to the internal block direction
    // so that the front side will be SOUTH.
    public static Vec3d blockToWorldSpaceHoriz(Vec3d v, IBlockState state) {
        return blockToWorldSpace(v, getOrientationHoriz(state));
    }

    public static Vec3d worldToBlockSpaceHoriz(Vec3d v, IBlockState state) {
        return worldToBlockSpace(v, getOrientationHoriz(state));
    }

    public static Vec3d blockToWorldSpace(Vec3d v, EnumFacing side) {
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

    public static Vec3d worldToBlockSpace(Vec3d v, EnumFacing side) {
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

    public static EnumFacing getTopDirection(EnumFacing rotation) {
        switch(rotation) {
            case DOWN:
                return SOUTH;
            case UP:
                return EnumFacing.NORTH;
            default:
                return EnumFacing.UP;
        }
    }

    public static EnumFacing getBottomDirection(EnumFacing rotation) {
        switch(rotation) {
            case DOWN:
                return EnumFacing.NORTH;
            case UP:
                return SOUTH;
            default:
                return DOWN;
        }
    }

    // Given the metavalue of a block, reorient the world direction to the internal block direction
    // so that the front side will be SOUTH.
    public static EnumFacing worldToBlockSpace(EnumFacing side, IBlockState state) {
        return worldToBlockSpace(side, getOrientation(state));
    }

    public static EnumFacing getFacingFromEntity(BlockPos clickedBlock, EntityLivingBase entityIn) {
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
    public static EnumFacing reorient(EnumFacing side, IBlockState state) {
        return reorient(side, getOrientation(state));
    }

    // Given the metavalue of a block, reorient the world direction to the internal block direction
    // so that the front side will be SOUTH.
    public static EnumFacing reorientHoriz(EnumFacing side, IBlockState state) {
        return reorient(side, getOrientationHoriz(state));
    }

    public static EnumFacing reorient(EnumFacing side, EnumFacing blockDirection) {
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
