package mcjty.lib.varia;

import javax.annotation.Nullable;

import mcjty.lib.McJtyLib;
import mcjty.lib.api.power.IBigPower;
import mcjty.lib.compat.EnergySupportDraconic;
import mcjty.lib.compat.EnergySupportEnderIO;
import mcjty.lib.compat.EnergySupportMekanism;
import mcjty.lib.compat.RedstoneFluxCompatibility;
import mcjty.lib.compat.TeslaCompatibility;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Loader;

public class EnergyTools {

    @CapabilityInject(ITeslaHolder.class)
    public static Capability<ITeslaHolder> TESLA_HOLDER = null;

    @CapabilityInject(ITeslaConsumer.class)
    public static Capability<ITeslaConsumer> TESLA_CONSUMER = null;

    @CapabilityInject(ITeslaProducer.class)
    public static Capability<ITeslaProducer> TESLA_PRODUCER = null;

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

    public static boolean isEnergyTE(TileEntity te, @Nullable EnumFacing side) {
        if (te == null) {
            return false;
        }
        if (McJtyLib.tesla && TeslaCompatibility.isEnergyHandler(te, side)) {
            return true;
        }
        if (McJtyLib.redstoneflux && RedstoneFluxCompatibility.isEnergyHandler(te)) {
            return true;
        }
        return te.hasCapability(CapabilityEnergy.ENERGY, side);
    }

    public static boolean isEnergyItem(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof IEnergyItem) {
            return true;
        }
        if (McJtyLib.tesla && TeslaCompatibility.isEnergyItem(stack)) {
            return true;
        }
        if (McJtyLib.redstoneflux && RedstoneFluxCompatibility.isEnergyItem(item)) {
            return true;
        }
        return stack.hasCapability(CapabilityEnergy.ENERGY, null);
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
    public static EnergyLevel getEnergyLevelMulti(TileEntity tileEntity, @Nullable EnumFacing side) {
        long maxEnergyStored;
        long energyStored;
        doCheckMods();
        if (tileEntity instanceof IBigPower) {
            maxEnergyStored = ((IBigPower) tileEntity).getCapacity();
            energyStored = ((IBigPower) tileEntity).getStoredPower();
        } else if (McJtyLib.tesla && TeslaCompatibility.isEnergyHandler(tileEntity, side)) {
            maxEnergyStored = TeslaCompatibility.getMaxEnergy(tileEntity, side);
            energyStored = TeslaCompatibility.getEnergy(tileEntity, side);
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
            maxEnergyStored = RedstoneFluxCompatibility.getMaxEnergy(tileEntity, side);
            energyStored = RedstoneFluxCompatibility.getEnergy(tileEntity, side);
        } else if (tileEntity != null && tileEntity.hasCapability(CapabilityEnergy.ENERGY, side)) {
            IEnergyStorage energy = tileEntity.getCapability(CapabilityEnergy.ENERGY, side);
            maxEnergyStored = energy.getMaxEnergyStored();
            energyStored = energy.getEnergyStored();
        } else {
            maxEnergyStored = 0;
            energyStored = 0;
        }
        return new EnergyLevel(energyStored, maxEnergyStored);
    }

    public static EnergyLevel getEnergyLevel(TileEntity tileEntity, @Nullable EnumFacing side) {
        long maxEnergyStored;
        long energyStored;
        if (McJtyLib.tesla && TeslaCompatibility.isEnergyHandler(tileEntity, side)) {
            maxEnergyStored = TeslaCompatibility.getMaxEnergy(tileEntity, side);
            energyStored = TeslaCompatibility.getEnergy(tileEntity, side);
        } else if (McJtyLib.redstoneflux && RedstoneFluxCompatibility.isEnergyHandler(tileEntity)) {
            maxEnergyStored = RedstoneFluxCompatibility.getMaxEnergy(tileEntity, side);
            energyStored = RedstoneFluxCompatibility.getEnergy(tileEntity, side);
        } else if (tileEntity != null && tileEntity.hasCapability(CapabilityEnergy.ENERGY, side)) {
            IEnergyStorage energy = tileEntity.getCapability(CapabilityEnergy.ENERGY, side);
            maxEnergyStored = energy.getMaxEnergyStored();
            energyStored = energy.getEnergyStored();
        } else {
            maxEnergyStored = 0;
            energyStored = 0;
        }
        return new EnergyLevel(energyStored, maxEnergyStored);
    }

    public static long receiveEnergy(TileEntity tileEntity, EnumFacing from, long maxReceive) {
        if (McJtyLib.tesla && TeslaCompatibility.isEnergyReceiver(tileEntity, from)) {
            return TeslaCompatibility.receiveEnergy(tileEntity, from, maxReceive);
        } else if (McJtyLib.redstoneflux && RedstoneFluxCompatibility.isEnergyReceiver(tileEntity)) {
            return RedstoneFluxCompatibility.receiveEnergy(tileEntity, from, unsignedClampToInt(maxReceive));
        } else if (tileEntity != null && tileEntity.hasCapability(CapabilityEnergy.ENERGY, from)) {
            IEnergyStorage capability = tileEntity.getCapability(CapabilityEnergy.ENERGY, from);
            if (capability.canReceive()) {
                return capability.receiveEnergy(unsignedClampToInt(maxReceive), false);
            }
        }
        return 0;
    }

    public static long receiveEnergy(ItemStack stack, long maxReceive) {
        Item item = stack.getItem();
        if (item instanceof IEnergyItem) {
            return ((IEnergyItem)item).receiveEnergyL(stack, maxReceive, false);
        } else if (McJtyLib.tesla && TeslaCompatibility.isEnergyItem(stack)) {
            return TeslaCompatibility.receiveEnergy(stack, maxReceive, false);
        } else if (McJtyLib.redstoneflux && RedstoneFluxCompatibility.isEnergyItem(item)) {
            return RedstoneFluxCompatibility.receiveEnergy(item, stack, unsignedClampToInt(maxReceive), false);
        } else if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            IEnergyStorage capability = stack.getCapability(CapabilityEnergy.ENERGY, null);
            if (capability.canReceive()) {
                return capability.receiveEnergy(unsignedClampToInt(maxReceive), false);
            }
        }
        return 0;
    }

    public static int unsignedClampToInt(long l) {
        return l > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)l;
    }
}
