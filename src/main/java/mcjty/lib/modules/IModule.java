package mcjty.lib.modules;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public interface IModule {

    void init(FMLCommonSetupEvent event);
    void initClient(FMLClientSetupEvent event);
    void initConfig();
}
