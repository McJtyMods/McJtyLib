package mcjty.lib.tileentity;

import mcjty.lib.api.power.IBigPower;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.EnergyTools;
import net.darkhax.tesla.api.ITeslaHolder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cofh.redstoneflux.api.IEnergyHandler;

@Optional.InterfaceList({
    @Optional.Interface(modid = "tesla", iface = "net.darkhax.tesla.api.ITeslaHolder"),
    @Optional.Interface(modid = "redstoneflux", iface = "cofh.redstoneflux.api.IEnergyHandler")
})
public class GenericEnergyStorageTileEntity extends GenericTileEntity implements IBigPower, ITeslaHolder, IEnergyHandler {

    public static final String CMD_GETENERGY = "getEnergy";

    public static final Key<Long> PARAM_ENERGY = new Key<>("energy", Type.LONG);

    protected McJtyEnergyStorage storage;

    private static long currentRF = 0;

    private int requestRfDelay = 3;

    public void modifyEnergyStored(long energy) {
        storage.modifyEnergyStored(energy);
    }

    public GenericEnergyStorageTileEntity(long maxEnergy, long maxReceive) {
        storage = new McJtyEnergyStorage(maxEnergy);
        storage.setMaxReceive(maxReceive);
    }

    public GenericEnergyStorageTileEntity(long maxEnergy, long maxReceive, long maxExtract) {
        storage = new McJtyEnergyStorage(maxEnergy);
        storage.setMaxReceive(maxReceive);
        storage.setMaxExtract(maxExtract);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        storage.readFromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        return tagCompound;
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        storage.writeToNBT(tagCompound);
    }

    public static long getCurrentRF() {
        return currentRF;
    }

    public static void setCurrentRF(long currentRF) {
        GenericEnergyStorageTileEntity.currentRF = currentRF;
    }

    // Request the RF from the server. This has to be called on the client side.
    public void requestRfFromServer(String modid) {
        requestRfDelay--;
        if (requestRfDelay > 0) {
            return;
        }
        requestRfDelay = 3;
        requestDataFromServer(modid, CMD_GETENERGY, TypedMap.EMPTY);
    }

    @Override
    @Nullable
    public TypedMap executeWithResult(String command, TypedMap args) {
        TypedMap rc = super.executeWithResult(command, args);
        if (rc != null) {
            return rc;
        }
        if (CMD_GETENERGY.equals(command)) {
            return TypedMap.builder().put(PARAM_ENERGY, storage.getEnergyStored()).build();
        }
        return null;
    }

    @Override
    public boolean receiveDataFromServer(String command, @Nonnull TypedMap result) {
        boolean rc = super.receiveDataFromServer(command, result);
        if (rc) {
            return true;
        }
        if (CMD_GETENERGY.equals(command)) {
            setCurrentRF(result.get(PARAM_ENERGY));
            return true;
        }
        return false;
    }

    // -----------------------------------------------------------
    // For IEnergyHandler

    @Optional.Method(modid = "redstoneflux")
    @Override
    public int getEnergyStored(EnumFacing from) {
        return EnergyTools.getIntEnergyStored(storage.getEnergyStored(), storage.getMaxEnergyStored());
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
    // For ITeslaHolder
    // deliberately not @Optional so that we can reliably call these elsewhere

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

    private final IEnergyStorage energyStorage = new IEnergyStorage() {
        private final boolean isReceiver = GenericEnergyStorageTileEntity.this instanceof GenericEnergyReceiverTileEntity;

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return isReceiver ? (int)storage.receiveEnergy(maxReceive, simulate) : 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return EnergyTools.getIntEnergyStored(storage.getEnergyStored(), storage.getMaxEnergyStored());
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
            return isReceiver;
        }
    };

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY || capability == EnergyTools.TESLA_HOLDER) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return (T) energyStorage;
        } else if(capability == EnergyTools.TESLA_HOLDER) {
            return (T) this;
        }
        return super.getCapability(capability, facing);
    }
}
