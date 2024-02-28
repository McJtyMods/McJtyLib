package mcjty.lib.tileentity;

import mcjty.lib.api.container.IGenericContainer;
import mcjty.lib.varia.EnergyTools;
import net.minecraft.nbt.LongTag;
import net.minecraft.world.inventory.DataSlot;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class GenericEnergyStorage implements IEnergyStorage, INBTSerializable<LongTag> {

    private final GenericTileEntity tileEntity;
    private final boolean isReceiver;

    private long energy;
    private final long capacity;
    private final long maxReceive;

    public GenericEnergyStorage(GenericTileEntity tileEntity, boolean isReceiver, long capacity, long maxReceive) {
        this.tileEntity = tileEntity;
        this.isReceiver = isReceiver;

        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.energy = 0;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (isReceiver) {
            if (!canReceive()) {
                return 0;
            }

            long energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
            if (!simulate) {
                energy += energyReceived;
                tileEntity.markDirtyQuick();
            }
            return (int) energyReceived;
        }
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return EnergyTools.getIntEnergyStored(energy, capacity);
    }

    public long getEnergy() {
        return energy;
    }

    public long getCapacity() {
        return capacity;
    }

    public void setEnergy(long s) {
        energy = s;
    }

    public void consumeEnergy(long energy) {
        this.energy -= energy;
        if (this.energy < 0) {
            this.energy = 0;
        } else if (this.energy > capacity) {
            this.energy = capacity;
        }
        tileEntity.markDirtyQuick();
    }

    public void produceEnergy(long energy) {
        this.energy += energy;
        if (this.energy < 0) {
            this.energy = 0;
        } else if (this.energy > capacity) {
            this.energy = capacity;
        }
        tileEntity.markDirtyQuick();
    }

    @Override
    public LongTag serializeNBT() {
        return LongTag.valueOf(energy);
    }

    @Override
    public void deserializeNBT(LongTag nbt) {
        energy = nbt.getAsLong();
    }

    @Override
    public int getMaxEnergyStored() {
        return EnergyTools.unsignedClampToInt(capacity);
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return isReceiver;
    }

    public void addIntegerListeners(IGenericContainer container) {
        // Least significant part
        container.addIntegerListener(new DataSlot() {
            @Override
            public int get() {
                return (int) (getEnergy());     // Least significant bits
            }

            @Override
            public void set(int i) {
                long orig = getEnergy() & ~0xffffffffL;
                orig |= i;
                setEnergy(orig);
            }
        });
        // Most significant part
        container.addIntegerListener(new DataSlot() {
            @Override
            public int get() {
                return (int) (getEnergy() >> 32L);     // Most significant bits
            }

            @Override
            public void set(int i) {
                long orig = getEnergy() & 0xffffffffL;
                orig |= (long) i << 32L;
                setEnergy(orig);
            }
        });
    }
}
