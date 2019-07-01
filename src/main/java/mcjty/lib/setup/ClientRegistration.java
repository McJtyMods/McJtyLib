package mcjty.lib.setup;

import mcjty.lib.ClientEventHandler;
import mcjty.lib.multipart.MultipartModelLoader;
import mcjty.lib.tooltips.TooltipRender;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientRegistration {

    public static void init(FMLClientSetupEvent e) {
        MinecraftForge.EVENT_BUS.register(new TooltipRender());
        MinecraftForge.EVENT_BUS.register(new McJtyLibBlockRegister());
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());

        ModelLoaderRegistry.registerLoader(new MultipartModelLoader());
    }

    private static class McJtyLibBlockRegister {
        @SubscribeEvent
        public void registerModels(ModelRegistryEvent event) {
            Registration.multipartBlock.initModel();
        }

    }

}
