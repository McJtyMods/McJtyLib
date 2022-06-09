package mcjty.lib.varia;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class CapabilityTools {

    @Nonnull
    public static LazyOptional<IItemHandler> getItemCapabilitySafe(BlockEntity tileEntity) {
        if (tileEntity == null) {
            return LazyOptional.empty();
        }
        try {
            return tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
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
            return tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
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
