package mcjty.lib.container;

import com.google.common.collect.Range;
import mcjty.lib.McJtyLib;
import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.IGenericContainer;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.Logging;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Generic container support.
 */
public class GenericContainer extends Container implements IGenericContainer {
    protected Map<String,IItemHandler> inventories = new HashMap<>();
    private ContainerFactory factory;
    private GenericCrafter crafter = null;
    protected final BlockPos pos;
    protected final GenericTileEntity te;

    public GenericContainer(@Nullable ContainerType<?> type, int id, ContainerFactory factory, BlockPos pos, @Nullable GenericTileEntity te) {
        super(type, id);
        this.factory = factory;
        this.pos = pos;
        this.te = te;
        factory.doSetup();
    }

    @Override
    public Container getAsContainer() {
        return this;
    }

    public GenericTileEntity getTe() {
        return te;
    }

    @Override
    public void addShortListener(IntReferenceHolder holder) {
        trackInt(holder);
    }

    @Override
    public void addIntegerListener(IntReferenceHolder holder) {
        trackInt(new IntReferenceHolder() {
            private int lastKnown;

            @Override
            public int get() {
                return holder.get() & 0xffff;
            }

            @Override
            public void set(int val) {
                int full = holder.get();
                holder.set((full & 0xffff0000) | (val & 0xffff));
            }

            @Override
            public boolean isDirty() {
                int i = this.get();
                boolean flag = i != this.lastKnown;
                this.lastKnown = i;
                return flag;
            }
        });
        trackInt(new IntReferenceHolder() {
            private int lastKnown;

            @Override
            public int get() {
                return (holder.get() >> 16) & 0xffff;
            }

            @Override
            public void set(int val) {
                int full = holder.get();
                holder.set((full & 0x0000ffff) | ((val & 0xffff) << 16));
            }

            @Override
            public boolean isDirty() {
                int i = this.get();
                boolean flag = i != this.lastKnown;
                this.lastKnown = i;
                return flag;
            }
        });
    }

    public void addInventory(String name, IItemHandler inventory) {
        inventories.put(name, inventory);
    }

    public BlockPos getPos() {
        return pos;
    }

    public IItemHandler getInventory(String name) {
        return inventories.get(name);
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        for (IItemHandler inventory : inventories.values()) {
            // @todo
//            if (!inventory.isUsableByPlayer(player)) {
//                return false;
//            }
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

    @Override
    public void setupInventories(IItemHandler itemHandler, PlayerInventory inventory) {
        addInventory(ContainerFactory.CONTAINER_CONTAINER, itemHandler);
        addInventory(ContainerFactory.CONTAINER_PLAYER, new InvWrapper(inventory));
        generateSlots();
    }

    public void generateSlots() {
        for (SlotFactory slotFactory : factory.getSlots()) {
            IItemHandler inventory = inventories.get(slotFactory.getInventoryName());
            int index = slotFactory.getIndex();
            int x = slotFactory.getX();
            int y = slotFactory.getY();
            SlotType slotType = slotFactory.getSlotType();
            Slot slot = createSlot(slotFactory, inventory, index, x, y, slotType);
            addSlot(slot);
        }
    }

    protected Slot createSlot(SlotFactory slotFactory, final IItemHandler inventory, final int index, final int x, final int y, SlotType slotType) {
        Slot slot;
        if (slotType == SlotType.SLOT_GHOST) {
            slot = new GhostSlot(inventory, index, x, y);
        } else if (slotType == SlotType.SLOT_GHOSTOUT) {
            slot = new GhostOutputSlot(inventory, index, x, y);
        } else if (slotType == SlotType.SLOT_SPECIFICITEM) {
            final SlotDefinition slotDefinition = slotFactory.getSlotDefinition();
            slot = new SlotItemHandler(inventory, index, x, y) {
                @Override
                public boolean isItemValid(ItemStack stack) {
                    return slotDefinition.itemStackMatches(stack);
                }
            };
        } else if (slotType == SlotType.SLOT_CRAFTRESULT) {
            slot = new CraftingSlot(inventory, te, index, x, y, crafter);
        } else {
            slot = new BaseSlot(inventory, te, index, x, y);
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
    public ItemStack transferStackInSlot(PlayerEntity player, int index) {
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

                if (!itemstack1.isEmpty() && itemstack1.getItem() == par1ItemStack.getItem() && (par1ItemStack.getDamage() == itemstack1.getDamage())
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
    public ItemStack slotClick(int index, int button, ClickType mode, PlayerEntity player) {
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

    public static ContainerType<Container> createContainerType(String registryName) {
        ContainerType<Container> containerType = IForgeContainerType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            TileEntity te = McJtyLib.proxy.getClientWorld().getTileEntity(pos);
            if (te == null) {
                throw new IllegalStateException("Something went wrong getting the GUI");
            }
            return te.getCapability(CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY).map(h -> h.createMenu(windowId, inv, McJtyLib.proxy.getClientPlayer())).orElseThrow(RuntimeException::new);
        });
        containerType.setRegistryName(registryName);
        return containerType;
    }
}
