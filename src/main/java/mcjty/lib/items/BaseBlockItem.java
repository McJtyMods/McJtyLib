package mcjty.lib.items;

import mcjty.lib.api.ITabExpander;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Collections;
import java.util.List;

/**
 * Base to abstract between 1.19.2 and 1.19.3
 */
public class BaseBlockItem extends BlockItem implements ITabExpander {

    public BaseBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context,
            net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<net.minecraft.network.chat.Component> tooltip,
            net.minecraft.world.item.TooltipFlag flags) {
        super.appendHoverText(stack, context, display, tooltip, flags);
        if (getBlock() instanceof mcjty.lib.blocks.BaseBlock block) {
            var lines = new java.util.ArrayList<net.minecraft.network.chat.Component>();
            block.appendHoverText(stack, context, lines, flags);
            lines.forEach(tooltip);
        }
    }

    @Override
    public List<ItemStack> getItemsForTab() {
        return Collections.emptyList();
    }
}
