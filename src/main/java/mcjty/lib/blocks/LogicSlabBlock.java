package mcjty.lib.blocks;

import mcjty.lib.base.ModBase;
import mcjty.lib.tileentity.LogicTileEntity;
import mcjty.lib.varia.LogicFacing;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Function;

import static mcjty.lib.varia.LogicFacing.*;
import static net.minecraft.util.Direction.*;

/**
 * The superclass for logic slabs.
 */
public abstract class LogicSlabBlock<T extends LogicTileEntity, C extends Container> extends GenericBlock<T, C> {

    public static IntegerProperty META_INTERMEDIATE = IntegerProperty.create("intermediate", 0, 3);
    public static EnumProperty<LogicFacing> LOGIC_FACING = EnumProperty.create("logic_facing", LogicFacing.class);

    public LogicSlabBlock(ModBase mod, Material material, Class<? extends T> tileEntityClass, BiFunction<PlayerEntity, IInventory, C> containerFactory, String name, boolean isContainer) {
        super(mod, material, tileEntityClass, containerFactory, name, isContainer);
    }

    public LogicSlabBlock(ModBase mod, Material material, Class<? extends T> tileEntityClass, BiFunction<PlayerEntity, IInventory, C> containerFactory, Function<Block, BlockItem> itemBlockFactory, String name, boolean isContainer) {
        super(mod, material, tileEntityClass, containerFactory, itemBlockFactory, name, isContainer);
    }

    public static Direction rotateLeft(Direction downSide, Direction inputSide) {
        switch (downSide) {
            case DOWN:
                return inputSide.rotateY();
            case UP:
                return inputSide.rotateYCCW();
            case NORTH:
                return inputSide.rotateAround(Axis.Z);
            case SOUTH:
                return inputSide.getOpposite().rotateAround(Axis.Z);
            case WEST:
                return inputSide.rotateAround(Axis.X);
            case EAST:
                return inputSide.getOpposite().rotateAround(Axis.X);
        }
        return inputSide;
    }

    public static Direction rotateRight(Direction downSide, Direction inputSide) {
        return rotateLeft(downSide.getOpposite(), inputSide);
    }

    @Override
    public BaseBlock.RotationType getRotationType() {
        return BaseBlock.RotationType.NONE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Vec3d hit = context.getHitVec();
        double dx = Math.abs(0.5 - hit.x);
        double dy = Math.abs(0.5 - hit.y);
        double dz = Math.abs(0.5 - hit.z);

        Direction side = context.getFace().getOpposite();
//        System.out.println("LogicSlabBlock.getStateForPlacement");
//        System.out.println("  side = " + side);
        LogicFacing facing;
        switch (side) {
            case DOWN:
                if (dx < dz) {
                    facing = hit.z < 0.5 ? DOWN_TOSOUTH : DOWN_TONORTH;
                } else {
                    facing = hit.x < 0.5 ? DOWN_TOEAST : DOWN_TOWEST;
                }
                break;
            case UP:
                if (dx < dz) {
                    facing = hit.z < 0.5 ? UP_TOSOUTH : UP_TONORTH;
                } else {
                    facing = hit.x < 0.5 ? UP_TOEAST : UP_TOWEST;
                }
                break;
            case NORTH:
                if (dx < dy) {
                    facing = hit.y < 0.5 ? NORTH_TOUP : NORTH_TODOWN;
                } else {
                    facing = hit.x < 0.5 ? NORTH_TOEAST : NORTH_TOWEST;
                }
                break;
            case SOUTH:
                if (dx < dy) {
                    facing = hit.y < 0.5 ? SOUTH_TOUP : SOUTH_TODOWN;
                } else {
                    facing = hit.x < 0.5 ? SOUTH_TOEAST : SOUTH_TOWEST;
                }
                break;
            case WEST:
                if (dy < dz) {
                    facing = hit.z < 0.5 ? WEST_TOSOUTH : WEST_TONORTH;
                } else {
                    facing = hit.y < 0.5 ? WEST_TOUP : WEST_TODOWN;
                }
                break;
            case EAST:
                if (dy < dz) {
                    facing = hit.z < 0.5 ? EAST_TOSOUTH : EAST_TONORTH;
                } else {
                    facing = hit.y < 0.5 ? EAST_TOUP : EAST_TODOWN;
                }
                break;
            default:
                facing = DOWN_TOWEST;
                break;
        }
//        System.out.println("  facing = " + facing);
//        System.out.println("  facing.getInputSide() = " + facing.getInputSide());
        // LOGIC_FACING doesn't get saved to metadata, but it doesn't need to. It only needs to be available until LogicTileEntity#onLoad() runs.
        // @todo 1.14 check: was side opposite or not?
        return super.getStateForPlacement(context).with(META_INTERMEDIATE, facing.getMeta()).with(LOGIC_FACING, facing);
    }

    @Override
    public boolean hasRedstoneOutput() {
        return true;
    }

    @Override
    public boolean needsRedstoneCheck() {
        return true;
    }

