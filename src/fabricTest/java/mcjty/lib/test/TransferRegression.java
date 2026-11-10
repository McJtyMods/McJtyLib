package mcjty.lib.test;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import mcjty.lib.McJtyLib;
import mcjty.lib.api.power.ItemEnergy;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.container.ItemStackHandler;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import mcjty.lib.setup.Registration;
import mcjty.lib.varia.CapabilityTools;
import mcjty.lib.varia.CustomTank;
import mcjty.lib.varia.FluidStack;
import mcjty.lib.varia.IEnergyItem;
import mcjty.lib.varia.IFluidHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import team.reborn.energy.api.EnergyStorage;

/** Runs inside Fabric so variants, lookups, attachments and mixins are available. */
public final class TransferRegression implements ModInitializer {

    private static Item fixtureItem;

    @Override
    public void onInitialize() {

        var id = ResourceLocation.parse("mcjtylib_transfer_test:stack");

        fixtureItem =
                Registry.register(
                        BuiltInRegistries.ITEM, id, new TestEnergyItem(new Item.Properties()));
    }

    public static void runChecks() {
        try {
            checkTransfers();
        } catch (Throwable error) {
            error.printStackTrace();
            System.exit(1);
        }
    }

    private static void checkTransfers() {
        require(McJtyLib.instance != null, "Library common initializer completed");
        // Item properties are initialized when the fixture is constructed on this target.

        try {

            Class.forName("net.minecraft.server.level.ServerLevel");

            Class.forName("net.minecraft.world.entity.Mob");

            Class.forName("net.minecraft.world.level.block.RedStoneWireBlock");

        } catch (ClassNotFoundException error) {
            throw new AssertionError(error);
        }

        fluidUnitsAndTransactions();

        inventoryPersistence();
        itemTransfers();
        nativeInventorySlots();
        itemEnergyTransactions();

        System.out.println("McJtyLib Fabric transfer regression checks passed");

        System.exit(0);
    }

