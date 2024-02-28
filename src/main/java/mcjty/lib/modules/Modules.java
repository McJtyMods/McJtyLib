package mcjty.lib.modules;

import mcjty.lib.datagen.DataGen;
import net.neoforged.neoforge.eventbus.api.IEventBus;
import net.neoforged.neoforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.List;

public class Modules {

    private final List<IModule> modules = new ArrayList<>();

    public void register(IModule module) {
        modules.add(module);
    }

    public void init(FMLCommonSetupEvent event) {
        modules.forEach(m -> m.init(event));
    }

    public void initClient(FMLClientSetupEvent event) {
        modules.forEach(m -> m.initClient(event));
    }

    public void initConfig(IEventBus bus) {
        modules.forEach(iModule -> iModule.initConfig(bus));
    }

    public void datagen(DataGen dataGen) {
        modules.forEach(m -> m.initDatagen(dataGen));
    }
}
