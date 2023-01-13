package mcjty.lib.varia;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.List;
import java.util.function.Supplier;

public class ClientTools {

    public static void enableKeyboardRepeat() {
        Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(true);
    }

    public static void onTextureStitch(IEventBus bus, Supplier<List<ResourceLocation>> textures) {
        bus.addListener((TextureStitchEvent.Pre event) -> {
            if (!event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
                return;
            }
            for (ResourceLocation location : textures.get()) {
                event.addSprite(location);
            }
        });
    }
}