    private static void inventoryPersistence() {
        var lookup = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        var original = new ItemStackHandler(3);
        var stack = new ItemStack(fixtureItem, 17);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal("Saved transfer fixture"));
        original.setStackInSlot(2, stack);
        var saved = original.serializeNBT(lookup);
        var restored = new ItemStackHandler();
        restored.deserializeNBT(lookup, saved);
        require(restored.getSlots() == 3, "Persisted inventory size");
        require(restored.getStackInSlot(0).isEmpty() && restored.getStackInSlot(1).isEmpty(), "Persisted empty slots");
        require(ItemStack.matches(stack, restored.getStackInSlot(2)), "Persisted item id, count and components");
    }

    private static void fluidUnitsAndTransactions() {

        int[] notifications = {0};

        var tank =
                new CustomTank(1000) {

                    @Override
                    protected void onContentsChanged() {
                        notifications[0]++;
                    }
                };

        var water = FluidVariant.of(Fluids.WATER);

        try (var outer = Transaction.openOuter()) {

            require(tank.insert(water, 40500, outer) == 40500, "500 mB in Fabric droplets");

            require(tank.getFluidAmount() == 500, "Container millibuckets");

            try (var nested = outer.openNested()) {

                require(tank.extract(water, 8100, nested) == 8100, "Extract 100 mB");

                nested.commit();
            }

            require(tank.getFluidAmount() == 400, "Nested extraction");
        }

        require(
                tank.isEmpty() && notifications[0] == 0,
                "Aborted fluid transfer has no side effects");

        try (var tx = Transaction.openOuter()) {

            require(tank.insert(water, 162000, tx) == 81000, "One bucket capacity");

            tx.commit();
        }

        require(
                tank.getFluidAmount() == 1000 && notifications[0] == 1,
                "Committed tank notification");

        var encoded = FluidStack.CODEC.encodeStart(JsonOps.INSTANCE, tank.getFluid()).getOrThrow();
        require(
                encoded.getAsJsonObject().get("id").getAsString().equals("minecraft:water"),
                "Fluid persistence id");
        require(
                FluidStack.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow().getAmount() == 1000,
                "Fluid persistence round trip");
        require(
                FluidStack.OPTIONAL_CODEC
                        .parse(JsonOps.INSTANCE, new JsonObject())
                        .getOrThrow()
                        .isEmpty(),
                "Empty persisted fluid");
        var facade = CapabilityTools.fluidHandler(tank);

        require(
                facade.drain(1000, IFluidHandler.FluidAction.SIMULATE).getAmount() == 1000,
                "Simulated facade drain");

        require(tank.getFluidAmount() == 1000, "Simulated facade preserves fluid");

        require(
                facade.drain(250, IFluidHandler.FluidAction.EXECUTE).getAmount() == 250,
                "Executed facade drain");

        require(tank.getFluidAmount() == 750, "Facade uses millibuckets");
    }

    private static void itemTransfers() {

        int[] notifications = {0};

        var items =
                new GenericItemHandler(null, new ContainerFactory(2)) {

                    @Override
                    protected void onUpdate(int slot, ItemStack stack) {
                        notifications[0]++;
                    }
                };

        var stone = ItemVariant.of(fixtureItem);

        try (var outer = Transaction.openOuter()) {

            require(items.insert(stone, 100, outer) == 100, "Insert across slots");

            try (var nested = outer.openNested()) {

                require(items.extract(stone, 32, nested) == 32, "Nested item extraction");
            }

            require(items.getAmountAsLong(0) == 64, "Nested item rollback");
        }

        require(items.getStackInSlot(0).isEmpty() && notifications[0] == 0, "Outer item rollback");

        try (var tx = Transaction.openOuter()) {
            items.insert(stone, 8, tx);
            tx.commit();
        }

        require(
                items.getAmountAsLong(0) == 8 && notifications[0] == 1,
                "Notify only changed slots");
    }

    private static void nativeInventorySlots() {
        var inventory = new SimpleContainer(2);
        var handler = CapabilityTools.itemHandler(InventoryStorage.of(inventory, null));
        require(handler.getSlots() == 2, "Native inventory keeps empty slots");
        require(handler.extractItem(0, 1, false).isEmpty(), "Empty native slot extraction");
        require(handler.extractItem(0, 0, true).isEmpty(), "Zero native extraction");
        require(
                handler.insertItem(1, new ItemStack(fixtureItem, 12), true).isEmpty(),
                "Simulated native insertion");
        require(inventory.isEmpty(), "Simulated insertion preserves the inventory");
        require(
                handler.insertItem(1, new ItemStack(fixtureItem, 12), false).isEmpty(),
                "Native slot insertion");
        require(
                inventory.getItem(0).isEmpty() && inventory.getItem(1).getCount() == 12,
                "Insertion uses the requested slot");
        require(handler.extractItem(1, 5, true).getCount() == 5, "Simulated native extraction");
        require(
                inventory.getItem(1).getCount() == 12,
                "Simulated extraction preserves the inventory");
        require(handler.extractItem(1, 5, false).getCount() == 5, "Native slot extraction");
        require(
                inventory.getItem(1).getCount() == 7,
                "Native extraction commits the requested amount");
    }

    private static void itemEnergyTransactions() {
        var stack = new ItemStack(fixtureItem);
        var storage = EnergyStorage.ITEM.find(stack, CapabilityTools.itemContext(stack));
        require(storage != null, "Native item energy lookup");
        try (var tx = Transaction.openOuter()) {
            require(storage.insert(500, tx) == 500, "Native item energy insertion");
        }
        require(
                ((TestEnergyItem) fixtureItem).getEnergyStoredL(stack) == 0,
                "Aborted item component change");
        try (var tx = Transaction.openOuter()) {
            storage.insert(500, tx);
            tx.commit();
        }
        require(
                ((TestEnergyItem) fixtureItem).getEnergyStoredL(stack) == 500,
                "Committed item component change");
    }

    private static class TestEnergyItem extends Item implements IEnergyItem {
        TestEnergyItem(Properties properties) {
            super(properties);
        }

        public long getEnergyStoredL(ItemStack stack) {
            return stack.getOrDefault(Registration.ITEM_ENERGY.get(), new ItemEnergy(0)).energy();
        }

        public long getMaxEnergyStoredL(ItemStack stack) {
            return 1000;
        }

        public long receiveEnergyL(ItemStack stack, long amount, boolean simulate) {
            long stored = getEnergyStoredL(stack);
            long moved = Math.min(amount, 1000 - stored);
            if (!simulate)
                stack.set(Registration.ITEM_ENERGY.get(), new ItemEnergy(stored + moved));
            return moved;
        }

        public long extractEnergyL(ItemStack stack, long amount, boolean simulate) {
            long stored = getEnergyStoredL(stack);
            long moved = Math.min(stored, amount);
            if (!simulate)
                stack.set(Registration.ITEM_ENERGY.get(), new ItemEnergy(stored - moved));
            return moved;
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
