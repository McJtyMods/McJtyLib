package mcjty.lib.tileentity;

import mcjty.lib.api.container.IGenericContainer;
import mcjty.lib.api.power.ItemEnergy;
import mcjty.lib.setup.Registration;
import mcjty.lib.varia.EnergyTools;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.world.inventory.DataSlot;
import mcjty.lib.varia.IEnergyStorage;


import mcjty.lib.container.IndexedStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import team.reborn.energy.api.EnergyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class GenericEnergyStorage implements IEnergyStorage, EnergyStorage {

    private final GenericTileEntity tileEntity;
    private final boolean isReceiver;

    private long energy;
    private final long capacity;
    private final long maxReceive;

    private final SnapshotParticipant<Long> transferJournal = new SnapshotParticipant<>() {
        private Long before;
        @Override protected void releaseSnapshot(Long snapshot) { before=snapshot; }
        @Override protected Long createSnapshot() { return energy; }
        @Override protected void readSnapshot(Long snapshot) { energy = snapshot; }
        @Override protected void onFinalCommit() { if(energy!=before) tileEntity.markDirtyQuick(); }
    };
    @Override public boolean supportsInsertion() { return isReceiver; }
    @Override public boolean supportsExtraction() { return false; }
    @Override public long getAmount() { return energy; }
    @Override public long insert(long amount, TransactionContext transaction) {
        if (amount < 0) throw new IllegalArgumentException("Negative amount");
        long inserted = isReceiver ? Math.min(amount, Math.min(maxReceive, capacity-energy)) : 0;
        if (inserted > 0) { transferJournal.updateSnapshots(transaction); energy += inserted; }
        return inserted;
    }
    @Override public long extract(long amount, TransactionContext transaction) {
        if (amount < 0) throw new IllegalArgumentException("Negative amount");
        return 0; // This storage has always exposed energy input only.
    }

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

    public void applyImplicitComponents(ItemEnergy energy) {
        if (energy != null) {
            setEnergy(energy.energy());
        }
    }

    public void collectImplicitComponents(DataComponentMap.Builder builder) {
        builder.set(Registration.ITEM_ENERGY.get(), new ItemEnergy(getEnergy()));
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

    public LongTag serializeNBT(HolderLookup.Provider provider) {
        return LongTag.valueOf(energy);
    }

    public void deserializeNBT(HolderLookup.Provider provider, LongTag nbt) {
        energy = nbt.value();
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

    public void save(CompoundTag tag, String tagName, HolderLookup.Provider provider) {
        tag.put(tagName, serializeNBT(provider));
    }

    public void load(CompoundTag tag, String tagName, HolderLookup.Provider provider) {
        if (tag.contains(tagName)) {
            deserializeNBT(provider, (LongTag) tag.get(tagName));
        }
    }
}
