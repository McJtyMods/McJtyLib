package mcjty.lib.compat;

import mcjty.lib.compat.theoneprobe.TOPCompatibility;
import net.fabricmc.loader.api.FabricLoader;

public class MainCompatHandler {

    public static void registerWaila() {
//        if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("waila")) {
            // @todo 1.14
//            WailaCompatibility.register();
//        }
    }

    public static void registerTOP() {
        if (FabricLoader.getInstance().isModLoaded("theoneprobe")) {
            TOPCompatibility.register();
        }
    }

}
