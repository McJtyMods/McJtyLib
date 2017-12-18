package mcjty.lib.varia;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class CapabilityTools {

    public static boolean hasItemCapabilitySafe(TileEntity tileEntity) {
        try {
            if (tileEntity != null && tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            reportWrongBlock(tileEntity, e);
            return false;
        }
    }

    public static IItemHandler getItemCapabilitySafe(TileEntity tileEntity) {
        try {
            return tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        } catch (Exception e) {
            reportWrongBlock(tileEntity, e);
            return null;
        }
    }

    public static IFluidHandler hasFluidCapabilitySafe(TileEntity tileEntity) {
        try {
            if (tileEntity != null && tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
                IFluidHandler capability = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                if (capability == null) {
                    reportWrongBlock(tileEntity, null);
                }
                return capability;
            }
            return null;
        } catch (Exception e) {
            reportWrongBlock(tileEntity, e);
            return null;
        }
    }

    public static IFluidHandler getFluidCapabilitySafe(TileEntity tileEntity) {
        try {
            return tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        } catch (Exception e) {
            reportWrongBlock(tileEntity, e);
            return null;
        }
    }

    private static void reportWrongBlock(TileEntity tileEntity, Exception e) {
        if (tileEntity != null) {
            ResourceLocation name = tileEntity.getWorld().getBlockState(tileEntity.getPos()).getBlock().getRegistryName();
            Logging.logError("Block " + name.toString() + " at " + BlockPosTools.toString(tileEntity.getPos()) + " does not respect the capability API and crashes on null side.");
            Logging.logError("Please report to the corresponding mod. This is not a bug in RFTools!");
        }
        if (e != null) {
            Logging.logError("Exception", e);
        }
    }
}
