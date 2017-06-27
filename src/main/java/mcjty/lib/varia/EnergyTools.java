package mcjty.lib.varia;

import mcjty.lib.McJtyLib;
import mcjty.lib.compat.RedstoneFluxCompatibility;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Loader;

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
        if (te == null) {
            return false;
        }
        if (McJtyLib.redstoneflux && RedstoneFluxCompatibility.isEnergyHandler(te)) {
            return true;
        }
        return te.hasCapability(CapabilityEnergy.ENERGY, null);
    }

    private static boolean draconic = false;
    private static boolean mekanism = false;
    private static boolean enderio = false;

    private static boolean checkMods = true;

    private static void doCheckMods() {
        if (checkMods) {
            draconic = Loader.isModLoaded("draconicevolution");
            mekanism = Loader.isModLoaded("mekanism") || Loader.isModLoaded("Mekanism");
            enderio = Loader.isModLoaded("EnderIO") || Loader.isModLoaded("enderio");
            checkMods = false;
        }
    }

    // Get energy level with possible support for multiblocks (like EnderIO capacitor bank).
    public static EnergyLevelMulti getEnergyLevelMulti(TileEntity tileEntity) {
        long maxEnergyStored;
        long energyStored;
        doCheckMods();
        if (draconic && EnergySupportDraconic.isDraconicEnergyTile(tileEntity)) {
            maxEnergyStored = EnergySupportDraconic.getMaxEnergy(tileEntity);
            energyStored = EnergySupportDraconic.getCurrentEnergy(tileEntity);
        } else if (mekanism && EnergySupportMekanism.isMekanismTileEntity(tileEntity)) {
            maxEnergyStored = EnergySupportMekanism.getMaxEnergy(tileEntity);
            energyStored = EnergySupportMekanism.getCurrentEnergy(tileEntity);
        } else if (enderio && EnergySupportEnderIO.isEnderioTileEntity(tileEntity)) {
            maxEnergyStored = EnergySupportEnderIO.getMaxEnergy(tileEntity);
            energyStored = EnergySupportEnderIO.getCurrentEnergy(tileEntity);
        } else if (McJtyLib.redstoneflux && RedstoneFluxCompatibility.isEnergyHandler(tileEntity)) {
            maxEnergyStored = RedstoneFluxCompatibility.getEnergy(tileEntity);
            energyStored = RedstoneFluxCompatibility.getMaxEnergy(tileEntity);
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
        if (McJtyLib.redstoneflux && RedstoneFluxCompatibility.isEnergyHandler(tileEntity)) {
            maxEnergyStored = RedstoneFluxCompatibility.getEnergy(tileEntity);
            energyStored = RedstoneFluxCompatibility.getMaxEnergy(tileEntity);
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
        if (McJtyLib.redstoneflux && RedstoneFluxCompatibility.isEnergyReceiver(tileEntity)) {
            return RedstoneFluxCompatibility.receiveEnergy(tileEntity, from, maxReceive);
        } else if (tileEntity != null && tileEntity.hasCapability(CapabilityEnergy.ENERGY, from)) {
            IEnergyStorage capability = tileEntity.getCapability(CapabilityEnergy.ENERGY, from);
            if (capability.canReceive()) {
                return capability.receiveEnergy(maxReceive, false);
            }
        }
        return 0;
    }
}
