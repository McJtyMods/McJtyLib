package mcjty.lib.container;

import mcjty.lib.tools.ChatTools;
import mcjty.lib.tools.ItemStackList;
import mcjty.lib.tools.ItemStackTools;
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

import javax.annotation.Nullable;
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
    private ItemStackList stacks;
    private int count;

    public InventoryHelper(TileEntity tileEntity, ContainerFactory containerFactory, int count) {
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
        setStackInSlot(index, ItemStackTools.getEmptyStack());
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
            if (ItemStackTools.isValid(item)) {
                if (amount == -1) {
                    amount = ItemStackTools.getStackSize(item);
                }
                ItemStack stack = capability.extractItem(slot, amount, false);
                if (ItemStackTools.isValid(stack)) {
                    consumer.accept(stack);
                }
            }
        } else if (tileEntity instanceof IInventory) {
            IInventory inventory = (IInventory) tileEntity;
            ItemStack item = inventory.getStackInSlot(slot);
            if (ItemStackTools.isValid(item)) {
                if (amount == -1) {
                    amount = ItemStackTools.getStackSize(item);
                }
                ItemStack stack = inventory.decrStackSize(slot, amount);
                if (ItemStackTools.isValid(stack)) {
                    consumer.accept(stack);
                }
            }
        }
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
                if (ItemStackTools.isValid(itemStack) && predicate.test(itemStack)) {
                    builder.add(itemStack);
                }
            }
        } else if (tileEntity instanceof IInventory) {
            IInventory inventory = (IInventory) tileEntity;
            for (int i = 0 ; i < inventory.getSizeInventory() ; i++) {
                ItemStack itemStack = inventory.getStackInSlot(i);
                if (ItemStackTools.isValid(itemStack) && predicate.test(itemStack)) {
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
                if (ItemStackTools.isEmpty(inventory.getStackInSlot(i))) {
                    ItemStack copy = heldItem.copy();
                    ItemStackTools.setStackSize(copy, 1);
                    inventory.setInventorySlotContents(i, copy);
                    ItemStackTools.incStackSize(heldItem, -1);
                    if (ItemStackTools.isEmpty(heldItem)) {
                        player.setHeldItem(hand, ItemStackTools.getEmptyStack());
                    }
                    if (world.isRemote) {
                        ChatTools.addChatMessage(player, new TextComponentString("Installed module"));
                    }
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Insert an item into an inventory at the given direction. Supports IItemHandler as
     * well as IInventory. Returns an itemstack with whatever could not be inserted or empty item
     * on succcess.
     */
    @Nullable
    public static ItemStack insertItem(World world, BlockPos pos, EnumFacing direction, ItemStack s) {
        TileEntity te = world.getTileEntity(direction == null ? pos : pos.offset(direction));
        if (te != null) {
            EnumFacing opposite = direction == null ? null : direction.getOpposite();
            if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, opposite)) {
                IItemHandler capability = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, opposite);
                s = ItemHandlerHelper.insertItem(capability, s, false);
                if (ItemStackTools.isEmpty(s)) {
                    return ItemStackTools.getEmptyStack();
                }
            } else if (te instanceof IInventory) {
                int i = mergeItemStackSafe((IInventory) te, true, opposite, s, 0, ((IInventory) te).getSizeInventory(), null);
                if (i == 0) {
                    return ItemStackTools.getEmptyStack();
                }
                ItemStackTools.setStackSize(s, i);
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
            return ItemStackTools.isEmpty(stack);
        }

        List<ItemStack> s = stacks.stream().map(ItemStack::copy).collect(Collectors.toList());
        for (int i = 0; i < dest.getSlots(); i++) {
            boolean empty = true;
            for (int j = 0 ; j < stacks.size() ; j++) {
                ItemStack stack = dest.insertItem(i, s.get(j), simulate);
                if (ItemStackTools.isValid(stack)) {
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
        } else {
            return false;
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

        ItemStack itemstack1 = ItemStackTools.getEmptyStack();
        int itemsToPlace = ItemStackTools.getStackSize(result);

        if (result.isStackable()) {
            while (itemsToPlace > 0 && (k < stop)) {
                itemstack1 = inventory.getStackInSlot(k);

                if (isItemStackConsideredEqual(result, itemstack1)
                        && (sidedInventory == null || sidedInventory.canInsertItem(k, result, side))
                        && ((!checkSlots) || inventory.isItemValidForSlot(k, result))) {
                    int l = ItemStackTools.getStackSize(itemstack1) + itemsToPlace;

                    if (l <= result.getMaxStackSize()) {
                        if (undo != null) {
                            // Only put on undo map if the key is not already present.
                            if (!undo.containsKey(k)) {
                                undo.put(k, itemstack1.copy());
                            }
                        }
                        itemsToPlace = 0;
                        ItemStackTools.setStackSize(itemstack1, l);
                        inventory.markDirty();
                    } else if (ItemStackTools.getStackSize(itemstack1) < result.getMaxStackSize()) {
                        if (undo != null) {
                            if (!undo.containsKey(k)) {
                                undo.put(k, itemstack1.copy());
                            }
                        }
                        itemsToPlace -= result.getMaxStackSize() - ItemStackTools.getStackSize(itemstack1);
                        ItemStackTools.setStackSize(itemstack1, result.getMaxStackSize());
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

                if (ItemStackTools.isEmpty(itemstack1)
                        && (sidedInventory == null || sidedInventory.canInsertItem(k, result, side))
                        && ((!checkSlots) || inventory.isItemValidForSlot(k, result))) {
                    if (undo != null) {
                        if (!undo.containsKey(k)) {
                            undo.put(k, ItemStackTools.getEmptyStack());
                        }
                    }
                    ItemStack copy = result.copy();
                    ItemStackTools.setStackSize(copy, itemsToPlace);
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
        return ItemStackTools.isValid(itemstack1) && itemstack1.getItem() == result.getItem() && (!result.getHasSubtypes() || result.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(result, itemstack1);
    }

    public int getCount() {
        return count;
    }

    public ItemStack getStackInSlot(int index) {
        if (index >= stacks.size()) {
            return ItemStackTools.getEmptyStack();
        }

        return stacks.get(index);
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
        return ItemStackTools.isValid(stacks.get(index));
    }

    public ItemStack decrStackSize(int index, int amount) {
        if (index >= stacks.size()) {
            return ItemStackTools.getEmptyStack();
        }

        if (containerFactory.isGhostSlot(index) || containerFactory.isGhostOutputSlot(index)) {
            ItemStack old = stacks.get(index);
            stacks.set(index, ItemStackTools.getEmptyStack());
            if (ItemStackTools.isEmpty(old)) {
                return ItemStackTools.getEmptyStack();
            }
            ItemStackTools.makeEmpty(old);
            return old;
        } else {
            if (ItemStackTools.isValid(stacks.get(index))) {
                if (ItemStackTools.getStackSize(stacks.get(index)) <= amount) {
                    ItemStack old = stacks.get(index);
                    stacks.set(index, ItemStackTools.getEmptyStack());
                    tileEntity.markDirty();
                    return old;
                }
                ItemStack its = stacks.get(index).splitStack(amount);
                if (ItemStackTools.isEmpty(stacks.get(index))) {
                    stacks.set(index, ItemStackTools.getEmptyStack());
                }
                tileEntity.markDirty();
                return its;
            }
            return ItemStackTools.getEmptyStack();
        }
    }

    public void setInventorySlotContents(int stackLimit, int index, ItemStack stack) {
        if (index >= stacks.size()) {
            return;
        }

        if (containerFactory.isGhostSlot(index)) {
            if (ItemStackTools.isValid(stack)) {
                stacks.set(index, stack.copy());
                if (index < 9) {
                    ItemStackTools.setStackSize(stacks.get(index), 1);
                }
            } else {
                stacks.set(index, ItemStackTools.getEmptyStack());
            }
        } else if (containerFactory.isGhostOutputSlot(index)) {
            if (ItemStackTools.isValid(stack)) {
                stacks.set(index, stack.copy());
            } else {
                stacks.set(index, ItemStackTools.getEmptyStack());
            }
        } else {
            stacks.set(index, stack);
            if (ItemStackTools.isValid(stack) && ItemStackTools.getStackSize(stack) > stackLimit) {
                ItemStackTools.setStackSize(stack, stackLimit);
            }
            tileEntity.markDirty();
        }
    }

    public static void compactStacks(InventoryHelper helper, int start, int max) {
        compactStacks(helper.stacks, start, max);
    }

    public static void compactStacks(List<ItemStack> stacks, int start, int max) {
        InventoryBasic inv = new InventoryBasic("temp", true, max);
        for (int i = 0 ; i < max ; i++) {
            ItemStack stack = stacks.get(i+start);
            if (ItemStackTools.isValid(stack)) {
                mergeItemStack(inv, false, stack, 0, max, null);
            }
        }
        for (int i = 0 ; i < max ; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (ItemStackTools.isValid(stack) && ItemStackTools.getStackSize(stack) == 0) {
                stack = ItemStackTools.getEmptyStack();
            }
            stacks.set(i+start, stack);
        }
    }

    public static void compactStacks(ItemStack[] stacks, int start, int max) {
        InventoryBasic inv = new InventoryBasic("temp", true, max);
        for (int i = 0 ; i < max ; i++) {
            ItemStack stack = stacks[i+start];
            if (ItemStackTools.isValid(stack)) {
                mergeItemStack(inv, false, stack, 0, max, null);
            }
        }
        for (int i = 0 ; i < max ; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (ItemStackTools.isValid(stack) && ItemStackTools.getStackSize(stack) == 0) {
                stack = ItemStackTools.getEmptyStack();
            }
            stacks[i+start] = stack;
        }
    }
}
