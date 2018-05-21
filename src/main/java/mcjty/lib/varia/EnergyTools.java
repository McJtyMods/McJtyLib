package mcjty.lib.varia;

import mcjty.lib.McJtyLib;
import mcjty.lib.api.power.IBigPower;
import mcjty.lib.compat.EnergySupportDraconic;
import mcjty.lib.compat.EnergySupportEnderIO;
import mcjty.lib.compat.EnergySupportMekanism;
import mcjty.lib.compat.RedstoneFluxCompatibility;
import mcjty.lib.compat.TeslaCompatibility;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Loader;

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

    public static boolean isEnergyTE(TileEntity te) {
        if (te == null) {
            return false;
        }
        if (McJtyLib.tesla && TeslaCompatibility.isEnergyHandler(te, null)) {
            return true;
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
            mekanism = Loader.isModLoaded("mekanism");
            enderio = Loader.isModLoaded("enderio");
            checkMods = false;
        }
    }

    // Get energy level with possible support for multiblocks (like EnderIO capacitor bank).
    public static EnergyLevel getEnergyLevelMulti(TileEntity tileEntity) {
        long maxEnergyStored;
        long energyStored;
        doCheckMods();
        if (tileEntity instanceof IBigPower) {
            maxEnergyStored = ((IBigPower) tileEntity).getBigMaxEnergy();
            energyStored = ((IBigPower) tileEntity).getBigEnergy();
        } else if (McJtyLib.tesla && TeslaCompatibility.isEnergyHandler(tileEntity, null)) {
            maxEnergyStored = TeslaCompatibility.getMaxEnergy(tileEntity, null);
            energyStored = TeslaCompatibility.getEnergy(tileEntity, null);
        } else if (draconic && EnergySupportDraconic.isDraconicEnergyTile(tileEntity)) {
            maxEnergyStored = EnergySupportDraconic.getMaxEnergy(tileEntity);
            energyStored = EnergySupportDraconic.getCurrentEnergy(tileEntity);
        } else if (mekanism && EnergySupportMekanism.isMekanismTileEntity(tileEntity)) {
            maxEnergyStored = EnergySupportMekanism.getMaxEnergy(tileEntity);
            energyStored = EnergySupportMekanism.getCurrentEnergy(tileEntity);
        } else if (enderio && EnergySupportEnderIO.isEnderioTileEntity(tileEntity)) {
            maxEnergyStored = EnergySupportEnderIO.getMaxEnergy(tileEntity);
            energyStored = EnergySupportEnderIO.getCurrentEnergy(tileEntity);
        } else if (McJtyLib.redstoneflux && RedstoneFluxCompatibility.isEnergyHandler(tileEntity)) {
            maxEnergyStored = RedstoneFluxCompatibility.getMaxEnergy(tileEntity);
            energyStored = RedstoneFluxCompatibility.getEnergy(tileEntity);
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

    public static EnergyLevel getEnergyLevel(TileEntity tileEntity) {
        long maxEnergyStored;
        long energyStored;
        if (McJtyLib.tesla && TeslaCompatibility.isEnergyHandler(tileEntity, null)) {
            maxEnergyStored = TeslaCompatibility.getMaxEnergy(tileEntity, null);
            energyStored = TeslaCompatibility.getEnergy(tileEntity, null);
        } else if (McJtyLib.redstoneflux && RedstoneFluxCompatibility.isEnergyHandler(tileEntity)) {
            maxEnergyStored = RedstoneFluxCompatibility.getMaxEnergy(tileEntity);
            energyStored = RedstoneFluxCompatibility.getEnergy(tileEntity);
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

    public static long receiveEnergy(TileEntity tileEntity, EnumFacing from, int maxReceive) {
        if (McJtyLib.tesla && TeslaCompatibility.isEnergyReceiver(tileEntity, from)) {
            return TeslaCompatibility.receiveEnergy(tileEntity, from, maxReceive);
        } else if (McJtyLib.redstoneflux && RedstoneFluxCompatibility.isEnergyReceiver(tileEntity)) {
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
