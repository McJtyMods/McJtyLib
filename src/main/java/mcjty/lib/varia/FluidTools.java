package mcjty.lib.varia;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidTools {

    /**
     * Make sure the forge bucket is enabled. If needed do this in your mod constructor:
     * FluidRegistry.enableUniversalBucket();
     *
     * @param fluidStack
     * @return
     */
    @Nonnull
    public static ItemStack convertFluidToBucket(@Nonnull FluidStack fluidStack) {
        //                return FluidContainerRegistry.fillFluidContainer(fluidStack, new ItemStack(Items.BUCKET));
        return FluidUtil.getFluidHandler(new ItemStack(Items.BUCKET)).map(handler -> {
            handler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
            return handler.getContainer();
        }).orElse(ItemStack.EMPTY);
    }

    @Nullable
    public static FluidStack convertBucketToFluid(@Nonnull ItemStack bucket) {
        // @todo 1.14: return null in LazyOptional?
        return FluidUtil.getFluidHandler(bucket).map(handler -> {
            for (int i = 0 ; i < handler.getTanks() ; i++) {
                FluidStack contents = handler.getFluidInTank(i);
                if (!contents.isEmpty()) {
                    return contents;
                }
            }
            return null;
        }).orElse(null);
    }


    public static boolean isEmptyContainer(@Nonnull ItemStack itemStack) {
        return FluidUtil.getFluidHandler(itemStack).map(handler -> {
            for (int i = 0 ; i < handler.getTanks() ; i++) {
                if (handler.getTankCapacity(i) > 0) {
                    FluidStack contents = handler.getFluidInTank(i);
                    if (contents.isEmpty()) {
                        return true;
                    } else if (contents.getAmount() > 0) {
                        return false;
                    }
                }
            }
            return false;
        }).orElse(false);
    }

    public static boolean isFilledContainer(@Nonnull ItemStack itemStack) {
        return FluidUtil.getFluidHandler(itemStack).map(handler -> {
            for (int i = 0 ; i < handler.getTanks() ; i++) {
                FluidStack contents = handler.getFluidInTank(i);
                if (contents.isEmpty() || contents.getAmount() < handler.getTankCapacity(i)) {
                    return false;
                }
            }
            return true;
        }).orElse(false);
    }

    // Drain a fluid container and return an empty container
    @Nonnull
    public static ItemStack drainContainer(@Nonnull ItemStack container) {
        ItemStack empty = container.copy();
        empty.setCount(1);
        return FluidUtil.getFluidHandler(empty).map(handler -> {
            if (!handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE).isEmpty()) {
                return handler.getContainer();
            }
            return ItemStack.EMPTY;
        }).orElse(ItemStack.EMPTY);
    }

    // Fill a container with a fluid and return the filled container
    @Nonnull
    public static ItemStack fillContainer(@Nonnull FluidStack fluidStack, @Nonnull ItemStack itemStack) {
        return FluidUtil.getFluidHandler(itemStack.copy()).map(handler -> {
            int filled = handler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
            if (filled == 0) {
                return ItemStack.EMPTY;
            }
            return handler.getContainer();
        }).orElse(ItemStack.EMPTY);
    }

    /**
     * Get the capacity (in mb) of the given container for the given fluid
     */
    // @todo 1.14
//    public static int getCapacity(@Nonnull FluidStack fluidStack, @Nonnull ItemStack itemStack) {
//        return FluidUtil.getFluidHandler(itemStack).map(handler -> {
//            IFluidTankProperties[] tankProperties = handler.getTankProperties();
//            for (IFluidTankProperties properties : tankProperties) {
//                if (properties.canDrainFluidType(fluidStack)) {
//                    return properties.getCapacity();
//                }
//            }
//            return 0;
//        }).orElse(0);
//    }
}
