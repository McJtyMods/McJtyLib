package mcjty.lib.container;

import mcjty.lib.gui.GuiParser;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContainerFactory {
    private Map<Integer,SlotDefinition> indexToType = new HashMap<>();
    private Map<SlotDefinition,SlotRanges> slotRangesMap = new HashMap<>();
    private List<SlotFactory> slots = new ArrayList<>();

    public static final String CONTAINER_CONTAINER = "container";
    public static final String CONTAINER_PLAYER = "player";

    private boolean setupDone = false;
    private boolean slotsSetup = false;
    private int[] accessibleSlots;
    private int[] accessibleInputSlots;
    private int[] accessibleOutputSlots;
    private final int containerSlots;

    public ContainerFactory(int containerSlots) {
        this.containerSlots = containerSlots;
    }

    public int getContainerSlots() {
        return containerSlots;
    }

    protected void setup() {
    }

    private void handleSlotCommand(GuiParser.GuiCommand slotCmd) {
        String typen = slotCmd.getOptionalPar(0, SlotType.SLOT_CONTAINER.getName());
        SlotType type = SlotType.findByName(typen);
        if (type == null) {
            throw new RuntimeException("Unknown slot type: " + typen + "!");
        }
        List<ItemStack> stacks = new ArrayList<>();
        slotCmd.findCommand("items").ifPresent(cmd -> {
            cmd.parameters().forEach(par -> {
                String itemName = par.toString();
                ItemStack stack;
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(itemName));
                if (block != null && block != Blocks.AIR) {
                    stack = new ItemStack(block);
                } else {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
                    if (item != null) {
                        stack = new ItemStack(item);
                    } else {
                        stack = ItemStack.EMPTY;
                    }
                }
                stacks.add(stack);
            });
        });
        SlotDefinition slotDefinition = new SlotDefinition(type, stacks.toArray(new ItemStack[stacks.size()]));
        int[] pos = new int[2];
        slotCmd.findCommand("pos").ifPresent(cmd -> {
            pos[0] = cmd.getOptionalPar(0, 0);
            pos[1] = cmd.getOptionalPar(1, 0);
        });
        int[] dim = new int[] { 1, 1 };
        slotCmd.findCommand("dim").ifPresent(cmd -> {
            dim[0] = cmd.getOptionalPar(0, 0);
            dim[1] = cmd.getOptionalPar(1, 0);
        });
        box(slotDefinition, CONTAINER_CONTAINER, slots.size(), pos[0], pos[1], dim[0], dim[1]);
    }

    public void doSetup() {
        if (!setupDone) {
            setupDone = true;
            setup();
        }
    }

    protected void setupAccessibleSlots() {
        if (slotsSetup) {
            return;
        }
        doSetup();
        slotsSetup = true;
        List<Integer> s = new ArrayList<>();
        List<Integer> si = new ArrayList<>();
        List<Integer> so = new ArrayList<>();
        int index = 0;
        for (SlotFactory slotFactory : slots) {
            if (slotFactory.getSlotType() == SlotType.SLOT_INPUT) {
                s.add(index);
                si.add(index);
            }
            if (slotFactory.getSlotType() == SlotType.SLOT_OUTPUT) {
                s.add(index);
                so.add(index);
            }
            index++;
        }
        accessibleSlots = convertList(s);
        accessibleInputSlots = convertList(si);
        accessibleOutputSlots = convertList(so);
    }

    private static int[] convertList(List<Integer> list) {
        int[] s = new int[list.size()];
        for (int i = 0 ; i < list.size() ; i++) {
            s[i] = list.get(i);
        }
        return s;
    }

    public Map<SlotDefinition, SlotRanges> getSlotRangesMap() {
        return slotRangesMap;
    }

    public int[] getAccessibleSlots() {
        setupAccessibleSlots();
        return accessibleSlots;
    }

    public int[] getAccessibleInputSlots() {
        setupAccessibleSlots();
        return accessibleInputSlots;
    }

    public int[] getAccessibleOutputSlots() {
        setupAccessibleSlots();
        return accessibleOutputSlots;
    }

    public Iterable<SlotFactory> getSlots() {
        return slots;
    }

    /**
     * Return the type of this slot for the given index.
     * @param index
     * @return
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

    public void slot(SlotDefinition slotDefinition, String inventoryName, int index, int x, int y) {
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
    }

    public int range(SlotDefinition slotDefinition, String inventoryName, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            slot(slotDefinition, inventoryName, index, x, y);
            x += dx;
            index++;
        }
        return index;
    }

    public int box(SlotDefinition slotDefinition, String inventoryName, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = range(slotDefinition, inventoryName, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    public int box(SlotDefinition slotDefinition, String inventoryName, int index, int x, int y, int horAmount, int verAmount) {
        return box(slotDefinition, inventoryName, index, x, y, horAmount, 18, verAmount, 18);
    }

    protected void playerSlots(int leftCol, int topRow) {
        // Player inventory
        box(new SlotDefinition(SlotType.SLOT_PLAYERINV), CONTAINER_PLAYER, 9, leftCol, topRow, 9, 3);

        // Hotbar
        topRow += 58;
        range(new SlotDefinition(SlotType.SLOT_PLAYERHOTBAR), CONTAINER_PLAYER, 0, leftCol, topRow, 9, 18);

    }

}
