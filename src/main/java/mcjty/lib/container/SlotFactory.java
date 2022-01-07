package mcjty.lib.container;

public record SlotFactory(SlotDefinition slotDefinition, String inventoryName, int index,
                          int x, int y) {

    public SlotType getSlotType() {
        return slotDefinition.getType();
    }
}
