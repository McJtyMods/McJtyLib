package mcjty.lib.varia;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

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

    @Nonnull
    public static FluidStack convertBucketToFluid(@Nonnull ItemStack bucket) {
        return FluidUtil.getFluidHandler(bucket).map(handler -> {
            for (int i = 0; i < handler.getTanks(); i++) {
                FluidStack contents = handler.getFluidInTank(i);
                if (!contents.isEmpty()) {
                    return contents;
                }
            }
            return FluidStack.EMPTY;
        }).orElse(FluidStack.EMPTY);
    }


    public static boolean isEmptyContainer(@Nonnull ItemStack itemStack) {
        return FluidUtil.getFluidHandler(itemStack).map(handler -> {
            for (int i = 0; i < handler.getTanks(); i++) {
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
            for (int i = 0; i < handler.getTanks(); i++) {
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

    @Nonnull
    public static FluidStack pickupFluidBlock(Level world, BlockPos pos, @Nonnull Predicate<FluidStack> action, @Nonnull Runnable clearBlock) {
        BlockState blockstate = world.getBlockState(pos);
        FluidState fluidstate = world.getFluidState(pos);
        Material material = blockstate.getMaterial();
        Fluid fluid = fluidstate.getType();

        if (blockstate.getBlock() instanceof BucketPickup && fluid != Fluids.EMPTY) {
            FluidStack stack = new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME);
            if (action.test(stack)) {
                return new FluidStack(((BucketPickup) blockstate.getBlock()).takeLiquid(world, pos, blockstate), FluidAttributes.BUCKET_VOLUME);
            }
            return stack;
        } else if (blockstate.getBlock() instanceof LiquidBlock) {
            FluidStack stack = new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME);
            if (action.test(stack)) {
                clearBlock.run();
            }
            return stack;
        } else if (material == Material.WATER_PLANT || material == Material.REPLACEABLE_WATER_PLANT) {
            FluidStack stack = new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME);
            if (action.test(stack)) {
                BlockEntity tileentity = blockstate.getBlock().hasTileEntity(blockstate) ? world.getBlockEntity(pos) : null;
                Block.dropResources(blockstate, world, pos, tileentity);
                clearBlock.run();
            }
            return stack;
        }
        return FluidStack.EMPTY;
    }
}
