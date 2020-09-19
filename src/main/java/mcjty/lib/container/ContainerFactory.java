package mcjty.lib.container;

import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * This class can be used to help build containers. It's used in combination with GenericContainer which
 * will take instances of this class to help setup the slots in the container
 */
public class ContainerFactory {

    public static final Lazy<ContainerFactory> EMPTY = Lazy.of(() -> new ContainerFactory(0));

    private Map<Integer,SlotDefinition> indexToType = new HashMap<>();
//    private Map<SlotDefinition,SlotRanges> slotRangesMap = new HashMap<>();
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

    // @todo
//    public Map<SlotDefinition, SlotRanges> getSlotRangesMap() {
//        return slotRangesMap;
//    }

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

    /**
     * Return the type of this slot for the given index.
     */
    @Nonnull
    public SlotDefinition getSlotDefinition(int index) {
        SlotDefinition slotDefinition = indexToType.get(index);
        if (slotDefinition == null) {
            throw new IllegalStateException("Bad slot specified!");
        }
        return slotDefinition;
    }

    public boolean isGenericSlot(int index) {
        return getSlotType(index) == SlotType.SLOT_GENERIC;
    }
    public boolean isOutputSlot(int index) {
        return getSlotDefinition(index).isOutput();
    }

    public boolean isInputSlot(int index) {
        return getSlotDefinition(index).isInput();
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
        return getSlotDefinition(index).isSpecific();
    }

    public boolean isPlayerHotbarSlot(int index) {
        return getSlotType(index) == SlotType.SLOT_PLAYERHOTBAR;
    }

    public SlotRanges getRanges(Predicate<SlotDefinition> matcher) {
        SlotRanges ranges = new SlotRanges();
        int idx = 0;
        for (SlotFactory slot : slots) {
            if (matcher.test(slot.getSlotDefinition())) {
                ranges.addSingle(idx);
            }
            idx++;
        }
        return ranges;
    }

    public ContainerFactory slot(SlotDefinition slotDefinition, String inventoryName, int index, int x, int y) {
        SlotFactory slotFactory = new SlotFactory(slotDefinition, inventoryName, index, x, y);
        int slotIndex = slots.size();
        slots.add(slotFactory);

//        SlotRanges slotRanges = slotRangesMap.get(slotDefinition);
//        if (slotRanges == null) {
//            slotRanges = new SlotRanges();
//            slotRangesMap.put(slotDefinition, slotRanges);
//        }
//        slotRanges.addSingle(slotIndex);
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
