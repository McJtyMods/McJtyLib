package mcjty.lib.varia;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class ItemCapabilityProvider implements ICapabilityProvider {

    private final ItemStack itemStack;
    private final IEnergyItem item;

    public ItemCapabilityProvider(ItemStack itemStack, IEnergyItem item) {
        this.itemStack = itemStack;
        this.item = item;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return true;
        }
        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return (T) new IEnergyStorage() {
                @Override
                public int receiveEnergy(int maxReceive, boolean simulate) {
                    return (int)item.receiveEnergyL(itemStack, maxReceive, simulate);
                }

                @Override
                public int extractEnergy(int maxExtract, boolean simulate) {
                    return (int)item.extractEnergyL(itemStack, maxExtract, simulate);
                }

                @Override
                public int getEnergyStored() {
                    return EnergyTools.unsignedClampToInt(item.getEnergyStoredL(itemStack));
                }

                @Override
                public int getMaxEnergyStored() {
                    return EnergyTools.unsignedClampToInt(item.getMaxEnergyStoredL(itemStack));
                }

                @Override
                public boolean canExtract() {
                    return true;
                }

                @Override
                public boolean canReceive() {
                    return true;
                }
            };
        }
        return null;
    }
}
