package mcjty.lib.varia;

import mcjty.lib.api.power.IBigPower;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// @todo 1.14
//@Optional.InterfaceList({
//    @Optional.Interface(modid = "tesla", iface = "net.darkhax.tesla.api.ITeslaConsumer"),
//    @Optional.Interface(modid = "tesla", iface = "net.darkhax.tesla.api.ITeslaHolder"),
//    @Optional.Interface(modid = "tesla", iface = "net.darkhax.tesla.api.ITeslaProducer")
//})
public class ItemCapabilityProvider implements ICapabilityProvider, IBigPower /*, ITeslaHolder, ITeslaConsumer, ITeslaProducer*/ {

    private final ItemStack itemStack;
    private final IEnergyItem item;

    public ItemCapabilityProvider(ItemStack itemStack, IEnergyItem item) {
        this.itemStack = itemStack;
        this.item = item;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return getCapability(cap);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability) {
        if (capability == CapabilityEnergy.ENERGY) {
            return LazyOptional.of(() -> (T) energyStorage);
//        } else if(capability == EnergyTools.TESLA_HOLDER || capability == EnergyTools.TESLA_CONSUMER || capability == EnergyTools.TESLA_PRODUCER) {
//            return (T) this;
        }
        return LazyOptional.empty();
    }

    private final IEnergyStorage energyStorage = new IEnergyStorage() {

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
            return EnergyTools.getIntEnergyStored(item.getEnergyStoredL(itemStack), item.getMaxEnergyStoredL(itemStack));
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

    @Override
    public long getStoredPower() {
        return item.getEnergyStoredL(itemStack);
    }

    @Override
    public long getCapacity() {
        return item.getMaxEnergyStoredL(itemStack);
    }

    // @todo 1.14
//    @Override
//    public long takePower(long power, boolean simulated) {
//        return item.extractEnergyL(itemStack, power, simulated);
//    }
//
//    @Override
//    public long givePower(long power, boolean simulated) {
//        return item.receiveEnergyL(itemStack, power, simulated);
//    }
}
