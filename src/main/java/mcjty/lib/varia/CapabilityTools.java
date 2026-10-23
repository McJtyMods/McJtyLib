package mcjty.lib.varia;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

import org.jspecify.annotations.Nullable;

public class CapabilityTools {

    @Nullable
    public static ResourceHandler<ItemResource> getItemCapabilitySafe(BlockEntity tileEntity) {
        if (tileEntity != null) {
            try {
                return mcjty.lib.varia.CapabilityTools.itemHandler(tileEntity.getLevel().getCapability(Capabilities.Item.BLOCK, tileEntity.getBlockPos(), null));
            } catch (RuntimeException e) {
                reportWrongBlock(tileEntity, e);
            }
        }
        return null;
    }

    @Nullable
    public static ResourceHandler<FluidResource> getFluidCapabilitySafe(BlockEntity tileEntity) {
        if (tileEntity != null) {
            try {
                return mcjty.lib.varia.CapabilityTools.fluidHandler(tileEntity.getLevel().getCapability(Capabilities.Fluid.BLOCK, tileEntity.getBlockPos(), null));
            } catch (RuntimeException e) {
                reportWrongBlock(tileEntity, e);
            }
        }
        return null;
    }

    private static void reportWrongBlock(BlockEntity tileEntity, Exception e) {
        if (tileEntity != null) {
            Identifier name = Tools.getId(tileEntity.getLevel().getBlockState(tileEntity.getBlockPos()));
            Logging.logError("Block " + name.toString() + " at " + BlockPosTools.toString(tileEntity.getBlockPos()) + " does not respect the capability API and crashes on null side.");
            Logging.logError("Please report to the corresponding mod. This is not a bug in RFTools!");
        }
        if (e != null) {
            Logging.logError("Exception", e);
        }
    }
    public static ResourceHandler<ItemResource> itemHandler(ResourceHandler<ItemResource> handler) {
        return handler;
    }

    public static EnergyHandler energyHandler(EnergyHandler handler) {
        return handler;
    }

    public static ResourceHandler<FluidResource> fluidHandler(ResourceHandler<FluidResource> handler) {
        return handler;
    }
}