    public static final AxisAlignedBB BLOCK_DOWN = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.3F, 1.0F);
    public static final AxisAlignedBB BLOCK_UP = new AxisAlignedBB(0.0F, 0.7F, 0.0F, 1.0F, 1.0F, 1.0F);
    public static final AxisAlignedBB BLOCK_NORTH = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.3F);
    public static final AxisAlignedBB BLOCK_SOUTH = new AxisAlignedBB(0.0F, 0.0F, 0.7F, 1.0F, 1.0F, 1.0F);
    public static final AxisAlignedBB BLOCK_WEST = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 0.3F, 1.0F, 1.0F);
    public static final AxisAlignedBB BLOCK_EAST = new AxisAlignedBB(0.7F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

    // @todo 1.14
//    @Override
//    public AxisAlignedBB getBoundingBox(BlockState state, IBlockReader world, BlockPos pos) {
//        BlockState blockState = world.getBlockState(pos);
//        if (blockState.getBlock() instanceof LogicSlabBlock) {
//            TileEntity te = world.getTileEntity(pos);
//            if (te instanceof LogicTileEntity) {
//                LogicTileEntity logicTileEntity = (LogicTileEntity) te;
//                Direction side = logicTileEntity.getFacing(blockState).getSide();
//                switch (side) {
//                    case DOWN:
//                        return BLOCK_DOWN;
//                    case UP:
//                        return BLOCK_UP;
//                    case NORTH:
//                        return BLOCK_NORTH;
//                    case SOUTH:
//                        return BLOCK_SOUTH;
//                    case WEST:
//                        return BLOCK_WEST;
//                    case EAST:
//                        return BLOCK_EAST;
//                }
//            }
//        }
//        return BLOCK_DOWN;
//    }


    // Client side
    @Override
    public void initModel() {
        super.initModel();
        // @todo 1.14
//        StateMap.Builder ignorePower = new StateMap.Builder().ignore(META_INTERMEDIATE);
//        ModelLoader.setCustomStateMapper(this, ignorePower.build());
    }

    /**
     * Returns the signal strength at one input of the block
     */
    protected int getInputStrength(World world, BlockPos pos, Direction side) {
        int power = world.getRedstonePower(pos.offset(side), side);
        if (power < 15) {
            // Check if there is no redstone wire there. If there is a 'bend' in the redstone wire it is
            // not detected with world.getRedstonePower().
            // Not exactly pretty, but it's how vanilla redstone repeaters do it.
            BlockState blockState = world.getBlockState(pos.offset(side));
            Block b = blockState.getBlock();
            if (b == Blocks.REDSTONE_WIRE) {
                power = Math.max(power, blockState.get(RedstoneWireBlock.POWER));
            }
        }

        return power;
    }

    @Deprecated
    @Override
    protected void checkRedstone(World world, BlockPos pos) {
        // Old behaviour
        // @todo remove once all implementations do this in the TE.checkRedstone
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof LogicTileEntity) {
            LogicTileEntity logicTileEntity = (LogicTileEntity)te;
            Direction inputSide = logicTileEntity.getFacing(world.getBlockState(pos)).getInputSide();
            int power = getInputStrength(world, pos, inputSide);
            logicTileEntity.setPowerInput(power);
        }
    }

//    @Override
//    public AxisAlignedBB getCollisionBoundingBox(BlockState blockState, World worldIn, BlockPos pos) {
//        return null;
//    }

    // @todo 1.14
//    @Override
//    public boolean isOpaqueCube(BlockState state) {
//        return false;
//    }
//
//    @Override
//    public boolean isFullBlock(BlockState state) {
//        return false;
//    }
//
//    @Override
//    public boolean isFullCube(BlockState state) {
//        return false;
//    }


    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        TileEntity te = world.getTileEntity(pos);
        if (state.getBlock() instanceof LogicSlabBlock && te instanceof LogicTileEntity) {
            LogicTileEntity logicTileEntity = (LogicTileEntity)te;
            Direction direction = logicTileEntity.getFacing(state).getInputSide();
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

    @Override
    protected int getRedstoneOutput(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        TileEntity te = world.getTileEntity(pos);
        if (state.getBlock() instanceof LogicSlabBlock && te instanceof LogicTileEntity) {
            LogicTileEntity logicTileEntity = (LogicTileEntity) te;
            return logicTileEntity.getRedstoneOutput(state, world, pos, side);
        }
        return 0;
    }

    @Override
    public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation rot) {
        TileEntity te = world.getTileEntity(pos);
        if (state.getBlock() instanceof LogicSlabBlock && te instanceof LogicTileEntity) {
            LogicTileEntity logicTileEntity = (LogicTileEntity) te;
            LogicFacing facing = logicTileEntity.getFacing(state);
            int meta = facing.getMeta();
            switch (meta) {
                case 0: meta = 2; break;
                case 1: meta = 3; break;
                case 2: meta = 1; break;
                case 3: meta = 0; break;
            }
            LogicFacing newfacing = LogicFacing.getFacingWithMeta(facing, meta);
            logicTileEntity.setFacing(newfacing);
            BlockState newstate = state.getBlock().getDefaultState().with(META_INTERMEDIATE, meta);
            world.setBlockState(pos, newstate, 3);
            logicTileEntity.rotateBlock(rot);
            return newstate;
        }
        return state;
    }


    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(LOGIC_FACING).add(META_INTERMEDIATE);
    }

}
