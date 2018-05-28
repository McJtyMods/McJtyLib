package mcjty.lib.varia;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nullable;

import mcjty.lib.api.power.IBigPower;

@Optional.InterfaceList({
    @Optional.Interface(modid = "tesla", iface = "net.darkhax.tesla.api.ITeslaConsumer"),
    @Optional.Interface(modid = "tesla", iface = "net.darkhax.tesla.api.ITeslaHolder"),
    @Optional.Interface(modid = "tesla", iface = "net.darkhax.tesla.api.ITeslaProducer")
})
public class ItemCapabilityProvider implements ICapabilityProvider, IBigPower, IEnergyStorage, ITeslaHolder, ITeslaConsumer, ITeslaProducer {

    private final ItemStack itemStack;
    private final IEnergyItem item;

    public ItemCapabilityProvider(ItemStack itemStack, IEnergyItem item) {
        this.itemStack = itemStack;
        this.item = item;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY || capability == EnergyTools.TESLA_HOLDER || capability == EnergyTools.TESLA_CONSUMER || capability == EnergyTools.TESLA_PRODUCER) {
            return true;
        }
        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY || capability == EnergyTools.TESLA_HOLDER || capability == EnergyTools.TESLA_CONSUMER || capability == EnergyTools.TESLA_PRODUCER) {
            return (T) this;
        }
        return null;
    }

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

    @Override
    public long getStoredPower() {
        return item.getEnergyStoredL(itemStack);
    }

    @Override
    public long getCapacity() {
        return item.getMaxEnergyStoredL(itemStack);
    }

    @Override
    public long takePower(long power, boolean simulated) {
        return item.extractEnergyL(itemStack, power, simulated);
    }

    @Override
    public long givePower(long power, boolean simulated) {
        return item.receiveEnergyL(itemStack, power, simulated);
    }
}
