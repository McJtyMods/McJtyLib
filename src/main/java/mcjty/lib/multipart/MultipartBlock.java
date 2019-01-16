package mcjty.lib.multipart;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class MultipartBlock extends Block {

    public static final PartsProperty PARTS = new PartsProperty("parts");

    public MultipartBlock() {
        super(Material.IRON);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return super.getExtendedState(state, world, pos);
    }
}
