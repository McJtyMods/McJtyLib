package mcjty.lib.varia;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class InventoryTools {
    /**
     * Get the size of the inventory
     */
    public static int getInventorySize(TileEntity tileEntity) {
        if (tileEntity == null) {
            return 0;
        }

        return tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(IItemHandler::getSlots).orElse(0);
    }

    public static boolean isInventory(TileEntity te) {
        return te != null && te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent();
    }

    /**
     * Return a stream of items in an inventory matching the predicate
     *
     * @param tileEntity
     * @param predicate
     * @return
     */
    public static Stream<ItemStack> getItems(TileEntity tileEntity, Predicate<ItemStack> predicate) {
        Stream.Builder<ItemStack> builder = Stream.builder();

        if (tileEntity != null) {
            tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack itemStack = handler.getStackInSlot(i);
                    if (!itemStack.isEmpty() && predicate.test(itemStack)) {
                        builder.add(itemStack);
                    }
                }
            });
        }
        return builder.build();
    }

    /**
     * Return the first item in an inventory matching the predicate
     *
     * @param tileEntity
     * @param predicate
     * @return
     */
    @Nonnull
    public static ItemStack getFirstMatchingItem(TileEntity tileEntity, Predicate<ItemStack> predicate) {
        if (tileEntity != null) {
            return tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(handler -> {
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack itemStack = handler.getStackInSlot(i);
                    if (!itemStack.isEmpty() && predicate.test(itemStack)) {
                        return itemStack;
                    }
                }
                return ItemStack.EMPTY;
            }).orElse(ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }

    /**
     * Insert an item into an inventory at the given direction. Supports IItemHandler as
     * well as IInventory. Returns an itemstack with whatever could not be inserted or empty item
     * on succcess.
     */
    @Nonnull
    public static ItemStack insertItem(World world, BlockPos pos, Direction direction, @Nonnull ItemStack s) {
        TileEntity te = world.getBlockEntity(direction == null ? pos : pos.relative(direction));
        if (te != null) {
            Direction opposite = direction == null ? null : direction.getOpposite();
            return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, opposite)
                    .map(handler -> ItemHandlerHelper.insertItem(handler, s, false))
                    .orElse(ItemStack.EMPTY);
        }
        return s;
    }

    public static boolean isItemStackConsideredEqual(ItemStack result, ItemStack itemstack1) {
        // @todo 1.14
//        return !itemstack1.isEmpty() && itemstack1.getItem() == result.getItem() && (!result.getHasSubtypes() || result.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(result, itemstack1);
        return !itemstack1.isEmpty() && itemstack1.getItem() == result.getItem() && (result.getDamageValue() == itemstack1.getDamageValue()) && ItemStack.tagMatches(result, itemstack1);
    }

    @Nonnull
    public static ItemStack insertItemRanged(IItemHandler dest, @Nonnull ItemStack stack, int start, int stop, boolean simulate) {
        if (dest == null || stack.isEmpty())
            return stack;

        for (int i = start; i < stop; i++) {
            stack = dest.insertItem(i, stack, simulate);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        return stack;
    }
}
