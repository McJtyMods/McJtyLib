package mcjty.lib.tileentity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

// @todo 1.14
//@Optional.InterfaceList({
//    @Optional.Interface(modid = "tesla", iface = "net.darkhax.tesla.api.ITeslaConsumer")
//})
public class GenericEnergyReceiverTileEntity extends GenericEnergyStorageTileEntity /*implements ITeslaConsumer*/ {

    public GenericEnergyReceiverTileEntity(TileEntityType<?> type, long maxEnergy, long maxReceive) {
        super(type, maxEnergy, maxReceive);
    }

    public GenericEnergyReceiverTileEntity(TileEntityType<?> type, long maxEnergy, long maxReceive, long maxExtract) {
        super(type, maxEnergy, maxReceive, maxExtract);
    }

    public void consumeEnergy(long consume) {
        modifyEnergyStored(-consume);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        // @todo 1.14
//        if (capability == EnergyTools.TESLA_CONSUMER) {
//            return (T) this;
//        }
        return super.getCapability(cap);
    }

    // -----------------------------------------------------------
    // For ITeslaConsumer
    // deliberately not @Optional so that we can reliably call this elsewhere

    // @todo 1.14
//    @Override
//    public long givePower(long power, boolean simulated) {
//        return storage.receiveEnergy(power, simulated);
//    }
}
