package mcjty.lib.modules;

import mcjty.lib.datagen.DataGen;
import net.minecraft.core.HolderLookup;

public interface IModule {

    void init();
    void initClient();
    void initConfig();

    default void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {}
}
