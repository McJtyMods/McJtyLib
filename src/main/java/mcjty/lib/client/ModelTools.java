package mcjty.lib.client;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.Map;
import java.util.function.Consumer;

public class ModelTools {

    public static void registerModelBakeEvent(IEventBus bus, Consumer<Map<BlockState, BlockStateModel>> modelConsumer) {
        bus.addListener((ModelEvent.ModifyBakingResult event) -> {
            modelConsumer.accept(event.getBakingResult().blockStateModels());
        });
    }
}
