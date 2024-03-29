package mcjty.lib.blocks;

import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.tileentity.LogicSupport;
import mcjty.lib.varia.LogicFacing;
import mcjty.lib.varia.OrientationTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.varia.LogicFacing.*;
import static net.minecraft.util.Direction.*;

/**
 * The superclass for logic slabs.
 */
public class LogicSlabBlock extends BaseBlock {

    public static final EnumProperty<LogicFacing> LOGIC_FACING = EnumProperty.create("logic_facing", LogicFacing.class);

    public LogicSlabBlock(BlockBuilder builder) {
        super(builder);
    }

    public static Direction rotateLeft(Direction downSide, Direction inputSide) {
        switch (downSide) {
            case DOWN:
                return inputSide.getClockWise();
            case UP:
                return inputSide.getCounterClockWise();
            case NORTH:
                return OrientationTools.rotateAround(inputSide, Axis.Z);
            case SOUTH:
                return OrientationTools.rotateAround(inputSide.getOpposite(), Axis.Z);
            case WEST:
                return OrientationTools.rotateAround(inputSide, Axis.X);
            case EAST:
                return OrientationTools.rotateAround(inputSide.getOpposite(), Axis.X);
        }
        return inputSide;
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
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Vector3d hit = context.getClickLocation();
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

    public static final VoxelShape BLOCK_DOWN = VoxelShapes.box(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
    public static final VoxelShape BLOCK_UP = VoxelShapes.box(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
    public static final VoxelShape BLOCK_NORTH = VoxelShapes.box(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
    public static final VoxelShape BLOCK_SOUTH = VoxelShapes.box(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
    public static final VoxelShape BLOCK_WEST = VoxelShapes.box(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
    public static final VoxelShape BLOCK_EAST = VoxelShapes.box(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        switch (state.getValue(LOGIC_FACING).getSide()) {
            case DOWN:
                return BLOCK_DOWN;
            case UP:
                return BLOCK_UP;
            case NORTH:
                return BLOCK_NORTH;
            case SOUTH:
                return BLOCK_SOUTH;
            case WEST:
                return BLOCK_WEST;
            case EAST:
                return BLOCK_EAST;
        }
        return BLOCK_DOWN;
    }

    /**
     * Returns the signal strength at one input of the block
     */
    protected int getInputStrength(World world, BlockPos pos, Direction side) {
        int power = world.getSignal(pos.relative(side), side);
        if (power < 15) {
            // Check if there is no redstone wire there. If there is a 'bend' in the redstone wire it is
            // not detected with world.getRedstonePower().
            // Not exactly pretty, but it's how vanilla redstone repeaters do it.
            BlockState blockState = world.getBlockState(pos.relative(side));
            Block b = blockState.getBlock();
            if (b == Blocks.REDSTONE_WIRE) {
                power = Math.max(power, blockState.getValue(RedstoneWireBlock.POWER));
            }
        }

        return power;
    }

    @Override
    @Deprecated
    protected void checkRedstone(World world, BlockPos pos) {
        super.checkRedstone(world, pos);
        // Old behaviour
        // @todo remove once all implementations do this in the TE.checkRedstone
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof GenericTileEntity) {
            GenericTileEntity generic = (GenericTileEntity)te;
            Direction inputSide = LogicSupport.getFacing(world.getBlockState(pos)).getInputSide();
            int power = getInputStrength(world, pos, inputSide);
            generic.setPowerInput(power);
        }
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        TileEntity te = world.getBlockEntity(pos);
        if (state.getBlock() instanceof LogicSlabBlock && te instanceof GenericTileEntity) {
            Direction direction = LogicSupport.getFacing(state).getInputSide();
            switch (direction) {
                case NORTH:
                case SOUTH:
                    return side == NORTH || side == SOUTH;
                case WEST:
                case EAST:
                    return side == WEST || side == EAST;
                case DOWN:
                case UP:
                    return side == DOWN || side == UP;
            }
        }
        return false;
    }

    protected int getRedstoneOutput(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        TileEntity te = world.getBlockEntity(pos);
        if (state.getBlock() instanceof LogicSlabBlock && te instanceof GenericTileEntity) {
            GenericTileEntity generic = (GenericTileEntity) te;
            return generic.getRedstoneOutput(state, world, pos, side);
        }
        return 0;
    }

    @Override
    public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation rot) {
        if (state.getBlock() instanceof LogicSlabBlock) {
            LogicFacing facing = state.getValue(LOGIC_FACING);
            LogicFacing newfacing = LogicFacing.rotate(facing);
            BlockState newstate = state.getBlock().defaultBlockState().setValue(LOGIC_FACING, newfacing);
            world.setBlock(pos, newstate, 3);
            TileEntity te = world.getBlockEntity(pos);
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
    public int getSignal(@Nonnull BlockState blockState, @Nonnull IBlockReader blockAccess, @Nonnull BlockPos pos, @Nonnull Direction side) {
        return getRedstoneOutput(blockState, blockAccess, pos, side);
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LOGIC_FACING);
    }

}
