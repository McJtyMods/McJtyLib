package mcjty.lib.compat;

import cpw.mods.fml.common.Loader;
import mcjty.lib.compat.waila.WailaCompatibility;

/**
 * Created by Elec332 on 17-10-2015.
 */
public class MainCompatHandler {

    public static void registerWaila(){
        if (Loader.isModLoaded("Waila")){
            WailaCompatibility.register();
        }
    }

}
