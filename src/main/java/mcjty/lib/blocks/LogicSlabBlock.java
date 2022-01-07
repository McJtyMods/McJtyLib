package mcjty.lib.blocks;

import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.tileentity.LogicSupport;
import mcjty.lib.varia.LogicFacing;
import mcjty.lib.varia.OrientationTools;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.varia.LogicFacing.*;
import static net.minecraft.core.Direction.*;

import net.minecraft.core.Direction.Axis;

/**
 * The superclass for logic slabs.
 */
public class LogicSlabBlock extends BaseBlock {

    public static final EnumProperty<LogicFacing> LOGIC_FACING = EnumProperty.create("logic_facing", LogicFacing.class);

    public LogicSlabBlock(BlockBuilder builder) {
        super(builder);
    }

    public static Direction rotateLeft(Direction downSide, Direction inputSide) {
        return switch (downSide) {
            case DOWN -> inputSide.getClockWise();
            case UP -> inputSide.getCounterClockWise();
            case NORTH -> OrientationTools.rotateAround(inputSide, Axis.Z);
            case SOUTH -> OrientationTools.rotateAround(inputSide.getOpposite(), Axis.Z);
            case WEST -> OrientationTools.rotateAround(inputSide, Axis.X);
            case EAST -> OrientationTools.rotateAround(inputSide.getOpposite(), Axis.X);
        };
    }

    public static Direction rotateRight(Direction downSide, Direction inputSide) {
        return rotateLeft(downSide.getOpposite(), inputSide);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Vec3 hit = context.getClickLocation();
        BlockPos pos = context.getClickedPos();
        double hx = hit.x - pos.getX();
        double hy = hit.y - pos.getY();
        double hz = hit.z - pos.getZ();
        double dx = Math.abs(0.5 - hx);
        double dy = Math.abs(0.5 - hy);
        double dz = Math.abs(0.5 - hz);

        Direction side = context.getClickedFace().getOpposite();
        LogicFacing facing;
        switch (side) {
            case DOWN:
                if (dx < dz) {
                    facing = hz < 0.5 ? DOWN_TOSOUTH : DOWN_TONORTH;
                } else {
                    facing = hx < 0.5 ? DOWN_TOEAST : DOWN_TOWEST;
                }
                break;
            case UP:
                if (dx < dz) {
                    facing = hz < 0.5 ? UP_TOSOUTH : UP_TONORTH;
                } else {
                    facing = hx < 0.5 ? UP_TOEAST : UP_TOWEST;
                }
                break;
            case NORTH:
                if (dx < dy) {
                    facing = hy < 0.5 ? NORTH_TOUP : NORTH_TODOWN;
                } else {
                    facing = hx < 0.5 ? NORTH_TOEAST : NORTH_TOWEST;
                }
                break;
            case SOUTH:
                if (dx < dy) {
                    facing = hy < 0.5 ? SOUTH_TOUP : SOUTH_TODOWN;
                } else {
                    facing = hx < 0.5 ? SOUTH_TOEAST : SOUTH_TOWEST;
                }
                break;
            case WEST:
                if (dy < dz) {
                    facing = hz < 0.5 ? WEST_TOSOUTH : WEST_TONORTH;
                } else {
                    facing = hy < 0.5 ? WEST_TOUP : WEST_TODOWN;
                }
                break;
            case EAST:
                if (dy < dz) {
                    facing = hz < 0.5 ? EAST_TOSOUTH : EAST_TONORTH;
                } else {
                    facing = hy < 0.5 ? EAST_TOUP : EAST_TODOWN;
                }
                break;
            default:
                facing = DOWN_TOWEST;
                break;
        }
        return super.getStateForPlacement(context).setValue(LOGIC_FACING, facing);
    }

    public static final VoxelShape BLOCK_DOWN = Shapes.box(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
    public static final VoxelShape BLOCK_UP = Shapes.box(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
    public static final VoxelShape BLOCK_NORTH = Shapes.box(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
    public static final VoxelShape BLOCK_SOUTH = Shapes.box(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
    public static final VoxelShape BLOCK_WEST = Shapes.box(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
    public static final VoxelShape BLOCK_EAST = Shapes.box(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return switch (state.getValue(LOGIC_FACING).getSide()) {
            case DOWN -> BLOCK_DOWN;
            case UP -> BLOCK_UP;
            case NORTH -> BLOCK_NORTH;
            case SOUTH -> BLOCK_SOUTH;
            case WEST -> BLOCK_WEST;
            case EAST -> BLOCK_EAST;
        };
    }

    /**
     * Returns the signal strength at one input of the block
     */
    protected int getInputStrength(Level world, BlockPos pos, Direction side) {
        int power = world.getSignal(pos.relative(side), side);
        if (power < 15) {
            // Check if there is no redstone wire there. If there is a 'bend' in the redstone wire it is
            // not detected with world.getRedstonePower().
            // Not exactly pretty, but it's how vanilla redstone repeaters do it.
            BlockState blockState = world.getBlockState(pos.relative(side));
            Block b = blockState.getBlock();
            if (b == Blocks.REDSTONE_WIRE) {
                power = Math.max(power, blockState.getValue(RedStoneWireBlock.POWER));
            }
        }

        return power;
    }

    @Override
    @Deprecated
    protected void checkRedstone(Level world, BlockPos pos) {
        super.checkRedstone(world, pos);
        // Old behaviour
        // @todo remove once all implementations do this in the TE.checkRedstone
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof GenericTileEntity generic) {
            Direction inputSide = LogicSupport.getFacing(world.getBlockState(pos)).getInputSide();
            int power = getInputStrength(world, pos, inputSide);
            generic.setPowerInput(power);
        }
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side) {
        BlockEntity te = world.getBlockEntity(pos);
        if (state.getBlock() instanceof LogicSlabBlock && te instanceof GenericTileEntity) {
            Direction direction = LogicSupport.getFacing(state).getInputSide();
            return switch (direction) {
                case NORTH, SOUTH -> side == NORTH || side == SOUTH;
                case WEST, EAST -> side == WEST || side == EAST;
                case DOWN, UP -> side == DOWN || side == UP;
            };
        }
        return false;
    }

    protected int getRedstoneOutput(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        BlockEntity te = world.getBlockEntity(pos);
        if (state.getBlock() instanceof LogicSlabBlock && te instanceof GenericTileEntity generic) {
            return generic.getRedstoneOutput(state, world, pos, side);
        }
        return 0;
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor world, BlockPos pos, Rotation rot) {
        if (state.getBlock() instanceof LogicSlabBlock) {
            LogicFacing facing = state.getValue(LOGIC_FACING);
            LogicFacing newfacing = LogicFacing.rotate(facing);
            BlockState newstate = state.getBlock().defaultBlockState().setValue(LOGIC_FACING, newfacing);
            world.setBlock(pos, newstate, 3);
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof GenericTileEntity) {
                ((GenericTileEntity) te).rotateBlock(rot);
            }
            return newstate;
        }
        return state;
    }

    @Override
    public boolean isSignalSource(@Nonnull BlockState state) {
        return true;
    }

    @Override
    public int getSignal(@Nonnull BlockState blockState, @Nonnull BlockGetter blockAccess, @Nonnull BlockPos pos, @Nonnull Direction side) {
        return getRedstoneOutput(blockState, blockAccess, pos, side);
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LOGIC_FACING);
    }

}
