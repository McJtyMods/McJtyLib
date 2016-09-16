package mcjty.lib.varia;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyTools {

    public static class EnergyLevel {
        private final int energy;
        private final int maxEnergy;

        public EnergyLevel(int energy, int maxEnergy) {
            this.energy = energy;
            this.maxEnergy = maxEnergy;
        }

        public int getEnergy() {
            return energy;
        }

        public int getMaxEnergy() {
            return maxEnergy;
        }
    }

    public static class EnergyLevelMulti {
        private final long energy;
        private final long maxEnergy;

        public EnergyLevelMulti(long energy, long maxEnergy) {
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

    public static boolean isEnergyTE(TileEntity te) {
        return te instanceof IEnergyHandler || (te != null && te.hasCapability(CapabilityEnergy.ENERGY, null));
    }

    // Get energy level with possible support for multiblocks (like EnderIO capacitor bank).
    public static EnergyLevelMulti getEnergyLevelMulti(TileEntity tileEntity) {
        long maxEnergyStored;
        long energyStored;
        if (tileEntity instanceof IEnergyHandler) {
            IEnergyHandler handler = (IEnergyHandler) tileEntity;
            maxEnergyStored = handler.getMaxEnergyStored(EnumFacing.DOWN);
            energyStored = handler.getEnergyStored(EnumFacing.DOWN);
        } else if (tileEntity != null && tileEntity.hasCapability(CapabilityEnergy.ENERGY, null)) {
            IEnergyStorage energy = tileEntity.getCapability(CapabilityEnergy.ENERGY, null);
            maxEnergyStored = energy.getMaxEnergyStored();
            energyStored = energy.getEnergyStored();
        } else {
            maxEnergyStored = 0;
            energyStored = 0;
        }
        return new EnergyLevelMulti(energyStored, maxEnergyStored);
    }

    public static EnergyLevel getEnergyLevel(TileEntity tileEntity) {
        int maxEnergyStored;
        int energyStored;
        if (tileEntity instanceof IEnergyHandler) {
            IEnergyHandler handler = (IEnergyHandler) tileEntity;
            maxEnergyStored = handler.getMaxEnergyStored(EnumFacing.DOWN);
            energyStored = handler.getEnergyStored(EnumFacing.DOWN);
        } else if (tileEntity != null && tileEntity.hasCapability(CapabilityEnergy.ENERGY, null)) {
            IEnergyStorage energy = tileEntity.getCapability(CapabilityEnergy.ENERGY, null);
            maxEnergyStored = energy.getMaxEnergyStored();
            energyStored = energy.getEnergyStored();
        } else {
            maxEnergyStored = 0;
            energyStored = 0;
        }
        return new EnergyLevel(energyStored, maxEnergyStored);
    }

    public static int receiveEnergy(TileEntity tileEntity, EnumFacing from, int maxReceive) {
        if (tileEntity instanceof IEnergyReceiver) {
            return ((IEnergyReceiver) tileEntity).receiveEnergy(from, maxReceive, false);
        } else if (tileEntity != null && tileEntity.hasCapability(CapabilityEnergy.ENERGY, from)) {
            IEnergyStorage capability = tileEntity.getCapability(CapabilityEnergy.ENERGY, from);
            if (capability.canReceive()) {
                return capability.receiveEnergy(maxReceive, false);
            }
        }
        return 0;
    }
}
