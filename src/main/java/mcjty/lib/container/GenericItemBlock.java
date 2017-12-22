package mcjty.lib.container;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class GenericItemBlock extends ItemBlock {
    private final BaseBlock baseBlock;

    public GenericItemBlock(Block block) {
        super(block);
        baseBlock = (BaseBlock) block;
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        baseBlock.addInformation(stack, worldIn, tooltip, flagIn);
    }

}
