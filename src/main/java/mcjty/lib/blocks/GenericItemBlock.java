package mcjty.lib.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class GenericItemBlock extends BlockItem {
    private final BaseBlock baseBlock;

    public GenericItemBlock(Block block) {
        // @todo 1.14
        super(block, new Properties());
        baseBlock = (BaseBlock) block;
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        baseBlock.addInformation(stack, worldIn, tooltip, flagIn);
    }

}
