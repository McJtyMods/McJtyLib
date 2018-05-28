package mcjty.lib.varia;

import cofh.redstoneflux.api.IEnergyContainerItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(modid = "redstoneflux", iface = "cofh.redstoneflux.api.IEnergyContainerItem")
public interface IEnergyItem extends IEnergyContainerItem {
    long receiveEnergyL(ItemStack container, long maxReceive, boolean simulate);

    long extractEnergyL(ItemStack container, long maxExtract, boolean simulate);

    long getEnergyStoredL(ItemStack container);

    long getMaxEnergyStoredL(ItemStack container);

    @Override
    @Optional.Method(modid = "redstoneflux")
    default int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        return (int)receiveEnergyL(container, maxReceive, simulate);
    }

    @Override
    @Optional.Method(modid = "redstoneflux")
    default int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        return (int)extractEnergyL(container, maxExtract, simulate);
    }

    @Override
    @Optional.Method(modid = "redstoneflux")
    default int getEnergyStored(ItemStack container) {
        return EnergyTools.getIntEnergyStored(getEnergyStoredL(container), getMaxEnergyStoredL(container));
    }

    @Override
    @Optional.Method(modid = "redstoneflux")
    default int getMaxEnergyStored(ItemStack container) {
        return EnergyTools.unsignedClampToInt(getMaxEnergyStoredL(container));
    }
}
