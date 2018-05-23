package mcjty.lib.tileentity;

import cofh.redstoneflux.api.IEnergyReceiver;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList({
    @Optional.Interface(modid = "tesla", iface = "net.darkhax.tesla.api.ITeslaConsumer"),
    @Optional.Interface(modid = "redstoneflux", iface = "cofh.redstoneflux.api.IEnergyReceiver")
})
public class GenericEnergyReceiverTileEntity extends GenericEnergyStorageTileEntity implements IEnergyReceiver, ITeslaConsumer {

    public GenericEnergyReceiverTileEntity(long maxEnergy, long maxReceive) {
        super(maxEnergy, maxReceive);
    }

    public GenericEnergyReceiverTileEntity(long maxEnergy, long maxReceive, long maxExtract) {
        super(maxEnergy, maxReceive, maxExtract);
    }

    public void consumeEnergy(long consume) {
        modifyEnergyStored(-consume);
    }


    // -----------------------------------------------------------
    // For IEnergyReceiver

    @Optional.Method(modid = "redstoneflux")
    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return (int)storage.receiveEnergy(maxReceive, simulate);
    }

    // -----------------------------------------------------------
    // For ITeslaConsumer
    // deliberately not @Optional so that we can reliably call this elsewhere

    @Override
    public long givePower(long power, boolean simulated) {
        return storage.receiveEnergy(power, simulated);
    }

    // -----------------------------------------------------------
    // For IEnergyStorage

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return (int)storage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
