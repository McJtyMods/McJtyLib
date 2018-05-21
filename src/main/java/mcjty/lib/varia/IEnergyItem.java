package mcjty.lib.varia;

import net.minecraft.item.ItemStack;

public interface IEnergyItem {
    long receiveEnergy(ItemStack container, long maxReceive, boolean simulate);

    long extractEnergy(ItemStack container, long maxExtract, boolean simulate);

    long getEnergyStored(ItemStack container);

    long getMaxEnergyStored(ItemStack container);
}
