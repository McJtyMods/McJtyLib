package mcjty.lib.tileentity;

import net.minecraft.nbt.CompoundNBT;

public class McJtyEnergyStorage {
    protected long energy;
    protected long capacity;
    protected long maxReceive;
    protected long maxExtract;

    public McJtyEnergyStorage(long capacity) {
        this(capacity, capacity, capacity, 0);
    }

    public McJtyEnergyStorage(long capacity, long maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public McJtyEnergyStorage(long capacity, long maxReceive, long maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public McJtyEnergyStorage(long capacity, long maxReceive, long maxExtract, long energy) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = Math.max(0 , Math.min(capacity, energy));
    }

    public long receiveEnergy(long maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;

        long energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate)
            energy += energyReceived;
        return energyReceived;
    }

    public long extractEnergy(long maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;

        long energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate)
            energy -= energyExtracted;
        return energyExtracted;
    }

    public long getEnergyStored() {
        return energy;
    }

    public long getMaxEnergyStored() {
        return capacity;
    }

    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    public boolean canReceive() {
        return this.maxReceive > 0;
    }

    public void modifyEnergyStored(long energy) {
        if (energy > capacity - this.energy) {
            energy = capacity - this.energy;
        } else if (energy < -this.energy) {
            energy = -this.energy;
        }

        this.energy += energy;
    }

    public void setMaxReceive(long max) {
        this.maxReceive = max;
    }

    public void setMaxExtract(long max) {
        this.maxExtract = max;
    }

    public McJtyEnergyStorage readFromNBT(CompoundNBT nbt) {

        this.energy = nbt.getLong("Energy");

        if (energy > capacity) {
            energy = capacity;
        }
        return this;
    }

    public CompoundNBT writeToNBT(CompoundNBT nbt) {

        if (energy < 0) {
            energy = 0;
        }
        nbt.setLong("Energy", energy);
        return nbt;
    }
}
