package mcjty.lib.container;

import com.google.common.collect.Range;
import mcjty.lib.network.PacketSendGuiData;
import mcjty.lib.varia.Logging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic container support.
 */
public class GenericContainer extends Container {
    protected Map<String,IInventory> inventories = new HashMap<>();
    private ContainerFactory factory;
    private GenericCrafter crafter = null;

    public GenericContainer(ContainerFactory factory) {
        this.factory = factory;
        factory.doSetup();
    }

    public void addInventory(String name, IInventory inventory) {
        inventories.put(name, inventory);
    }

    public IInventory getInventory(String name) {
        return inventories.get(name);
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        for (IInventory inventory : inventories.values()) {
            if (!inventory.isUsableByPlayer(entityPlayer)) {
                return false;
            }
        }
        return true;
    }

    public SlotType getSlotType(int index) {
        return factory.getSlotType(index);
    }

    public GenericCrafter getCrafter() {
        return crafter;
    }

    public void setCrafter(GenericCrafter crafter) {
        this.crafter = crafter;
    }

    public void generateSlots() {
        for (SlotFactory slotFactory : factory.getSlots()) {
            IInventory inventory = inventories.get(slotFactory.getInventoryName());
            int index = slotFactory.getIndex();
            int x = slotFactory.getX();
            int y = slotFactory.getY();
            SlotType slotType = slotFactory.getSlotType();
            Slot slot = createSlot(slotFactory, inventory, index, x, y, slotType);
            addSlotToContainer(slot);
        }
    }

    protected Slot createSlot(SlotFactory slotFactory, final IInventory inventory, final int index, final int x, final int y, SlotType slotType) {
        Slot slot;
        if (slotType == SlotType.SLOT_GHOST) {
            slot = new GhostSlot(inventory, index, x, y);
        } else if (slotType == SlotType.SLOT_GHOSTOUT) {
            slot = new GhostOutputSlot(inventory, index, x, y);
        } else if (slotType == SlotType.SLOT_SPECIFICITEM) {
            final SlotDefinition slotDefinition = slotFactory.getSlotDefinition();
            slot = new Slot(inventory, index, x, y) {
                @Override
                public boolean isItemValid(ItemStack stack) {
                    return slotDefinition.itemStackMatches(stack);
                }
            };
        } else if (slotType == SlotType.SLOT_CRAFTRESULT) {
            slot = new CraftingSlot(inventory, index, x, y, crafter);
        } else {
            slot = new BaseSlot(inventory, index, x, y);
        }
        return slot;
    }

