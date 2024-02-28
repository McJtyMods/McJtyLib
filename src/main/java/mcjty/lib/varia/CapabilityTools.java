package mcjty.lib.varia;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.capabilities.ForgeCapabilities;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class CapabilityTools {

    @Nonnull
    public static LazyOptional<IItemHandler> getItemCapabilitySafe(BlockEntity tileEntity) {
        if (tileEntity == null) {
            return LazyOptional.empty();
        }
        try {
            return tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER);
        } catch (RuntimeException e) {
            reportWrongBlock(tileEntity, e);
            return LazyOptional.empty();
        }
    }

    @Nonnull
    public static LazyOptional<IFluidHandler> getFluidCapabilitySafe(BlockEntity tileEntity) {
        if (tileEntity == null) {
            return LazyOptional.empty();
        }
        try {
            return tileEntity.getCapability(ForgeCapabilities.FLUID_HANDLER);
        } catch (RuntimeException e) {
            reportWrongBlock(tileEntity, e);
            return LazyOptional.empty();
        }
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
