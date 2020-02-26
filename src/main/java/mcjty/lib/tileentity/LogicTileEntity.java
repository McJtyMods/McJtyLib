package mcjty.lib.tileentity;

import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.LogicFacing;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import static mcjty.lib.blocks.LogicSlabBlock.LOGIC_FACING;

public class LogicTileEntity extends GenericTileEntity {

    protected int powerOutput = 0;

    public LogicTileEntity(TileEntityType<?> type) {
        super(type);
    }

    public LogicFacing getFacing(BlockState state) {
        // Should not be needed but apparently it sometimes is
        LogicFacing facing = state.get(LOGIC_FACING);
        if (facing == null) {
            Logging.getLogger().log(Level.WARN, "LogicTileEntity has null facing!");
            return LogicFacing.DOWN_TOEAST;
        } else if (!(state.getBlock() instanceof LogicSlabBlock)) {
            Logging.getLogger().log(Level.WARN, "LogicTileEntity expected LogicSlabBlock but had " + state.getBlock().getClass().getName());
            return LogicFacing.DOWN_TOEAST;
        }
        return facing;
    }

    public int getPowerOutput() {
        return powerOutput;
    }

    protected void setRedstoneState(int newout) {
        if (powerOutput == newout) {
            return;
        }
        powerOutput = newout;
        markDirty();
        BlockState state = getWorld().getBlockState(this.pos);
        Direction outputSide = getFacing(state).getInputSide().getOpposite();
        getWorld().neighborChanged(this.pos.offset(outputSide), state.getBlock(), this.pos);
        //        getWorld().notifyNeighborsOfStateChange(this.pos, this.getBlockType());
    }

    @Override
    public void checkRedstone(World world, BlockPos pos) {
        Direction inputSide = getFacing(world.getBlockState(pos)).getInputSide();
        int power = getInputStrength(world, pos, inputSide);
        setPowerInput(power);
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

    @Override
    public int getRedstoneOutput(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        if (side == getFacing(state).getInputSide()) {
            return getPowerOutput();
        } else {
            return 0;
        }
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        super.write(tagCompound);
        return tagCompound;
    }

    // @todo 1.14
//    public BlockState getActualState(BlockState state) {
//        int meta = state.get(META_INTERMEDIATE);
//        LogicFacing facing = getFacing(state);
//        facing = LogicFacing.getFacingWithMeta(facing, meta);
//        return state.with(LOGIC_FACING, facing);
//    }
}