    private boolean mergeItemStacks(ItemStack itemStack, int sourceSlot, SlotType slotType, boolean reverse) {
        if (slotType == SlotType.SLOT_SPECIFICITEM) {
            for (SlotDefinition definition : factory.getSlotRangesMap().keySet()) {
                if (slotType.equals(definition.getType())) {
                    if (mergeItemStacks(itemStack, sourceSlot, definition, reverse)) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return mergeItemStacks(itemStack, sourceSlot, new SlotDefinition(slotType), reverse);
        }
    }

    protected boolean mergeItemStacks(ItemStack itemStack, int sourceSlot, SlotDefinition slotDefinition, boolean reverse) {
        SlotRanges ranges = factory.getSlotRangesMap().get(slotDefinition);
        if (ranges == null) {
            return false;
        }

        SlotType slotType = slotDefinition.getType();

        if (itemStack.getItem() != null && slotType == SlotType.SLOT_SPECIFICITEM && !slotDefinition.itemStackMatches(itemStack)) {
            return false;
        }
        for (Range<Integer> r : ranges.asRanges()) {
            Integer start = r.lowerEndpoint();
            int end = r.upperEndpoint();
            if (mergeItemStack(itemStack, start, end, reverse)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack origStack = slot.getStack();
            itemstack = origStack.copy();

            if (factory.isSpecificItemSlot(index)) {
                if (!mergeItemStacks(origStack, index, SlotType.SLOT_PLAYERINV, true)) {
                    if (!mergeItemStacks(origStack, index, SlotType.SLOT_PLAYERHOTBAR, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                slot.onSlotChange(origStack, itemstack);
            } else if (factory.isOutputSlot(index) || factory.isInputSlot(index) || factory.isContainerSlot(index)) {
                if (!mergeItemStacks(origStack, index, SlotType.SLOT_SPECIFICITEM, false)) {
                    if (!mergeItemStacks(origStack, index, SlotType.SLOT_PLAYERINV, true)) {
                        if (!mergeItemStacks(origStack, index, SlotType.SLOT_PLAYERHOTBAR, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
                slot.onSlotChange(origStack, itemstack);
            } else if (factory.isGhostSlot(index) || factory.isGhostOutputSlot(index)) {
                return ItemStack.EMPTY;
            } else if (factory.isPlayerInventorySlot(index)) {
                if (!mergeItemStacks(origStack, index, SlotType.SLOT_SPECIFICITEM, false)) {
                    if (!mergeItemStacks(origStack, index, SlotType.SLOT_INPUT, false)) {
                        if (!mergeItemStacks(origStack, index, SlotType.SLOT_PLAYERHOTBAR, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            } else if (factory.isPlayerHotbarSlot(index)) {
                if (!mergeItemStacks(origStack, index, SlotType.SLOT_SPECIFICITEM, false)) {
                    if (!mergeItemStacks(origStack, index, SlotType.SLOT_INPUT, false)) {
                        if (!mergeItemStacks(origStack, index, SlotType.SLOT_PLAYERINV, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            } else {
                Logging.log("Weird slot at index: " + index);
            }

            if (origStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (origStack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, origStack);
        }

        return itemstack;
    }


    @Override
    protected boolean mergeItemStack(ItemStack par1ItemStack, int fromIndex, int toIndex, boolean reverseOrder) {
        boolean result = false;
        int checkIndex = fromIndex;

        if (reverseOrder) {
            checkIndex = toIndex - 1;
        }

        Slot slot;
        ItemStack itemstack1 = ItemStack.EMPTY;

        if (par1ItemStack.isStackable()) {

            while (!par1ItemStack.isEmpty() && (!reverseOrder && checkIndex < toIndex || reverseOrder && checkIndex >= fromIndex)) {
                slot = this.inventorySlots.get(checkIndex);
                itemstack1 = slot.getStack();

                if (!itemstack1.isEmpty() && itemstack1.getItem() == par1ItemStack.getItem() && (!par1ItemStack.getHasSubtypes() || par1ItemStack.getItemDamage() == itemstack1.getItemDamage())
                        && ItemStack.areItemStackTagsEqual(par1ItemStack, itemstack1) && slot.isItemValid(par1ItemStack)) {

                    int mergedSize = itemstack1.getCount() + par1ItemStack.getCount();
                    int maxStackSize = Math.min(par1ItemStack.getMaxStackSize(), slot.getSlotStackLimit());
                    if (mergedSize <= maxStackSize) {
                        par1ItemStack.setCount(0);
                        if (mergedSize <= 0) {
                            itemstack1.setCount(0);
                        } else {
                            itemstack1.setCount(mergedSize);
                        }
                        slot.onSlotChanged();
                        result = true;
                    } else if (itemstack1.getCount() < maxStackSize) {
                        int amount = -(maxStackSize - itemstack1.getCount());
                        par1ItemStack.grow(amount);
                        if (maxStackSize <= 0) {
                            itemstack1.setCount(0);
                        } else {
                            itemstack1.setCount(maxStackSize);
                        }
                        slot.onSlotChanged();
                        result = true;
                    }
                }

                if (reverseOrder) {
                    --checkIndex;
                } else {
                    ++checkIndex;
                }
            }
        }

        if (!par1ItemStack.isEmpty()) {
            if (reverseOrder) {
                checkIndex = toIndex - 1;
            } else {
                checkIndex = fromIndex;
            }

            while (!reverseOrder && checkIndex < toIndex || reverseOrder && checkIndex >= fromIndex) {
                slot = this.inventorySlots.get(checkIndex);
                itemstack1 = slot.getStack();

                if (itemstack1.isEmpty() && slot.isItemValid(par1ItemStack)) {
                    ItemStack in = par1ItemStack.copy();
                    int amount1 = Math.min(in.getCount(), slot.getSlotStackLimit());
                    if (amount1 <= 0) {
                        in.setCount(0);
                    } else {
                        in.setCount(amount1);
                    }

                    slot.putStack(in);
                    slot.onSlotChanged();
                    if (in.getCount() >= par1ItemStack.getCount()) {
                        par1ItemStack.setCount(0);
                    } else {
                        int amount = -in.getCount();
                        par1ItemStack.grow(amount);
                    }
                    result = true;
                    break;
                }

                if (reverseOrder) {
                    --checkIndex;
                } else {
                    ++checkIndex;
                }
            }
        }

        return result;
    }


    @Override
    public ItemStack slotClick(int index, int button, ClickType mode, EntityPlayer player) {
        if (factory.isGhostSlot(index)) {
            Slot slot = getSlot(index);
            if (slot.getHasStack()) {
                slot.putStack(ItemStack.EMPTY);
            }

            ItemStack clickedWith = player.inventory.getItemStack();
            if (!clickedWith.isEmpty()) {
                ItemStack copy = clickedWith.copy();
                copy.setCount(1);
                slot.putStack(copy);
            }
            detectAndSendChanges();
            return ItemStack.EMPTY;
        } else {
            return super.slotClick(index, button, mode, player);
        }
    }

    // Call this in your detectAndSendChanges() implementation when you find one
    // of the fields you need in the GUI has changed
    protected void notifyPlayerOfChanges(SimpleNetworkWrapper wrapper, World world, BlockPos pos) {
        for (IContainerListener listener : this.listeners) {
            if (listener instanceof EntityPlayerMP) {
                EntityPlayerMP player = (EntityPlayerMP) listener;
                wrapper.sendTo(new PacketSendGuiData(world, pos), player);
            }
        }
    }
}
