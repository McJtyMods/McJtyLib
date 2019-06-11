package mcjty.lib.multipart;


import net.minecraft.block.BlockState;

public class PartBlockId {
    private final BlockState state;

    public PartBlockId(BlockState state) {
        this.state = state;
    }

    public BlockState getBlockState() {
        return state;
    }

    @Override
    public String toString() {
        return state.toString();
    }
}
