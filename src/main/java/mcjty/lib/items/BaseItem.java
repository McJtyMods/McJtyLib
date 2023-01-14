package mcjty.lib.items;

import mcjty.lib.api.ITabExpander;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * Base to abstract between 1.19.2 and 1.19.3
 */
public class BaseItem extends Item implements ITabExpander {

    public BaseItem(Properties properties) {
        super(properties);
    }

    @Override
    public List<ItemStack> getItemsForTab() {
        return Collections.emptyList();
    }
}
