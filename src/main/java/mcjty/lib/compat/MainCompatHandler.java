package mcjty.lib.compat;

import mcjty.lib.compat.theoneprobe.TOPCompatibility;
import net.neoforged.neoforge.fml.ModList;

public class MainCompatHandler {

    public static void registerWaila() {
//        if (ModList.get().isLoaded("waila")) {
            // @todo 1.14
//            WailaCompatibility.register();
//        }
    }

    public static void registerTOP() {
        if (ModList.get().isLoaded("theoneprobe")) {
            TOPCompatibility.register();
        }
    }

}
