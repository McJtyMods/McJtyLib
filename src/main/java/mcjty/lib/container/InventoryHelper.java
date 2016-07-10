package mcjty.lib.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InventoryHelper {
    private final TileEntity tileEntity;
    private final ContainerFactory containerFactory;
    private ItemStack stacks[];
    private int count;

    public InventoryHelper(TileEntity tileEntity, ContainerFactory containerFactory, int count) {
        this.tileEntity = tileEntity;
        this.containerFactory = containerFactory;
        stacks = new ItemStack[count];
        this.count = count;
    }

    public void setNewCount(int newcount) {
        this.count = newcount;
        ItemStack newstacks[] = new ItemStack[newcount];
        System.arraycopy(stacks, 0, newstacks, 0, Math.min(stacks.length, newstacks.length));
        stacks = newstacks;
    }

    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = stacks[index];
        setStackInSlot(index, null);
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
        if (tileEntity != null && tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            IItemHandler capability = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            ItemStack item = capability.getStackInSlot(slot);
            if (item != null) {
                if (amount == -1) {
                    amount = item.stackSize;
                }
                ItemStack stack = capability.extractItem(slot, amount, false);
                if (stack != null) {
                    consumer.accept(stack);
                }
            }
        } else if (tileEntity instanceof IInventory) {
            IInventory inventory = (IInventory) tileEntity;
            ItemStack item = inventory.getStackInSlot(slot);
            if (item != null) {
                if (amount == -1) {
                    amount = item.stackSize;
                }
                ItemStack stack = inventory.decrStackSize(slot, amount);
                if (stack != null) {
                    consumer.accept(stack);
                }
            }
        }
    }

    /**
     * Extract itemstack out of a slot and return a new stack
     * @param tileEntity
     * @param slot
     * @param amount
     */
    public static ItemStack extractItem(TileEntity tileEntity, int slot, int amount) {
        if (tileEntity != null && tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            IItemHandler capability = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            return capability.extractItem(slot, amount, false);
        } else if (tileEntity instanceof IInventory) {
            IInventory inventory = (IInventory) tileEntity;
            return inventory.decrStackSize(slot, amount);
        }
        return null;
    }

    /**
     * Get the size of the inventory
     * @param tileEntity
     */
    public static int getInventorySize(TileEntity tileEntity) {
        if (tileEntity != null && tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            IItemHandler capability = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (capability == null) {
                return 0;
            }
            return capability.getSlots();
        } else if (tileEntity instanceof IInventory) {
            IInventory inventory = (IInventory) tileEntity;
            return inventory.getSizeInventory();
        }
        return 0;
    }

    /**
     * Get a slot
     * @param tileEntity
     * @param slot
     */
    public static ItemStack getSlot(TileEntity tileEntity, int slot) {
        if (tileEntity != null && tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            IItemHandler capability = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            return capability.getStackInSlot(slot);
        } else if (tileEntity instanceof IInventory) {
            IInventory inventory = (IInventory) tileEntity;
            return inventory.getStackInSlot(slot);
        }
        return null;
    }

    public static boolean isInventory(TileEntity te) {
        if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            return true;
        } else if (te instanceof IInventory) {
            return true;
        }
        return false;
    }

    /**
     * Return a stream of items in an inventory matching the predicate
     * @param tileEntity
     * @param predicate
     * @return
     */
    public static Stream<ItemStack> getItems(TileEntity tileEntity, Predicate<ItemStack> predicate) {
        Stream.Builder<ItemStack> builder = Stream.builder();

        if (tileEntity != null && tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            IItemHandler capability = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            for (int i = 0 ; i < capability.getSlots() ; i++) {
                ItemStack itemStack = capability.getStackInSlot(i);
                if (itemStack != null && predicate.test(itemStack)) {
                    builder.add(itemStack);
                }
            }
        } else if (tileEntity instanceof IInventory) {
            IInventory inventory = (IInventory) tileEntity;
            for (int i = 0 ; i < inventory.getSizeInventory() ; i++) {
                ItemStack itemStack = inventory.getStackInSlot(i);
                if (itemStack != null && predicate.test(itemStack)) {
                    builder.add(itemStack);
                }
            }
        }
        return builder.build();
    }

    /**
     * Inject a module that the player is holding into the appropriate slots (slots are from start to stop inclusive both ends)
     * @return true if successful
     */
    public static boolean installModule(EntityPlayer player, ItemStack heldItem, EnumHand hand, BlockPos pos, int start, int stop) {
        World world = player.getEntityWorld();
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof IInventory) {
            IInventory inventory = (IInventory) te;
            for (int i = start ; i <= stop  ; i++) {
                if (inventory.getStackInSlot(i) == null) {
                    ItemStack copy = heldItem.copy();
                    copy.stackSize = 1;
                    inventory.setInventorySlotContents(i, copy);
                    heldItem.stackSize--;
                    if (heldItem.stackSize == 0) {
                        player.setHeldItem(hand, null);
                    }
                    if (world.isRemote) {
                        player.addChatComponentMessage(new TextComponentString("Installed module"));
                    }
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Insert an item into an inventory at the given direction. Supports IItemHandler as
     * well as IInventory. Returns an itemstack with whatever could not be inserted or null
     * on succcess.
     */
    public static ItemStack insertItem(World world, BlockPos pos, EnumFacing direction, ItemStack s) {
        TileEntity te = world.getTileEntity(direction == null ? pos : pos.offset(direction));
        if (te != null) {
            EnumFacing opposite = direction == null ? null : direction.getOpposite();
            if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, opposite)) {
                IItemHandler capability = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, opposite);
                s = ItemHandlerHelper.insertItem(capability, s, false);
                if (s == null) {
                    return null;
                }
            } else if (te instanceof IInventory) {
                int i = mergeItemStackSafe((IInventory) te, true, opposite, s, 0, ((IInventory) te).getSizeInventory(), null);
                if (i == 0) {
                    return null;
                }
                s.stackSize = i;
            }
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
            return stack == null;
        }

        List<ItemStack> s = stacks.stream().map(ItemStack::copy).collect(Collectors.toList());
        for (int i = 0; i < dest.getSlots(); i++) {
            boolean empty = true;
            for (int j = 0 ; j < stacks.size() ; j++) {
                ItemStack stack = dest.insertItem(i, s.get(j), simulate);
                if (stack != null && stack.stackSize > 0) {
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
    public static boolean insertItemsAtomic(List<ItemStack> items, TileEntity te, EnumFacing side) {
        if (te instanceof IInventory) {
            IInventory inventory = (IInventory) te;
            Map<Integer, ItemStack> undo = new HashMap<>();
            for (ItemStack item : items) {
                int remaining = InventoryHelper.mergeItemStackSafe(inventory, false, EnumFacing.DOWN, item, 0, inventory.getSizeInventory(), undo);
                if (remaining > 0) {
                    undo(undo, inventory);
                    return false;
                }
            }
        } else if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
            IItemHandler capability = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
            if (!insertItemsItemHandlerWithUndo(capability, items, true)) {
                return false;
            }
            insertItemsItemHandlerWithUndo(capability, items, false);
        }
        return true;
    }

    public static void undo(Map<Integer,ItemStack> undo, IInventory inventory) {
        for (Map.Entry<Integer, ItemStack> entry : undo.entrySet()) {
            inventory.setInventorySlotContents(entry.getKey(), entry.getValue());
        }
    }



    /**
     * Merges provided ItemStack with the first available one in this inventory. It will return the amount
     * of items that could not be merged. Also fills the undo buffer in case you want to undo the operation.
     * This version also checks for ISidedInventory if that's implemented by the inventory
     */
    public static int mergeItemStackSafe(IInventory inventory, boolean checkSlots, EnumFacing side, ItemStack result, int start, int stop, Map<Integer,ItemStack> undo) {
        if (inventory instanceof ISidedInventory) {
            return mergeItemStackInternal(inventory, (ISidedInventory) inventory, checkSlots, side, result, start, stop, undo);
        } else {
            return mergeItemStackInternal(inventory, null, checkSlots, side, result, start, stop, undo);
        }
    }

    /**
     * Merges provided ItemStack with the first available one in this inventory. It will return the amount
     * of items that could not be merged. Also fills the undo buffer in case you want to undo the operation.
     */
    public static int mergeItemStack(IInventory inventory, boolean checkSlots, ItemStack result, int start, int stop, Map<Integer,ItemStack> undo) {
        return mergeItemStackInternal(inventory, null, checkSlots, null, result, start, stop, undo);
    }

    private static int mergeItemStackInternal(IInventory inventory, ISidedInventory sidedInventory, boolean checkSlots, EnumFacing side, ItemStack result, int start, int stop, Map<Integer,ItemStack> undo) {
        int k = start;

        ItemStack itemstack1;
        int itemsToPlace = result.stackSize;

        if (result.isStackable()) {
            while (itemsToPlace > 0 && (k < stop)) {
                itemstack1 = inventory.getStackInSlot(k);

                if (isItemStackConsideredEqual(result, itemstack1)
                        && (sidedInventory == null || sidedInventory.canInsertItem(k, result, side))
                        && ((!checkSlots) || inventory.isItemValidForSlot(k, result))) {
                    int l = itemstack1.stackSize + itemsToPlace;

                    if (l <= result.getMaxStackSize()) {
                        if (undo != null) {
                            // Only put on undo map if the key is not already present.
                            if (!undo.containsKey(k)) {
                                undo.put(k, itemstack1.copy());
                            }
                        }
                        itemsToPlace = 0;
                        itemstack1.stackSize = l;
                        inventory.markDirty();
                    } else if (itemstack1.stackSize < result.getMaxStackSize()) {
                        if (undo != null) {
                            if (!undo.containsKey(k)) {
                                undo.put(k, itemstack1.copy());
                            }
                        }
                        itemsToPlace -= result.getMaxStackSize() - itemstack1.stackSize;
                        itemstack1.stackSize = result.getMaxStackSize();
                        inventory.markDirty();
                    }
                }

                ++k;
            }
        }

        if (itemsToPlace > 0) {
            k = start;

            while (k < stop) {
                itemstack1 = inventory.getStackInSlot(k);

                if (itemstack1 == null
                        && (sidedInventory == null || sidedInventory.canInsertItem(k, result, side))
                        && ((!checkSlots) || inventory.isItemValidForSlot(k, result))) {
                    if (undo != null) {
                        if (!undo.containsKey(k)) {
                            undo.put(k, null);
                        }
                    }
                    ItemStack copy = result.copy();
                    copy.stackSize = itemsToPlace;
                    inventory.setInventorySlotContents(k, copy);
                    inventory.markDirty();
                    itemsToPlace = 0;
                    break;
                }

                ++k;
            }
        }

        return itemsToPlace;
    }

    private static boolean isItemStackConsideredEqual(ItemStack result, ItemStack itemstack1) {
        return itemstack1 != null && itemstack1.getItem() == result.getItem() && (!result.getHasSubtypes() || result.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(result, itemstack1);
    }

    public int getCount() {
        return count;
    }

    public ItemStack getStackInSlot(int index) {
        if (index >= stacks.length) {
            return null;
        }

        return stacks[index];
    }

    /**
     * This function sets a stack in a slot but doesn't check if this slot allows it.
     * @param index
     * @param stack
     */
    public void setStackInSlot(int index, ItemStack stack) {
        if (index >= stacks.length) {
            return;
        }
        stacks[index] = stack;
    }

    public boolean containsItem(int index) {
        if (index >= stacks.length) {
            return false;
        }
        return stacks[index] != null;
    }

    public ItemStack decrStackSize(int index, int amount) {
        if (index >= stacks.length) {
            return null;
        }

        if (containerFactory.isGhostSlot(index) || containerFactory.isGhostOutputSlot(index)) {
            ItemStack old = stacks[index];
            stacks[index] = null;
            if (old == null) {
                return null;
            }
            old.stackSize = 0;
            return old;
        } else {
            if (stacks[index] != null) {
                if (stacks[index].stackSize <= amount) {
                    ItemStack old = stacks[index];
                    stacks[index] = null;
                    tileEntity.markDirty();
                    return old;
                }
                ItemStack its = stacks[index].splitStack(amount);
                if (stacks[index].stackSize == 0) {
                    stacks[index] = null;
                }
                tileEntity.markDirty();
                return its;
            }
            return null;
        }
    }

    public void setInventorySlotContents(int stackLimit, int index, ItemStack stack) {
        if (index >= stacks.length) {
            return;
        }

        if (containerFactory.isGhostSlot(index)) {
            if (stack != null) {
                stacks[index] = stack.copy();
                if (index < 9) {
                    stacks[index].stackSize = 1;
                }
            } else {
                stacks[index] = null;
            }
        } else if (containerFactory.isGhostOutputSlot(index)) {
            if (stack != null) {
                stacks[index] = stack.copy();
            } else {
                stacks[index] = null;
            }
        } else {
            stacks[index] = stack;
            if (stack != null && stack.stackSize > stackLimit) {
                stack.stackSize = stackLimit;
            }
            tileEntity.markDirty();
        }
    }

    public static void compactStacks(InventoryHelper helper, int start, int max) {
        compactStacks(helper.stacks, start, max);
    }

    public static void compactStacks(ItemStack[] stacks, int start, int max) {
        InventoryBasic inv = new InventoryBasic("temp", true, max);
        for (int i = 0 ; i < max ; i++) {
            ItemStack stack = stacks[i+start];
            if (stack != null) {
                mergeItemStack(inv, false, stack, 0, max, null);
            }
        }
        for (int i = 0 ; i < max ; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null && stack.stackSize == 0) {
                stack = null;
            }
            stacks[i+start] = stack;
        }
    }
}
