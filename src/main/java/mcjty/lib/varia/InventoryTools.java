package mcjty.lib.varia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

import javax.annotation.Nonnull;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class InventoryTools {
    @SuppressWarnings("unchecked")
    public static net.neoforged.neoforge.transfer.IndexModifier<ItemResource> modifier(ResourceHandler<ItemResource> handler) {
        if (handler instanceof net.neoforged.neoforge.transfer.IndexModifier<?> modifier) {
            return (net.neoforged.neoforge.transfer.IndexModifier<ItemResource>) modifier;
        }
        if (handler instanceof net.neoforged.neoforge.transfer.StacksResourceHandler<?, ?> stacks) {
            return ((net.neoforged.neoforge.transfer.StacksResourceHandler<?, ItemResource>) stacks)::set;
        }
        throw new IllegalArgumentException("Menu inventories must expose an IndexModifier for direct slot changes");
    }

    public static ItemStack insertItem(ResourceHandler<ItemResource> handler, ItemStack stack, boolean simulate) {
        return net.neoforged.neoforge.transfer.item.ItemUtil.insertItemReturnRemaining(handler, stack, simulate, null);
    }

    public static ItemStack insertItem(ResourceHandler<ItemResource> handler, int slot, ItemStack stack, boolean simulate) {
        return net.neoforged.neoforge.transfer.item.ItemUtil.insertItemReturnRemaining(handler, slot, stack, simulate, null);
    }

    public static ItemStack extractItem(ResourceHandler<ItemResource> handler, int slot, int amount, boolean simulate) {
        var resource = handler.getResource(slot);
        if (resource.isEmpty()) return ItemStack.EMPTY;
        try (var transaction = net.neoforged.neoforge.transfer.transaction.Transaction.openRoot()) {
            int extracted = handler.extract(slot, resource, amount, transaction);
            if (!simulate) transaction.commit();
            return resource.toStack(extracted);
        }
    }

    /**
     * Get the size of the inventory
     */
    public static int getInventorySize(BlockEntity tileEntity) {
        if (tileEntity == null) {
            return 0;
        }

        ResourceHandler<ItemResource> handler = mcjty.lib.varia.CapabilityTools.itemHandler(tileEntity.getLevel().getCapability(Capabilities.Item.BLOCK, tileEntity.getBlockPos(), null));
        if (handler != null) {
            return handler.size();
        } else {
            return 0;
        }
    }

    public static boolean isInventory(BlockEntity te) {
        return te != null && mcjty.lib.varia.CapabilityTools.itemHandler(te.getLevel().getCapability(Capabilities.Item.BLOCK, te.getBlockPos(), null)) != null;
    }

    /**
     * Return a stream of items in an inventory matching the predicate
     */
    public static Stream<ItemStack> getItems(BlockEntity tileEntity, Predicate<ItemStack> predicate) {
        Stream.Builder<ItemStack> builder = Stream.builder();

        if (tileEntity != null) {
            ResourceHandler<ItemResource> handler = mcjty.lib.varia.CapabilityTools.itemHandler(tileEntity.getLevel().getCapability(Capabilities.Item.BLOCK, tileEntity.getBlockPos(), null));
            if (handler != null) {
                for (int i = 0; i < handler.size(); i++) {
                    ItemStack itemStack = net.neoforged.neoforge.transfer.item.ItemUtil.getStack(handler, i);
                    if (!itemStack.isEmpty() && predicate.test(itemStack)) {
                        builder.add(itemStack);
                    }
                }
            }
        }
        return builder.build();
    }

    /**
     * Return the first item in an inventory matching the predicate
     */
    @Nonnull
    public static ItemStack getFirstMatchingItem(BlockEntity tileEntity, Predicate<ItemStack> predicate) {
        if (tileEntity != null) {
            ResourceHandler<ItemResource> handler = mcjty.lib.varia.CapabilityTools.itemHandler(tileEntity.getLevel().getCapability(Capabilities.Item.BLOCK, tileEntity.getBlockPos(), null));
            if (handler != null) {
                for (int i = 0; i < handler.size(); i++) {
                    ItemStack itemStack = net.neoforged.neoforge.transfer.item.ItemUtil.getStack(handler, i);
                    if (!itemStack.isEmpty() && predicate.test(itemStack)) {
                        return itemStack;
                    }
                }
                return ItemStack.EMPTY;
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Insert an item into an inventory at the given direction. Supports ResourceHandler<ItemResource> as
     * well as IInventory. Returns an itemstack with whatever could not be inserted or empty item
     * on succcess.
     */
    @Nonnull
    public static ItemStack insertItem(Level world, BlockPos pos, Direction direction, @Nonnull ItemStack s) {
        BlockEntity te = world.getBlockEntity(direction == null ? pos : pos.relative(direction));
        if (te != null) {
            Direction opposite = direction == null ? null : direction.getOpposite();
            ResourceHandler<ItemResource> handler = mcjty.lib.varia.CapabilityTools.itemHandler(te.getLevel().getCapability(Capabilities.Item.BLOCK, te.getBlockPos(), opposite));
            if (handler != null) {
                return InventoryTools.insertItem(handler, s, false);
            } else {
                return s;
            }
        }
        return s;
    }

    public static boolean isItemStackConsideredEqual(ItemStack result, ItemStack itemstack1) {
        // @todo 1.20 isSameItemSameTags?
        return !itemstack1.isEmpty() && itemstack1.getItem() == result.getItem() && (result.getDamageValue() == itemstack1.getDamageValue()) && ItemStack.isSameItemSameComponents(result, itemstack1);
    }

    @Nonnull
    public static ItemStack insertItemRanged(ResourceHandler<ItemResource> dest, @Nonnull ItemStack stack, int start, int stop, boolean simulate) {
        if (dest == null || stack.isEmpty())
            return stack;

        for (int i = start; i < stop; i++) {
            stack = insertItem(dest, i, stack, simulate);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        return stack;
    }
}
