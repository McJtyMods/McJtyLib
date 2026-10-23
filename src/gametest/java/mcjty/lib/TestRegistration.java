package mcjty.lib;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = McJtyLib.MODID)
public final class TestRegistration {
    private record Test(String name, Consumer<GameTestHelper> function) {}
    private static final List<Test> TESTS = List.of(
            new Test("item_rollback", TransferGameTests::itemTransfersRespectCapacityAndRollback),
            new Test("ghost_slots", TransferGameTests::ghostSlotsAreEditableButCannotTransferRealItems),
            new Test("item_notifications", TransferGameTests::itemNotificationsOnlyFireAfterRootCommit),
            new Test("fluid_rollback", TransferGameTests::fluidSimulationAndNestedAbortPreserveContents),
            new Test("undo_transfers", TransferGameTests::undoRestoresSlotlessTransfers),
            new Test("automation", TransferGameTests::automationRespectsInputAndOutputSlots),
            new Test("inventory_persistence", TransferGameTests::inventorySaveRetainsIndexed26_2Layout),
            new Test("fluid_containers", TransferGameTests::fluidContainersCanReplaceBucketItems));

    @SubscribeEvent
    public static void functions(RegisterEvent event) {
        event.register(Registries.TEST_FUNCTION, registry -> {
            for (var test : TESTS) registry.register(id(test.name), test.function);
        });
    }

    @SubscribeEvent
    public static void tests(RegisterGameTestsEvent event) {
        var environment = event.registerEnvironment(id("default"));
        for (var test : TESTS) {
            event.registerTest(id(test.name), new FunctionGameTestInstance(
                    ResourceKey.create(Registries.TEST_FUNCTION, id(test.name)),
                    new TestData<>(environment, id("empty"), 100, 0, true)));
        }
    }

    private static Identifier id(String name) { return Identifier.fromNamespaceAndPath(McJtyLib.MODID, name); }
}
