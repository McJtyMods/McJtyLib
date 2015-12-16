package mcjty.lib.entity;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.util.EnumFacing;

public class GenericEnergyReceiverTileEntity extends GenericEnergyStorageTileEntity implements IEnergyReceiver {

    public GenericEnergyReceiverTileEntity(int maxEnergy, int maxReceive) {
        super(maxEnergy, maxReceive);
    }

    public GenericEnergyReceiverTileEntity(int maxEnergy, int maxReceive, int maxExtract) {
        super(maxEnergy, maxReceive, maxExtract);
    }

    public void consumeEnergy(int consume) {
        modifyEnergyStored(-consume);
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return storage.receiveEnergy(maxReceive, simulate);
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
