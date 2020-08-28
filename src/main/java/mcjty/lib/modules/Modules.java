package mcjty.lib.modules;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.List;

public class Modules {

    private final List<IModule> modules = new ArrayList<>();

    public void register(IModule module) {
        modules.add(module);
    }

    public void init(FMLCommonSetupEvent event) {
        modules.stream().forEach(m -> m.init(event));
    }

    public void initClient(FMLClientSetupEvent event) {
        modules.stream().forEach(m -> m.initClient(event));
    }

    public void initConfig() {
        modules.stream().forEach(IModule::initConfig);
    }
}
