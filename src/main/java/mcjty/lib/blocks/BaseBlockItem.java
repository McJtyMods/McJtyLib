package mcjty.lib.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class BaseBlockItem extends BlockItem {

    public BaseBlockItem(Block blockIn, Properties builder) {
        super(blockIn, builder);
        setRegistryName(blockIn.getRegistryName());
    }
}
