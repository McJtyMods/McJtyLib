package mcjty.lib.entity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.EnergyStorage;

public class McJtyEnergyStorage extends EnergyStorage {

    public McJtyEnergyStorage(int capacity) {
        super(capacity);
    }

    public McJtyEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public McJtyEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public McJtyEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    public void modifyEnergyStored(int energy) {
        if (energy > capacity - this.energy) {
            energy = capacity - this.energy;
        } else if (energy < -this.energy) {
            energy = -this.energy;
        }

        this.energy += energy;
    }

    public void setMaxReceive(int max) {
        this.maxReceive = max;
    }

    public void setMaxExtract(int max) {
        this.maxExtract = max;
    }

    public McJtyEnergyStorage readFromNBT(NBTTagCompound nbt) {

        this.energy = nbt.getInteger("Energy");

        if (energy > capacity) {
            energy = capacity;
        }
        return this;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

        if (energy < 0) {
            energy = 0;
        }
        nbt.setInteger("Energy", energy);
        return nbt;
    }
}
