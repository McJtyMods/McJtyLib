package mcjty.lib.container;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class GenericItemBlock extends ItemBlock {
    private final GenericBlock genericBlock;

    public GenericItemBlock(Block block) {
        super(block);
        genericBlock = (GenericBlock) block;
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        genericBlock.addInformation(stack, worldIn, tooltip, flagIn);
    }

}
