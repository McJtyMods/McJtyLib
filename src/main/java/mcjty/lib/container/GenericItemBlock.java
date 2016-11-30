package mcjty.lib.container;

import mcjty.lib.compat.CompatItemBlock;
import net.minecraft.block.Block;

public class GenericItemBlock extends CompatItemBlock {
    private final GenericBlock genericBlock;

    public GenericItemBlock(Block block) {
        super(block);
        genericBlock = (GenericBlock) block;
    }
}
