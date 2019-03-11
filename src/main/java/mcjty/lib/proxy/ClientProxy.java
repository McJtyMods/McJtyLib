package mcjty.lib.proxy;

import mcjty.lib.multipart.MultipartModelLoader;
import mcjty.lib.setup.CommonSetup;
import mcjty.lib.setup.DefaultClientProxy;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends DefaultClientProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        MinecraftForge.EVENT_BUS.register(new McJtyLibBlockRegister());
        ModelLoaderRegistry.registerLoader(new MultipartModelLoader());
    }

    private static class McJtyLibBlockRegister {
        @SubscribeEvent
        public void registerModels(ModelRegistryEvent event) {
            CommonSetup.multipartBlock.initModel();
        }

    }

}
