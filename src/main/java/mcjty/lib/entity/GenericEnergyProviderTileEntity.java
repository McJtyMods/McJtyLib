package mcjty.lib.entity;

import cofh.api.energy.IEnergyProvider;
import net.minecraft.util.EnumFacing;

public class GenericEnergyProviderTileEntity extends GenericEnergyStorageTileEntity implements IEnergyProvider {

    public GenericEnergyProviderTileEntity(int maxEnergy, int maxExtract) {
        super(maxEnergy, 0, maxExtract);
    }

    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return storage.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return storage.getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }

}
