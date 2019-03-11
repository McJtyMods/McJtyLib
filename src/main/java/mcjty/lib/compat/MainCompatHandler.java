package mcjty.lib.compat;

import mcjty.lib.compat.theoneprobe.TOPCompatibility;
import net.minecraftforge.fml.common.Loader;
import mcjty.lib.compat.waila.WailaCompatibility;

public class MainCompatHandler {

    public static void registerWaila() {
        if (Loader.isModLoaded("waila")) {
            WailaCompatibility.register();
        }
    }

    public static void registerTOP() {
        if (Loader.isModLoaded("theoneprobe")) {
            TOPCompatibility.register();
        }
    }

}
