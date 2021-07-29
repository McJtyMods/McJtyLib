package mcjty.lib.api.smartwrench;

import net.minecraft.world.item.ItemStack;

public interface SmartWrench {
    SmartWrenchMode getMode(ItemStack itemStack);
}
