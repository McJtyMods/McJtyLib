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
    public List<ItemStack> getItemsForTab() {
        return Collections.emptyList();
    }
}
