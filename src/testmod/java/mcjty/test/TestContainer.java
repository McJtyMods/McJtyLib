package mcjty.test;

import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.SlotDefinition;
import mcjty.lib.container.SlotType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;

public class TestContainer extends GenericContainer {
    public static final int SLOT_TAB = 0;

    public static final ContainerFactory factory = new ContainerFactory() {
        @Override
        protected void setup() {
            addSlotBox(new SlotDefinition(SlotType.SLOT_CONTAINER),
                    CONTAINER_FACTORY, SLOT_TAB, 5, 157, 1, 18, 1, 18);
            layoutPlayerInventorySlots(91, 157);
        }
    };


    public TestContainer(PlayerEntity player, IInventory containerInventory) {
        super(factory);
        addInventory(CONTAINER_INVENTORY, containerInventory);
        addInventory(ContainerFactory.CONTAINER_PLAYER, player.inventory);
        generateSlots();
    }
}
