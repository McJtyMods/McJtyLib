package mcjty.lib.api;

import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * For expansion of items in the creative tab (abstraction between 1.19.2 and 1.19.3)
 */
public interface ITabExpander {
    List<ItemStack> getItemsForTab();
}
