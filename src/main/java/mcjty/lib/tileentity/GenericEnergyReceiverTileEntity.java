package mcjty.lib.tileentity;

import mcjty.lib.varia.EnergyTools;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList({
    @Optional.Interface(modid = "tesla", iface = "net.darkhax.tesla.api.ITeslaConsumer")
})
public class GenericEnergyReceiverTileEntity extends GenericEnergyStorageTileEntity implements ITeslaConsumer {

    public GenericEnergyReceiverTileEntity(long maxEnergy, long maxReceive) {
        super(maxEnergy, maxReceive);
    }

    public GenericEnergyReceiverTileEntity(long maxEnergy, long maxReceive, long maxExtract) {
        super(maxEnergy, maxReceive, maxExtract);
    }

    public void consumeEnergy(long consume) {
        modifyEnergyStored(-consume);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == EnergyTools.TESLA_CONSUMER) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == EnergyTools.TESLA_CONSUMER) {
            return (T) this;
        }
        return super.getCapability(capability, facing);
    }

    // -----------------------------------------------------------
    // For ITeslaConsumer
    // deliberately not @Optional so that we can reliably call this elsewhere

    @Override
    public long givePower(long power, boolean simulated) {
        return storage.receiveEnergy(power, simulated);
    }
}
