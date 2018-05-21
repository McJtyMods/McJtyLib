package mcjty.lib.tileentity;

import net.minecraft.nbt.NBTTagCompound;

public class McJtyEnergyStorage {
    protected int energy;
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    public McJtyEnergyStorage(int capacity) {
        this(capacity, capacity, capacity, 0);
    }

    public McJtyEnergyStorage(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public McJtyEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public McJtyEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = Math.max(0 , Math.min(capacity, energy));
    }

    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;

        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate)
            energy += energyReceived;
        return energyReceived;
    }

    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;

        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate)
            energy -= energyExtracted;
        return energyExtracted;
    }

    public int getEnergyStored() {
        return energy;
    }

    public int getMaxEnergyStored() {
        return capacity;
    }

    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    public boolean canReceive() {
        return this.maxReceive > 0;
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
