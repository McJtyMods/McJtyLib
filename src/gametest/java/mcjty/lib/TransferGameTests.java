package mcjty.lib;

import mcjty.lib.container.*;
import mcjty.lib.varia.CustomTank;
import mcjty.lib.varia.InventoryTools;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import net.minecraft.gametest.framework.GameTestHelper;

/** Transactions must preserve contents and notifications across aborts and nested commits. */
public class TransferGameTests {
    public static void itemTransfersRespectCapacityAndRollback(GameTestHelper test) {
        var inventory = GenericItemHandler.create(null, () -> new ContainerFactory(2)).slotLimit(16).build();
        var iron = ItemResource.of(Items.IRON_INGOT);
        try (var outer = Transaction.openRoot()) {
            assertEquals(16, inventory.insert(0, iron, 32, outer));
            try (var inner = Transaction.open(outer)) {
                assertEquals(8, inventory.extract(0, iron, 8, inner));
                inner.commit();
            }
            assertEquals(8, inventory.getAmountAsLong(0));
            assertEquals(0, inventory.insert(0, ItemResource.of(Items.GOLD_INGOT), 1, outer));
        }
        assertEquals(0, inventory.getAmountAsLong(0));
        assertEquals(4, inventory.insertItem(0, new ItemStack(Items.IRON_INGOT, 20), false).getCount());
        assertEquals(16, inventory.getAmountAsLong(0));
        assertEquals(5, inventory.extractItem(0, 5, true).getCount());
        assertEquals(16, inventory.getAmountAsLong(0));
        test.succeed();
    }

    public static void ghostSlotsAreEditableButCannotTransferRealItems(GameTestHelper test) {
        var inventory = new GenericItemHandler(null, new ContainerFactory(1)
                .slot(SlotDefinition.ghost(), 0, 0, 0));
        var slot = new GhostSlot(inventory, 0, 0, 0);
        slot.set(new ItemStack(Items.IRON_INGOT, 12));
        assertEquals(1, inventory.getAmountAsLong(0));
        try (var tx = Transaction.openRoot()) {
            assertEquals(0, inventory.extract(0, ItemResource.of(Items.IRON_INGOT), 1, tx));
            assertEquals(0, inventory.insert(0, ItemResource.of(Items.IRON_INGOT), 1, tx));
        }
        assertTrue(slot.remove(1).isEmpty());
        assertEquals(1, inventory.getAmountAsLong(0));
        test.succeed();
    }

    public static void itemNotificationsOnlyFireAfterRootCommit(GameTestHelper test) {
        int[] changes = {0};
        var inventory = GenericItemHandler.create(null, () -> new ContainerFactory(1))
                .onUpdate((slot, stack) -> changes[0]++).build();
        try (var tx = Transaction.openRoot()) {
            inventory.insert(0, ItemResource.of(Items.IRON_INGOT), 1, tx);
        }
        assertEquals(0, changes[0]);
        try (var tx = Transaction.openRoot()) {
            inventory.insert(0, ItemResource.of(Items.IRON_INGOT), 1, tx);
            assertEquals(0, changes[0]);
            tx.commit();
        }
        assertEquals(1, changes[0]);
        test.succeed();
    }

    public static void fluidSimulationAndNestedAbortPreserveContents(GameTestHelper test) {
        int[] changes = {0};
        var tank = new CustomTank(1000) {
            @Override protected void onContentsChanged() { changes[0]++; }
        };
        var water = FluidResource.of(Fluids.WATER);
        try (var outer = Transaction.openRoot()) {
            assertEquals(1000, tank.insert(0, water, 1500, outer));
            try (var inner = Transaction.open(outer)) {
                assertEquals(250, tank.extract(0, water, 250, inner));
                inner.commit();
            }
            assertEquals(750, tank.getFluidAmount());
        }
        assertTrue(tank.isEmpty());
        assertEquals(0, changes[0]);
        assertEquals(1000, tank.fill(water.toStack(1500), false));
        assertEquals(1, changes[0]);
        assertEquals(500, tank.drain(500, true).getAmount());
        assertEquals(1000, tank.getFluidAmount());
        assertEquals(1, changes[0]);
        assertEquals(0, tank.fill(FluidResource.of(Fluids.LAVA).toStack(100), false));
        assertEquals(1000, tank.drain(1500, false).getAmount());
        assertTrue(tank.isEmpty());
        assertEquals(2, changes[0]);
        test.succeed();
    }

