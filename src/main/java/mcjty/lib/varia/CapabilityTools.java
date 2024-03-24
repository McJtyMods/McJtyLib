package mcjty.lib.varia;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;

public class CapabilityTools {

    @Nullable
    public static IItemHandler getItemCapabilitySafe(BlockEntity tileEntity) {
        if (tileEntity != null) {
            try {
                return tileEntity.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, tileEntity.getBlockPos(), null);
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
                return tileEntity.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, tileEntity.getBlockPos(), null);
            } catch (RuntimeException e) {
                reportWrongBlock(tileEntity, e);
            }
        }
        return null;
    }

    private static void reportWrongBlock(BlockEntity tileEntity, Exception e) {
        if (tileEntity != null) {
            ResourceLocation name = Tools.getId(tileEntity.getLevel().getBlockState(tileEntity.getBlockPos()));
            Logging.logError("Block " + name.toString() + " at " + BlockPosTools.toString(tileEntity.getBlockPos()) + " does not respect the capability API and crashes on null side.");
            Logging.logError("Please report to the corresponding mod. This is not a bug in RFTools!");
        }
        if (e != null) {
            Logging.logError("Exception", e);
        }
    }
}
