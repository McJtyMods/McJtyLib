package mcjty.lib.varia;

import net.minecraft.world.item.ItemStack;

public interface IEnergyItem {
    long receiveEnergyL(ItemStack container, long maxReceive, boolean simulate);

    long extractEnergyL(ItemStack container, long maxExtract, boolean simulate);

    long getEnergyStoredL(ItemStack container);

    long getMaxEnergyStoredL(ItemStack container);

    default int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        return (int)receiveEnergyL(container, maxReceive, simulate);
    }

    default int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        return (int)extractEnergyL(container, maxExtract, simulate);
    }

    default int getEnergyStored(ItemStack container) {
        return EnergyTools.getIntEnergyStored(getEnergyStoredL(container), getMaxEnergyStoredL(container));
    }

    default int getMaxEnergyStored(ItemStack container) {
        return EnergyTools.unsignedClampToInt(getMaxEnergyStoredL(container));
    }
}
