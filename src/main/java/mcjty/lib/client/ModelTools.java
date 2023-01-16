package mcjty.lib.client;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Map;
import java.util.function.Consumer;

public class ModelTools {

    public static void registerModelBakeEvent(Consumer<Map<ResourceLocation, BakedModel>> modelConsumer) {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener((ModelEvent.BakingCompleted event) -> {
            modelConsumer.accept(event.getModels());
        });
    }
}
