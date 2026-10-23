package mcjty.lib.test;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

/** Runs inside Fabric so variants, lookups, attachments and mixins are available. */
public final class TransferRegression implements ModInitializer {

    private static net.minecraft.world.item.Item fixtureItem;

    @Override
    public void onInitialize() {

        var id=net.minecraft.resources.Identifier.parse("mcjtylib_transfer_test:stack");

        fixtureItem=net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.ITEM,id,
        new TestEnergyItem(new net.minecraft.world.item.Item.Properties().setId(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ITEM,id))));

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
        require(mcjty.lib.McJtyLib.instance != null, "Library common initializer completed");
        // Tests run before the normal registry component bootstrap. Bind only our fixture.
        fixtureItem.builtInRegistryHolder().bindComponents(net.minecraft.core.component.DataComponentMap.builder().set(net.minecraft.core.component.DataComponents.MAX_STACK_SIZE,64).build());

        try {

            Class.forName("net.minecraft.server.level.ServerLevel");

            Class.forName("net.minecraft.world.entity.Mob");

            Class.forName("net.minecraft.world.level.block.RedstoneWireBlock");

        }
        catch (ClassNotFoundException error) {
            throw new AssertionError(error);
        }

        fluidUnitsAndTransactions();

        itemTransfers();
        itemEnergyTransactions();

        System.out.println("McJtyLib Fabric transfer regression checks passed");

        System.exit(0);

    }

    private static void fluidUnitsAndTransactions() {



        int[] notifications = {
            0
        };

        var tank = new mcjty.lib.varia.CustomTank(1000) {

            @Override
        protected void onContentsChanged() {
                notifications[0]++;
            }

        };

        var water = net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant.of(net.minecraft.world.level.material.Fluids.WATER);

        try (var outer = Transaction.openOuter()) {

            require(tank.insert(water, 40500, outer) == 40500, "500 mB in Fabric droplets");

            require(tank.getFluidAmount() == 500, "Container millibuckets");

            try (var nested = outer.openNested()) {

                require(tank.extract(water, 8100, nested) == 8100, "Extract 100 mB");

                nested.commit();

            }

            require(tank.getFluidAmount() == 400, "Nested extraction");

        }

        require(tank.isEmpty() && notifications[0] == 0, "Aborted fluid transfer has no side effects");

        try (var tx = Transaction.openOuter()) {

            require(tank.insert(water, 162000, tx) == 81000, "One bucket capacity");

            tx.commit();

        }

        require(tank.getFluidAmount() == 1000 && notifications[0] == 1, "Committed tank notification");

        var encoded = mcjty.lib.varia.FluidStack.CODEC.encodeStart(com.mojang.serialization.JsonOps.INSTANCE, tank.getFluid()).getOrThrow();
        require(encoded.getAsJsonObject().get("id").getAsString().equals("minecraft:water"), "Fluid persistence id");
        require(mcjty.lib.varia.FluidStack.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, encoded).getOrThrow().getAmount() == 1000, "Fluid persistence round trip");
        require(mcjty.lib.varia.FluidStack.OPTIONAL_CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, new com.google.gson.JsonObject()).getOrThrow().isEmpty(), "Empty persisted fluid");
        var facade = mcjty.lib.varia.CapabilityTools.fluidHandler(tank);

        require(facade.drain(1000, mcjty.lib.varia.IFluidHandler.FluidAction.SIMULATE).getAmount() == 1000, "Simulated facade drain");

        require(tank.getFluidAmount() == 1000, "Simulated facade preserves fluid");

        require(facade.drain(250, mcjty.lib.varia.IFluidHandler.FluidAction.EXECUTE).getAmount() == 250, "Executed facade drain");

        require(tank.getFluidAmount() == 750, "Facade uses millibuckets");

    }

    private static void itemTransfers() {



        int[] notifications = {
            0
        };

        var items = new mcjty.lib.container.GenericItemHandler(null, new mcjty.lib.container.ContainerFactory(2)) {

            @Override
        protected void onUpdate(int slot, net.minecraft.world.item.ItemStack stack) {
                notifications[0]++;
            }

        };

        var stone = net.fabricmc.fabric.api.transfer.v1.item.ItemVariant.of(fixtureItem);

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

        require(items.getAmountAsLong(0) == 8 && notifications[0] == 1, "Notify only changed slots");

    }

    private static void itemEnergyTransactions() {
        var stack = new net.minecraft.world.item.ItemStack(fixtureItem);
        var storage = team.reborn.energy.api.EnergyStorage.ITEM.find(stack, mcjty.lib.varia.CapabilityTools.itemContext(stack));
        require(storage != null, "Native item energy lookup");
        try (var tx = Transaction.openOuter()) { require(storage.insert(500, tx) == 500, "Native item energy insertion"); }
        require(((TestEnergyItem)fixtureItem).getEnergyStoredL(stack) == 0, "Aborted item component change");
        try (var tx = Transaction.openOuter()) { storage.insert(500, tx); tx.commit(); }
        require(((TestEnergyItem)fixtureItem).getEnergyStoredL(stack) == 500, "Committed item component change");
    }

    private static class TestEnergyItem extends net.minecraft.world.item.Item implements mcjty.lib.varia.IEnergyItem {
        TestEnergyItem(Properties properties) { super(properties); }
        public long getEnergyStoredL(net.minecraft.world.item.ItemStack stack) {
            return stack.getOrDefault(mcjty.lib.setup.Registration.ITEM_ENERGY.get(), new mcjty.lib.api.power.ItemEnergy(0)).energy();
        }
        public long getMaxEnergyStoredL(net.minecraft.world.item.ItemStack stack) { return 1000; }
        public long receiveEnergyL(net.minecraft.world.item.ItemStack stack, long amount, boolean simulate) {
            long stored = getEnergyStoredL(stack);
            long moved = Math.min(amount, 1000 - stored);
            if (!simulate) stack.set(mcjty.lib.setup.Registration.ITEM_ENERGY.get(), new mcjty.lib.api.power.ItemEnergy(stored + moved));
            return moved;
        }
        public long extractEnergyL(net.minecraft.world.item.ItemStack stack, long amount, boolean simulate) {
            long stored = getEnergyStoredL(stack);
            long moved = Math.min(stored, amount);
            if (!simulate) stack.set(mcjty.lib.setup.Registration.ITEM_ENERGY.get(), new mcjty.lib.api.power.ItemEnergy(stored - moved));
            return moved;
        }
    }

    private static void require(boolean condition,String message) {
        if(!condition)throw new AssertionError(message);
    }

}
