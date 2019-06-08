package mcjty.lib.proxy;

import mcjty.lib.ClientEventHandler;
import mcjty.lib.multipart.MultipartModelLoader;
import mcjty.lib.setup.ModSetup;
import mcjty.lib.setup.DefaultClientProxy;
import mcjty.lib.tooltips.TooltipRender;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientProxy extends DefaultClientProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        MinecraftForge.EVENT_BUS.register(new TooltipRender());
        MinecraftForge.EVENT_BUS.register(new McJtyLibBlockRegister());
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());

        ModelLoaderRegistry.registerLoader(new MultipartModelLoader());
    }

    private static class McJtyLibBlockRegister {
        @SubscribeEvent
        public void registerModels(ModelRegistryEvent event) {
            ModSetup.multipartBlock.initModel();
        }

    }

}
