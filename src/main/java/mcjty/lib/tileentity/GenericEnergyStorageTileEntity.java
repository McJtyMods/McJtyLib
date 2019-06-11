package mcjty.lib.tileentity;

import mcjty.lib.api.power.IBigPower;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.EnergyTools;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// @todo 1.14
//@Optional.InterfaceList({
//    @Optional.Interface(modid = "tesla", iface = "net.darkhax.tesla.api.ITeslaHolder")
//})
public class GenericEnergyStorageTileEntity extends GenericTileEntity implements IBigPower /*, ITeslaHolder*/ {

    public static final String CMD_GETENERGY = "getEnergy";

    public static final Key<Long> PARAM_ENERGY = new Key<>("energy", Type.LONG);

    protected McJtyEnergyStorage storage;

    private static long currentRF = 0;

    private int requestRfDelay = 3;

    public void modifyEnergyStored(long energy) {
        storage.modifyEnergyStored(energy);
    }

    public GenericEnergyStorageTileEntity(TileEntityType<?> type, long maxEnergy, long maxReceive) {
        super(type);
        storage = new McJtyEnergyStorage(maxEnergy);
        storage.setMaxReceive(maxReceive);
    }

    public GenericEnergyStorageTileEntity(TileEntityType<?> type, long maxEnergy, long maxReceive, long maxExtract) {
        super(type);
        storage = new McJtyEnergyStorage(maxEnergy);
        storage.setMaxReceive(maxReceive);
        storage.setMaxExtract(maxExtract);
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
    }

    @Override
    public void readRestorableFromNBT(CompoundNBT tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        storage.read(tagCompound);
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        super.write(tagCompound);
        return tagCompound;
    }

    @Override
    public void writeRestorableToNBT(CompoundNBT tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        storage.write(tagCompound);
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

    public int getEnergyStored() {
        return EnergyTools.getIntEnergyStored(storage.getEnergyStored(), storage.getMaxEnergyStored());
    }

    public int getMaxEnergyStored() {
        return EnergyTools.unsignedClampToInt(storage.getMaxEnergyStored());
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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        if (cap == CapabilityEnergy.ENERGY) {
            return LazyOptional.of(() -> (T) energyStorage);
        }
        // @todo 1.14
//        } else if(capability == EnergyTools.TESLA_HOLDER) {
//            return (T) this;
        return super.getCapability(cap);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityEnergy.ENERGY) {
            return LazyOptional.of(() -> (T) energyStorage);
        }
        // @todo 1.14
//        } else if(capability == EnergyTools.TESLA_HOLDER) {
//            return (T) this;
        return super.getCapability(cap, facing);
    }
}
