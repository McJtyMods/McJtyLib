package mcjty.lib.client;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.Map;
import java.util.function.Consumer;

public class ModelTools {

    public static void registerModelBakeEvent(IEventBus bus, Consumer<Map<ResourceLocation, BakedModel>> modelConsumer) {
        bus.addListener((ModelEvent.ModifyBakingResult event) -> {
            modelConsumer.accept(event.getModels());
        });
    }
}
