package mcjty.lib.client;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.Map;
import java.util.function.Consumer;

public class ModelTools {

    public static void registerModelBakeEvent(IEventBus bus, Consumer<Map<ModelResourceLocation, BakedModel>> modelConsumer) {
        bus.addListener((ModelEvent.ModifyBakingResult event) -> {
            modelConsumer.accept(event.getModels());
        });
    }
}
