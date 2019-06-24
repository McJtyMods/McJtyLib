package mcjty.lib.container;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.ItemStackList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InventoryHelper {
    private final GenericTileEntity tileEntity;
    private final ContainerFactory containerFactory;
    private ItemStackList stacks;
    private int count;

    public InventoryHelper(GenericTileEntity tileEntity, ContainerFactory containerFactory, int count) {
        this.tileEntity = tileEntity;
        this.containerFactory = containerFactory;
        stacks = ItemStackList.create(count);
        this.count = count;
    }

    public void setNewCount(int newcount) {
        this.count = newcount;
        ItemStackList newstacks = ItemStackList.create(newcount);
        for (int i = 0 ; i < Math.min(stacks.size(), newstacks.size()) ; i++) {
            newstacks.set(i, stacks.get(i));
        }
        stacks = newstacks;
    }

    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = stacks.get(index);
        setStackInSlot(index, ItemStack.EMPTY);
        return stack;
    }

    /**
     * Handle a slot from an inventory and consume it
     * @param tileEntity
     * @param slot
     * @param consumer
     */
    public static void handleSlot(TileEntity tileEntity, int slot, Consumer<ItemStack> consumer) {
        handleSlot(tileEntity, slot, -1, consumer);
    }

    /**
     * Handle a slot from an inventory and consume it
     * @param tileEntity
     * @param slot
     * @param amount (use -1 for entire stack)
     * @param consumer
     */
    public static void handleSlot(TileEntity tileEntity, int slot, int amount, Consumer<ItemStack> consumer) {
        if (tileEntity == null) {
            return;
        }
        int finalAmount = amount;
        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            ItemStack item = handler.getStackInSlot(slot);
            if (!item.isEmpty()) {
                int a = finalAmount;
                if (a == -1) {
                    a = item.getCount();
                }
                ItemStack stack = handler.extractItem(slot, a, false);
                if (!stack.isEmpty()) {
                    consumer.accept(stack);
                }
            }

        });
    }

    /**
     * Get the size of the inventory
     * @param tileEntity
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
     * Inject a module that the player is holding into the appropriate slots (slots are from start to stop inclusive both ends)
     * @return true if successful
     */
    public static boolean installModule(PlayerEntity player, ItemStack heldItem, Hand hand, BlockPos pos, int start, int stop) {
        World world = player.getEntityWorld();
        TileEntity te = world.getTileEntity(pos);
        if (te == null) {
            return false;
        }
        return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(inventory -> {
            for (int i = start ; i <= stop  ; i++) {
                if (inventory.getStackInSlot(i).isEmpty()) {
                    ItemStack copy = heldItem.copy();
                    copy.setCount(1);
                    if (inventory instanceof IItemHandlerModifiable) {
                        ((IItemHandlerModifiable) inventory).setStackInSlot(i, copy);
                    } else {
                        throw new IllegalStateException("Not an IItemHandlerModifiable!");
                    }
                    heldItem.shrink(1);
                    if (heldItem.isEmpty()) {
                        player.setHeldItem(hand, ItemStack.EMPTY);
                    }
                    if (world.isRemote) {
                        player.sendStatusMessage(new StringTextComponent("Installed module"), false);
                    }
                    return true;
                }
            }
            return false;
        }).orElse(false);
    }


    /**
     * Insert an item into an inventory at the given direction. Supports IItemHandler as
     * well as IInventory. Returns an itemstack with whatever could not be inserted or empty item
     * on succcess.
     */
    @Nullable
    public static ItemStack insertItem(World world, BlockPos pos, Direction direction, ItemStack s) {
        TileEntity te = world.getTileEntity(direction == null ? pos : pos.offset(direction));
        if (te != null) {
            Direction opposite = direction == null ? null : direction.getOpposite();
            return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, opposite)
                    .map(handler -> ItemHandlerHelper.insertItem(handler, s, false))
                    .orElse(ItemStack.EMPTY);
        }
        return s;
    }

    private static boolean insertItemsItemHandlerWithUndo(IItemHandler dest, List<ItemStack> stacks, boolean simulate) {
        if (dest == null || stacks == null || stacks.isEmpty()) {
            return true;
        }
        if (stacks.size() == 1) {
            // More optimal case
            ItemStack stack = stacks.get(0);
            stack = ItemHandlerHelper.insertItem(dest, stack, simulate);
            return stack.isEmpty();
        }

        List<ItemStack> s = stacks.stream().map(ItemStack::copy).collect(Collectors.toList());
        for (int i = 0; i < dest.getSlots(); i++) {
            boolean empty = true;
            for (int j = 0 ; j < stacks.size() ; j++) {
                ItemStack stack = dest.insertItem(i, s.get(j), simulate);
                if (!stack.isEmpty()) {
                    empty = false;
                }
                s.set(j, stack);
            }
            if (empty) {
                return true;
            }
        }

        return false;
    }

    /**
     * Insert multiple items in an inventory. If it didn't work nothing happens and false
     * is returned. No items will be inserted in that case.
     */
    public static boolean insertItemsAtomic(List<ItemStack> items, TileEntity te, Direction side) {
        if (te == null) {
            return false;
        }
        return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).map(handler -> {
            if (!insertItemsItemHandlerWithUndo(handler, items, true)) {
                return false;
            }
            insertItemsItemHandlerWithUndo(handler, items, false);
            return true;
        }).orElse(false);
    }

    private static boolean isItemStackConsideredEqual(ItemStack result, ItemStack itemstack1) {
        // @todo 1.14
//        return !itemstack1.isEmpty() && itemstack1.getItem() == result.getItem() && (!result.getHasSubtypes() || result.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(result, itemstack1);
        return !itemstack1.isEmpty() && itemstack1.getItem() == result.getItem() && (result.getDamage() == itemstack1.getDamage()) && ItemStack.areItemStackTagsEqual(result, itemstack1);
    }

    public int getCount() {
        return count;
    }

    public ItemStack getStackInSlot(int index) {
        if (index >= stacks.size()) {
            return ItemStack.EMPTY;
        }

        return stacks.get(index);
    }

    public ItemStackList getStacks() {
        return stacks;
    }

    /**
     * This function sets a stack in a slot but doesn't check if this slot allows it.
     * @param index
     * @param stack
     */
    public void setStackInSlot(int index, ItemStack stack) {
        if (index >= stacks.size()) {
            return;
        }
        stacks.set(index, stack);
    }

    public boolean containsItem(int index) {
        if (index >= stacks.size()) {
            return false;
        }
        return !stacks.get(index).isEmpty();
    }

    public ItemStack decrStackSize(int index, int amount) {
        if (index >= stacks.size()) {
            return ItemStack.EMPTY;
        }

        if (containerFactory.isGhostSlot(index) || containerFactory.isGhostOutputSlot(index)) {
            ItemStack old = stacks.get(index);
            stacks.set(index, ItemStack.EMPTY);
            if (old.isEmpty()) {
                return ItemStack.EMPTY;
            }
            old.setCount(0);
            return old;
        } else {
            if (!stacks.get(index).isEmpty()) {
                if (stacks.get(index).getCount() <= amount) {
                    ItemStack old = stacks.get(index);
                    stacks.set(index, ItemStack.EMPTY);
                    tileEntity.markDirty();
                    return old;
                }
                ItemStack its = stacks.get(index).split(amount);
                if (stacks.get(index).isEmpty()) {
                    stacks.set(index, ItemStack.EMPTY);
                }
                tileEntity.markDirty();
                return its;
            }
            return ItemStack.EMPTY;
        }
    }

    public void setInventorySlotContents(int stackLimit, int index, ItemStack stack) {
        if (index >= stacks.size()) {
            return;
        }

        if (containerFactory.isGhostSlot(index)) {
            if (!stack.isEmpty()) {
                ItemStack stack1 = stack.copy();
                if (index < 9) {
                    stack1.setCount(1);
                }
                stacks.set(index, stack1);
            } else {
                stacks.set(index, ItemStack.EMPTY);
            }
        } else if (containerFactory.isGhostOutputSlot(index)) {
            if (!stack.isEmpty()) {
                stacks.set(index, stack.copy());
            } else {
                stacks.set(index, ItemStack.EMPTY);
            }
        } else {
            stacks.set(index, stack);
            if (!stack.isEmpty() && stack.getCount() > stackLimit) {
                if (stackLimit <= 0) {
                    stack.setCount(0);
                } else {
                    stack.setCount(stackLimit);
                }
            }
            tileEntity.markDirty();
        }
    }
}
