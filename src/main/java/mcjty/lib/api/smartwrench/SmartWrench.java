package mcjty.lib.api.smartwrench;

import net.minecraft.item.ItemStack;

public interface SmartWrench {
    SmartWrenchMode getMode(ItemStack itemStack);
}
