package mcjty.lib.blocks;

import mcjty.lib.builder.BlockBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

public class BaseBlockWithTile extends BaseBlock implements EntityBlock {

    private final BiFunction<BlockPos, BlockState, BlockEntity> teSupplier;

    public BaseBlockWithTile(BlockBuilder builder, BiFunction<BlockPos, BlockState, BlockEntity> teSupplier) {
        super(builder);
        this.teSupplier = builder.getTileEntitySupplier();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return teSupplier.apply(blockPos, blockState);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
        super.triggerEvent(state, worldIn, pos, id, param);
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        return tileentity == null ? false : tileentity.triggerEvent(id, param);
    }
}
