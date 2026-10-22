package mcjty.lib.varia;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import org.jspecify.annotations.Nullable;

public class CapabilityTools {

    @Nullable
    public static IItemHandler getItemCapabilitySafe(BlockEntity tileEntity) {
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
    public static IFluidHandler getFluidCapabilitySafe(BlockEntity tileEntity) {
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
    public static net.neoforged.neoforge.items.IItemHandler itemHandler(net.neoforged.neoforge.transfer.ResourceHandler<net.neoforged.neoforge.transfer.item.ItemResource> handler) {
        return handler == null ? null : net.neoforged.neoforge.items.IItemHandler.of(handler);
    }
    public static net.neoforged.neoforge.energy.IEnergyStorage energyHandler(net.neoforged.neoforge.transfer.energy.EnergyHandler handler) {
        return handler == null ? null : net.neoforged.neoforge.energy.IEnergyStorage.of(handler);
    }
    public static net.neoforged.neoforge.fluids.capability.IFluidHandler fluidHandler(net.neoforged.neoforge.transfer.ResourceHandler<net.neoforged.neoforge.transfer.fluid.FluidResource> handler) {
        return handler == null ? null : net.neoforged.neoforge.fluids.capability.IFluidHandler.of(handler);
    }
}
