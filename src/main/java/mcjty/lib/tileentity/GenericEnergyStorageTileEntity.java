package mcjty.lib.tileentity;

import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GenericEnergyStorageTileEntity extends GenericTileEntity {

    public static final String CMD_GETENERGY = "getEnergy";

    public static final Key<Long> PARAM_ENERGY = new Key<>("energy", Type.LONG);

    protected McJtyEnergyStorage storage;

    private static long currentRF = 0;

    private int requestRfDelay = 3;

    public void modifyEnergyStored(int energy) {
        storage.modifyEnergyStored(energy);
    }

    public GenericEnergyStorageTileEntity(int maxEnergy, int maxReceive) {
        storage = new McJtyEnergyStorage(maxEnergy);
        storage.setMaxReceive(maxReceive);
    }

    public GenericEnergyStorageTileEntity(int maxEnergy, int maxReceive, int maxExtract) {
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
}
