package mcjty.lib.entity;

import cofh.redstoneflux.api.IEnergyProvider;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(modid = "redstoneflux", iface = "cofh.redstoneflux.api.IEnergyProvider")
public class GenericEnergyProviderTileEntity extends GenericEnergyStorageTileEntity implements IEnergyProvider, IEnergyStorage {

    public GenericEnergyProviderTileEntity(int maxEnergy, int maxExtract) {
        super(maxEnergy, 0, maxExtract);
    }

    // -----------------------------------------------------------
    // For IEnergyProvider

    @Optional.Method(modid = "redstoneflux")
    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return storage.extractEnergy(maxExtract, simulate);
    }

    @Optional.Method(modid = "redstoneflux")
    @Override
    public int getEnergyStored(EnumFacing from) {
        return storage.getEnergyStored();
    }

    @Optional.Method(modid = "redstoneflux")
    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return storage.getMaxEnergyStored();
    }

    @Optional.Method(modid = "redstoneflux")
    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }

    // -----------------------------------------------------------
    // For IEnergyStorage

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return storage.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return false;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return (T) this;
        }
        return super.getCapability(capability, facing);
    }
}
