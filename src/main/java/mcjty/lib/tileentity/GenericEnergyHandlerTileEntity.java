package mcjty.lib.tileentity;

import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import mcjty.lib.varia.EnergyTools;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList({
        @Optional.Interface(modid = "tesla", iface = "net.darkhax.tesla.api.ITeslaHolder"),
        @Optional.Interface(modid = "tesla", iface = "net.darkhax.tesla.api.ITeslaConsumer"),
        @Optional.Interface(modid = "tesla", iface = "net.darkhax.tesla.api.ITeslaProducer"),
        @Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyProvider", modid = "redstoneflux"),
        @Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyReceiver", modid = "redstoneflux")
        })
public class GenericEnergyHandlerTileEntity extends GenericEnergyStorageTileEntity implements IEnergyReceiver, IEnergyProvider, ITeslaHolder, ITeslaConsumer, ITeslaProducer, IEnergyStorage {

    public GenericEnergyHandlerTileEntity(long maxEnergy, long maxReceive) {
        super(maxEnergy, maxReceive);
    }

    public GenericEnergyHandlerTileEntity(long maxEnergy, long maxReceive, long maxExtract) {
        super(maxEnergy, maxReceive, maxExtract);
    }

    // -----------------------------------------------------------
    // For IEnergyReceiver and IEnergyProvider

    @Optional.Method(modid = "redstoneflux")
    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return (int)storage.receiveEnergy(maxReceive, simulate);
    }

    @Optional.Method(modid = "redstoneflux")
    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return (int)storage.extractEnergy(maxExtract, simulate);
    }

    @Optional.Method(modid = "redstoneflux")
    @Override
    public int getEnergyStored(EnumFacing from) {
        return EnergyTools.unsignedClampToInt(storage.getEnergyStored());
    }

    @Optional.Method(modid = "redstoneflux")
    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return EnergyTools.unsignedClampToInt(storage.getMaxEnergyStored());
    }

    @Optional.Method(modid = "redstoneflux")
    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }

    // -----------------------------------------------------------
    // For ITeslaHolder, ITeslaConsumer, and ITeslaProducer
    // deliberately not @Optional so that we can reliably call these elsewhere

    @Override
    public long takePower(long power, boolean simulated) {
        return storage.extractEnergy(power, simulated);
    }

    @Override
    public long givePower(long power, boolean simulated) {
        return storage.receiveEnergy(power, simulated);
    }

    @Override
    public long getStoredPower() {
        return storage.getEnergyStored();
    }

    @Override
    public long getCapacity() {
        return storage.getMaxEnergyStored();
    }

    // -----------------------------------------------------------
    // For IEnergyStorage

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return (int)storage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return EnergyTools.unsignedClampToInt(storage.getEnergyStored());
    }

    @Override
    public int getMaxEnergyStored() {
        return EnergyTools.unsignedClampToInt(storage.getMaxEnergyStored());
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
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
