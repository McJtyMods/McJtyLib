package mcjty.lib.setup;

import mcjty.lib.ClientEventHandler;
import mcjty.lib.keys.KeyBindings;
import mcjty.lib.keys.KeyInputHandler;
import mcjty.lib.multipart.MultipartModelLoader;
import mcjty.lib.tooltips.TooltipRender;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientSetup {

    public static void init(FMLClientSetupEvent e) {
        MinecraftForge.EVENT_BUS.register(new TooltipRender());
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());

        KeyBindings.init();
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());

        RenderTypeLookup.setRenderLayer(Registration.MULTIPART_BLOCK, (RenderType) -> true);
    }

}
