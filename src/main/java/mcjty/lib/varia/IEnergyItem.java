package mcjty.lib.varia;

import net.minecraft.item.ItemStack;

public interface IEnergyItem {
    long receiveEnergyL(ItemStack container, long maxReceive, boolean simulate);

    long extractEnergyL(ItemStack container, long maxExtract, boolean simulate);

    long getEnergyStoredL(ItemStack container);

    long getMaxEnergyStoredL(ItemStack container);
}
