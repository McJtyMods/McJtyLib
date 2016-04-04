package mcjty.lib.container;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class GenericItemBlock extends ItemBlock {
    private final GenericBlock genericBlock;

    public GenericItemBlock(Block block) {
        super(block);
        genericBlock = (GenericBlock) block;
    }
}
