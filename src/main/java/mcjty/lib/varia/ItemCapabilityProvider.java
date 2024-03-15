package mcjty.lib.varia;

import mcjty.lib.api.power.IBigPower;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class ItemCapabilityProvider implements ICapabilityProvider, IBigPower {

    private final ItemStack itemStack;
    private final IEnergyItem item;

    private final Lazy<IEnergyStorage> energy = Lazy.of(this::createEnergyStorage);

    @Nonnull
    private <T> IEnergyStorage createEnergyStorage() {
        return new IEnergyStorage() {
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
        };
    }

    public ItemCapabilityProvider(ItemStack itemStack, IEnergyItem item) {
        this.itemStack = itemStack;
        this.item = item;
    }

    @Nullable
    @Override
    public Object getCapability(Object o, Object o2) {
        return null;
    }

    //    @Nonnull
//    @Override
//    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
//        return getCapability(cap);
//    }
//
//    @Nonnull
//    @Override
//    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability) {
//        if (capability == ForgeCapabilities.ENERGY) {
//            return energy.cast();
//        }
//        return LazyOptional.empty();
//    }
//
    @Override
    public long getStoredPower() {
        return item.getEnergyStoredL(itemStack);
    }

    @Override
    public long getCapacity() {
        return item.getMaxEnergyStoredL(itemStack);
    }
}
