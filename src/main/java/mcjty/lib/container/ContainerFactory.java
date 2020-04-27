package mcjty.lib.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class can be used to help build containers. It's used in combination with GenericContainer which
 * will take instances of this class to help setup the slots in the container
 */
public class ContainerFactory {
    private Map<Integer,SlotDefinition> indexToType = new HashMap<>();
    private Map<SlotDefinition,SlotRanges> slotRangesMap = new HashMap<>();
    private List<SlotFactory> slots = new ArrayList<>();

    public static final String CONTAINER_CONTAINER = "container";
    public static final String CONTAINER_PLAYER = "player";

    private final int containerSlots;

    public ContainerFactory(int containerSlots) {
        this.containerSlots = containerSlots;
    }

    public int getContainerSlots() {
        return containerSlots;
    }

    public Map<SlotDefinition, SlotRanges> getSlotRangesMap() {
        return slotRangesMap;
    }

    public Iterable<SlotFactory> getSlots() {
        return slots;
    }

    /**
     * Return the type of this slot for the given index.
     */
    public SlotType getSlotType(int index) {
        SlotDefinition slotDefinition = indexToType.get(index);
        if (slotDefinition == null) {
            return SlotType.SLOT_UNKNOWN;
        }
        return slotDefinition.getType();
    }

    public boolean isContainerSlot(int index) {
        return getSlotType(index) == SlotType.SLOT_CONTAINER;
    }
    public boolean isOutputSlot(int index) {
        return getSlotType(index) == SlotType.SLOT_OUTPUT;
    }

    public boolean isInputSlot(int index) {
        return getSlotType(index) == SlotType.SLOT_INPUT;
    }

    public boolean isGhostSlot(int index) {
        return getSlotType(index) == SlotType.SLOT_GHOST;
    }

    public boolean isGhostOutputSlot(int index) {
        return getSlotType(index) == SlotType.SLOT_GHOSTOUT;
    }

    public boolean isCraftResultSlot(int index) {
        return getSlotType(index) == SlotType.SLOT_CRAFTRESULT;
    }

    public boolean isPlayerInventorySlot(int index) {
        return getSlotType(index) == SlotType.SLOT_PLAYERINV;
    }

    public boolean isSpecificItemSlot(int index) {
        return getSlotType(index) == SlotType.SLOT_SPECIFICITEM;
    }

    public boolean isPlayerHotbarSlot(int index) {
        return getSlotType(index) == SlotType.SLOT_PLAYERHOTBAR;
    }

    public ContainerFactory slot(SlotDefinition slotDefinition, String inventoryName, int index, int x, int y) {
        SlotFactory slotFactory = new SlotFactory(slotDefinition, inventoryName, index, x, y);
        int slotIndex = slots.size();
        slots.add(slotFactory);

        SlotRanges slotRanges = slotRangesMap.get(slotDefinition);
        if (slotRanges == null) {
            slotRanges = new SlotRanges(slotDefinition);
            slotRangesMap.put(slotDefinition, slotRanges);
        }
        slotRanges.addSingle(slotIndex);
        indexToType.put(slotIndex, slotDefinition);
        return this;
    }

    public ContainerFactory range(SlotDefinition slotDefinition, String inventoryName, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            slot(slotDefinition, inventoryName, index, x, y);
            x += dx;
            index++;
        }
        return this;
    }

    public ContainerFactory box(SlotDefinition slotDefinition, String inventoryName, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            range(slotDefinition, inventoryName, index, x, y, horAmount, dx);
            index += horAmount;
            y += dy;
        }
        return this;
    }

    public ContainerFactory box(SlotDefinition slotDefinition, String inventoryName, int index, int x, int y, int horAmount, int verAmount) {
        return box(slotDefinition, inventoryName, index, x, y, horAmount, 18, verAmount, 18);
    }

    public ContainerFactory playerSlots(int leftCol, int topRow) {
        // Player inventory
        box(new SlotDefinition(SlotType.SLOT_PLAYERINV), CONTAINER_PLAYER, 9, leftCol, topRow, 9, 3);

        // Hotbar
        topRow += 58;
        range(new SlotDefinition(SlotType.SLOT_PLAYERHOTBAR), CONTAINER_PLAYER, 0, leftCol, topRow, 9, 18);
        return this;
    }

}
