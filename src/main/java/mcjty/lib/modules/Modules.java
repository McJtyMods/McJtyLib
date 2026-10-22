package mcjty.lib.modules;

import mcjty.lib.datagen.DataGen;
import net.minecraft.core.HolderLookup;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Modules {

    private final List<IModule> modules = new ArrayList<>();

    public void register(IModule module) {
        modules.add(module);
    }

    public void init() {
        modules.forEach(m -> m.init());
    }

    public void initClient() {
        modules.forEach(m -> m.initClient());
    }

    public void initConfig() {
        modules.forEach(iModule -> iModule.initConfig());
    }

    public void datagen(DataGen dataGen, CompletableFuture<HolderLookup.Provider> provider) {
        modules.forEach(m -> {
            try {
                m.initDatagen(dataGen, provider.get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
