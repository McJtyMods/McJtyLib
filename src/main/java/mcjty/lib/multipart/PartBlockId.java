package mcjty.lib.multipart;

import net.minecraft.block.state.IBlockState;

public class PartBlockId {
    private final IBlockState state;

    public PartBlockId(IBlockState state) {
        this.state = state;
    }

    public IBlockState getBlockState() {
        return state;
    }

    @Override
    public String toString() {
        return state.toString();
    }
}
