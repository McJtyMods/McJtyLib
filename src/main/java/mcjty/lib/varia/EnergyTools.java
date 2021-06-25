package mcjty.lib.varia;

import mcjty.lib.McJtyLib;
import mcjty.lib.api.power.IBigPower;
import mcjty.lib.compat.TeslaCompatibility;
import mcjty.lib.tileentity.GenericEnergyStorage;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicLong;

public class EnergyTools {

    public static class EnergyLevel {
        private final long energy;
        private final long maxEnergy;

        public EnergyLevel(long energy, long maxEnergy) {
            this.energy = energy;
            this.maxEnergy = maxEnergy;
        }

        public long getEnergy() {
            return energy;
        }

        public long getMaxEnergy() {
            return maxEnergy;
        }
    }

    public static boolean isEnergyTE(TileEntity te, @Nullable Direction side) {
        if (te == null) {
            return false;
        }
        if (McJtyLib.tesla && TeslaCompatibility.isEnergyHandler(te, side)) {
            return true;
        }
        return te.getCapability(CapabilityEnergy.ENERGY, side).isPresent();
    }

    public static boolean isEnergyItem(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof IEnergyItem) {
            return true;
        }
        if (McJtyLib.tesla && TeslaCompatibility.isEnergyItem(stack)) {
            return true;
        }
        return stack.getCapability(CapabilityEnergy.ENERGY).isPresent();
    }

    // Get energy level with possible support for multiblocks (like EnderIO capacitor bank).
    public static EnergyLevel getEnergyLevelMulti(TileEntity tileEntity, @Nullable Direction side) {
        long maxEnergyStored;
        long energyStored;
        if (tileEntity instanceof IBigPower) {
            maxEnergyStored = ((IBigPower) tileEntity).getCapacity();
            energyStored = ((IBigPower) tileEntity).getStoredPower();
        } else if (McJtyLib.tesla && TeslaCompatibility.isEnergyHandler(tileEntity, side)) {
            maxEnergyStored = TeslaCompatibility.getMaxEnergy(tileEntity, side);
            energyStored = TeslaCompatibility.getEnergy(tileEntity, side);
        } else if (tileEntity != null) {
            return tileEntity.getCapability(CapabilityEnergy.ENERGY, side).map(h -> new EnergyLevel(h.getEnergyStored(), h.getMaxEnergyStored())).orElse(new EnergyLevel(0, 0));
        } else {
            maxEnergyStored = 0;
            energyStored = 0;
        }
        return new EnergyLevel(energyStored, maxEnergyStored);
    }

    public static EnergyLevel getEnergyLevel(TileEntity tileEntity, @Nullable Direction side) {
        AtomicLong maxEnergyStored = new AtomicLong();
        AtomicLong energyStored = new AtomicLong();
        if (McJtyLib.tesla && TeslaCompatibility.isEnergyHandler(tileEntity, side)) {
            maxEnergyStored.set(TeslaCompatibility.getMaxEnergy(tileEntity, side));
            energyStored.set(TeslaCompatibility.getEnergy(tileEntity, side));
        } else if (tileEntity != null) {
            tileEntity.getCapability(CapabilityEnergy.ENERGY, side).ifPresent(handler -> {
                maxEnergyStored.set(handler.getMaxEnergyStored());
                energyStored.set(handler.getEnergyStored());
            });
        } else {
            maxEnergyStored.set(0);
            energyStored.set(0);
        }
        return new EnergyLevel(energyStored.get(), maxEnergyStored.get());
    }

    public static long receiveEnergy(TileEntity tileEntity, Direction from, long maxReceive) {
        if (McJtyLib.tesla && TeslaCompatibility.isEnergyReceiver(tileEntity, from)) {
            return TeslaCompatibility.receiveEnergy(tileEntity, from, maxReceive);
        } else if (tileEntity != null) {
            return tileEntity.getCapability(CapabilityEnergy.ENERGY, from).map(handler ->
                    handler.receiveEnergy(unsignedClampToInt(maxReceive), false)).orElse(0);
        }
        return 0;
    }

    public static long receiveEnergy(ItemStack stack, long maxReceive) {
        Item item = stack.getItem();
        if (item instanceof IEnergyItem) {
            return ((IEnergyItem)item).receiveEnergyL(stack, maxReceive, false);
        } else if (McJtyLib.tesla && TeslaCompatibility.isEnergyItem(stack)) {
            return TeslaCompatibility.receiveEnergy(stack, maxReceive, false);
        } else {
            return stack.getCapability(CapabilityEnergy.ENERGY).map(handler ->
                    handler.receiveEnergy(unsignedClampToInt(maxReceive), false)).orElse(0);
        }
    }

    public static int unsignedClampToInt(long l) {
        return l > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)l;
    }

    /**
     * Some energy APIs only support ints for energy, not longs.
     * This function makes sure that these APIs never incorrectly think that
     * larger-than-int storage is too full or empty to perform operations on.
     *
     * @param energyStored The actual energy stored
     * @param maxEnergyStored The actual max energy stored
     * @return The energy stored to report to APIs that don't support longs
     */
    public static int getIntEnergyStored(long energyStored, long maxEnergyStored) {
        return unsignedClampToInt(energyStored); // the below is too risky until everything is converted to read longs
//        if(maxEnergyStored <= Integer.MAX_VALUE) {
//            // Easy case: everything naturally fits in ints already
//            return (int)energyStored;
//        }
//        if(energyStored <= 0x3FFF_FFFF) {
//            // Very little energy is stored. Return the amount such that the integer API will know the true energy stored
//            return (int)energyStored;
//        }
//        long remainingCapacity = maxEnergyStored - energyStored;
//        if(remainingCapacity <= 0x3FFF_FFFF) {
//            // Very little capacity remains. Return the amount such that the integer API will know the true remaining capacity
//            return Integer.MAX_VALUE - (int)remainingCapacity;
//        }
//        // All of the numbers involved are so high that we can't return the true energy stored or remaining capacity.
//        // We can only fit one bit of useful information: whether or not it's half full
//        return energyStored < remainingCapacity ? 0x3FFF_FFFF : 0x4000_0000;
    }

    /**
     * Send out energy to all adjacent devices that support receiving energy
     */
    public static void handleSendingEnergy(World world, BlockPos pos, long storedPower, long sendPerTick, GenericEnergyStorage storage) {
        for (Direction facing : OrientationTools.DIRECTION_VALUES) {
            BlockPos p = pos.relative(facing);
            TileEntity te = world.getBlockEntity(p);
            Direction opposite = facing.getOpposite();
            if (EnergyTools.isEnergyTE(te, opposite)) {
                long rfToGive = Math.min(sendPerTick, storedPower);
                long received = EnergyTools.receiveEnergy(te, opposite, rfToGive);
                storage.consumeEnergy(received);
                storedPower -= received;
                if (storedPower <= 0) {
                    break;
                }
            }
        }
    }


}
