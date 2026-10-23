package mcjty.lib.varia;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.capabilities.Capabilities;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class FluidTools {

    @Nonnull
    public static ItemStack convertFluidToBucket(@Nonnull FluidStack fluidStack) {
        return fillContainer(fluidStack, new ItemStack(Items.BUCKET));
    }

    @Nonnull
    public static FluidStack convertBucketToFluid(@Nonnull ItemStack bucket) {
        return FluidUtil.getFirstStackContained(bucket);
    }

    private static ResourceHandler<FluidResource> getHandler(ItemStack stack) {
        return stack.isEmpty() ? null : ItemAccess.forStack(stack).oneByOne().getCapability(Capabilities.Fluid.ITEM);
    }

    public static boolean isEmptyContainer(@Nonnull ItemStack stack) {
        var handler = getHandler(stack);
        if (handler == null) return false;
        for (int i = 0; i < handler.size(); i++) {
            var resource = handler.getResource(i);
            if (handler.getCapacityAsLong(i, resource) > 0) return handler.getAmountAsLong(i) == 0;
        }
        return false;
    }

    public static boolean isFilledContainer(@Nonnull ItemStack stack) {
        var handler = getHandler(stack);
        if (handler == null || handler.size() == 0) return false;
        for (int i = 0; i < handler.size(); i++) {
            var resource = handler.getResource(i);
            if (resource.isEmpty() || handler.getAmountAsLong(i) < handler.getCapacityAsLong(i, resource)) return false;
        }
        return true;
    }

    // Use a slot-backed access so a bucket can change its underlying item.
    private static ItemStacksResourceHandler containerSlot(ItemStack stack) {
        var slot = new ItemStacksResourceHandler(1);
        if (!stack.isEmpty()) slot.set(0, ItemResource.of(stack), 1);
        return slot;
    }

    @Nonnull
    public static ItemStack drainContainer(@Nonnull ItemStack container) {
        if (container.isEmpty()) return ItemStack.EMPTY;
        var slot = containerSlot(container);
        var handler = ItemAccess.forHandlerIndexStrict(slot, 0).getCapability(Capabilities.Fluid.ITEM);
        if (handler == null) return ItemStack.EMPTY;
        try (var transaction = Transaction.openRoot()) {
            for (int i = 0; i < handler.size(); i++) {
                var resource = handler.getResource(i);
                if (!resource.isEmpty() && handler.extract(resource, Integer.MAX_VALUE, transaction) > 0) {
                    transaction.commit();
                    return slot.getResource(0).toStack(slot.getAmountAsInt(0));
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Nonnull
    public static ItemStack fillContainer(@Nonnull FluidStack fluid, @Nonnull ItemStack container) {
        if (fluid.isEmpty() || container.isEmpty()) return ItemStack.EMPTY;
        var slot = containerSlot(container);
        var handler = ItemAccess.forHandlerIndexStrict(slot, 0).getCapability(Capabilities.Fluid.ITEM);
        if (handler == null) return ItemStack.EMPTY;
        try (var transaction = Transaction.openRoot()) {
            if (handler.insert(FluidResource.of(fluid), fluid.getAmount(), transaction) > 0) {
                transaction.commit();
                return slot.getResource(0).toStack(slot.getAmountAsInt(0));
            }
        }
        return ItemStack.EMPTY;
    }

    @Nonnull
    public static FluidStack pickupFluidBlock(Level world, BlockPos pos, @Nonnull Predicate<FluidStack> action, @Nonnull Runnable clearBlock) {
        BlockState blockstate = world.getBlockState(pos);
        FluidState fluidstate = world.getFluidState(pos);
        Fluid fluid = fluidstate.getType();

        if (blockstate.getBlock() instanceof LiquidBlock) {
            FluidStack stack = new FluidStack(fluid, FluidType.BUCKET_VOLUME);
            if (action.test(stack)) {
                clearBlock.run();
            }
            return stack;
        } else if (blockstate.getBlock() instanceof BucketPickup && fluid != Fluids.EMPTY) {
            FluidStack stack = new FluidStack(fluid, FluidType.BUCKET_VOLUME);
            if (action.test(stack)) {
                ItemStack fluidBucket = ((BucketPickup) blockstate.getBlock()).pickupBlock(null, world, pos, blockstate);
                return FluidUtil.getFirstStackContained(fluidBucket).copyWithAmount(FluidType.BUCKET_VOLUME);
            }
            return stack;
        } else if (blockstate.getBlock() instanceof BucketPickup) { // @todo 1.20 right?
            FluidStack stack = new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME);
            if (action.test(stack)) {
                BlockEntity tileentity = blockstate.getBlock() instanceof EntityBlock ? world.getBlockEntity(pos) : null;
                Block.dropResources(blockstate, world, pos, tileentity);
                clearBlock.run();
            }
            return stack;
        }
        return FluidStack.EMPTY;
    }
}
