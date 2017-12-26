package mcjty.lib.container;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class DamageMetadataItemBlock extends ItemBlock {

    public DamageMetadataItemBlock(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

}