    public static void undoRestoresSlotlessTransfers(GameTestHelper test) {
        var inventory = new GenericItemHandler(null, new ContainerFactory(1));
        inventory.setStackInSlot(0, new ItemStack(Items.IRON_INGOT, 3));
        var undo = new UndoableItemHandler(inventory);
        InventoryTools.insertItem(undo, new ItemStack(Items.IRON_INGOT, 2), false);
        assertEquals(5, inventory.getAmountAsLong(0));
        undo.restore();
        assertEquals(3, inventory.getAmountAsLong(0));
        test.succeed();
    }
    public static void automationRespectsInputAndOutputSlots(GameTestHelper test) {
        var factory = new ContainerFactory(2)
                .slot(SlotDefinition.generic().in(), 0, 0, 0)
                .slot(SlotDefinition.generic().out(), 1, 0, 0);
        var inventory = new GenericItemHandler(null, factory);
        inventory.setStackInSlot(1, new ItemStack(Items.IRON_INGOT, 3));
        var automation = new AutomationFilterItemHander(inventory);
        var iron = ItemResource.of(Items.IRON_INGOT);
        try (var tx = Transaction.openRoot()) {
            assertEquals(2, automation.insert(0, iron, 2, tx));
            assertEquals(0, automation.insert(1, iron, 2, tx));
            assertEquals(0, automation.extract(0, iron, 1, tx));
            assertEquals(1, automation.extract(1, iron, 1, tx));
            tx.commit();
        }
        assertEquals(2, inventory.getAmountAsLong(0));
        assertEquals(2, inventory.getAmountAsLong(1));
        new BaseSlot(automation, null, 1, 0, 0).set(new ItemStack(Items.IRON_INGOT, 4));
        assertEquals(4, inventory.getAmountAsLong(1));
        test.succeed();
    }

    public static void inventorySaveRetainsIndexed26_2Layout(GameTestHelper test) {
        var factory = new ContainerFactory(3).slot(SlotDefinition.ghost(), 2, 0, 0);
        var inventory = new GenericItemHandler(null, factory);
        inventory.setStackInSlot(0, new ItemStack(Items.IRON_INGOT, 5));
        inventory.setStackInSlot(2, new ItemStack(Items.GOLD_INGOT, 1));
        var data = inventory.serializeNBT(test.getLevel().registryAccess());
        assertEquals(3, data.getIntOr("Size", 0));
        assertTrue(data.contains("Items"));
        var restored = new GenericItemHandler(null, factory);
        restored.deserializeNBT(test.getLevel().registryAccess(), data);
        assertTrue(restored.getResource(0).is(Items.IRON_INGOT));
        assertEquals(5, restored.getAmountAsLong(0));
        assertTrue(restored.getResource(1).isEmpty());
        assertTrue(restored.getResource(2).is(Items.GOLD_INGOT));
        assertEquals(1, restored.getAmountAsLong(2));
        test.succeed();
    }

    public static void fluidContainersCanReplaceBucketItems(GameTestHelper test) {
        var original = new ItemStack(Items.BUCKET, 3);
        var water = FluidResource.of(Fluids.WATER).toStack(1000);
        var filled = mcjty.lib.varia.FluidTools.fillContainer(water, original);
        assertTrue(filled.is(Items.WATER_BUCKET));
        assertEquals(3, original.getCount());
        assertTrue(mcjty.lib.varia.FluidTools.isEmptyContainer(original));
        assertTrue(mcjty.lib.varia.FluidTools.isFilledContainer(filled));
        assertEquals(1000, mcjty.lib.varia.FluidTools.convertBucketToFluid(filled).getAmount());
        assertTrue(mcjty.lib.varia.FluidTools.drainContainer(filled).is(Items.BUCKET));
        assertTrue(filled.is(Items.WATER_BUCKET));
        assertTrue(mcjty.lib.varia.FluidTools.fillContainer(water.copyWithAmount(500), original).isEmpty());
        test.succeed();
    }

    private static void assertEquals(long expected, long actual) {
        if (expected != actual) throw new AssertionError("Expected " + expected + ", got " + actual);
    }
    private static void assertTrue(boolean value) {
        if (!value) throw new AssertionError("Expected true");
    }
}
