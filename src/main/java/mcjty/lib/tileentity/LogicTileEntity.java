package mcjty.lib.tileentity;

import mcjty.lib.varia.LogicFacing;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import static mcjty.lib.blocks.LogicSlabBlock.LOGIC_FACING;

public class LogicTileEntity extends GenericTileEntity {

    protected int powerOutput = 0;

    public LogicTileEntity(BlockEntityType<?> type) {
        super(type);
    }

    public LogicFacing getFacing(BlockState state) {
        return state.getValue(LOGIC_FACING);
    }

    public int getPowerOutput() {
        return powerOutput;
    }

    protected void setRedstoneState(int newout) {
        if (powerOutput == newout) {
            return;
        }
        powerOutput = newout;
        setChanged();
        BlockState state = getBlockState();
        Direction outputSide = getFacing(state).getInputSide().getOpposite();
        getLevel().neighborChanged(this.worldPosition.relative(outputSide), state.getBlock(), this.worldPosition);
        //        getWorld().notifyNeighborsOfStateChange(this.pos, this.getBlockType());
    }

    @Override
    public void checkRedstone(Level world, BlockPos pos) {
        Direction inputSide = getFacing(world.getBlockState(pos)).getInputSide();
        int power = getInputStrength(world, pos, inputSide);
        setPowerInput(power);
    }

    /**
     * Returns the signal strength at one input of the block
     */
    public int getInputStrength(Level world, BlockPos pos, Direction side) {
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
    public int getRedstoneOutput(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        if (side == getFacing(state).getInputSide()) {
            return getPowerOutput();
        } else {
            return 0;
        }
    }
}
