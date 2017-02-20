package mcjty.lib.container;

import mcjty.lib.compat.CompatItemBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class GenericItemBlock extends CompatItemBlock {
    private final GenericBlock genericBlock;

    public GenericItemBlock(Block block) {
        super(block);
        genericBlock = (GenericBlock) block;
    }


    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean advancedToolTip) {
//        super.addInformation(itemStack, player, list, advancedToolTip);
        genericBlock.addInformation(itemStack, player, list, advancedToolTip);
    }

}
