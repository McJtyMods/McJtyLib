package mcjty.lib.api;

import net.minecraft.item.ItemStack;

/**
 * Implement this for your block if you want to support automatic insertion of modules
 */
public interface IModuleSupport {

    boolean isModule(ItemStack item);

    int getFirstSlot();

    int getLastSlot();
}
