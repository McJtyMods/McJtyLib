package mcjty.lib.container;

import mcjty.lib.compat.CompatItemBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class GenericItemBlock extends CompatItemBlock {
    private final GenericBlock genericBlock;

    public GenericItemBlock(Block block) {
        super(block);
        genericBlock = (GenericBlock) block;
    }
}
