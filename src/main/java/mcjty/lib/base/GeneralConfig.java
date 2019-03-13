package mcjty.lib.base;

import mcjty.lib.varia.Logging;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Level;

import java.io.File;

public class GeneralConfig {
    public static final String CATEGORY_GENERAL = "general";

    public static int maxInfuse = 256;

    private static File modConfigDir;
    private static Configuration mainConfig;

    public static boolean manageOwnership = true;
    public static boolean tallChunkFriendly = false;

    public static void init(Configuration cfg) {
        Logging.doLogging = cfg.get(CATEGORY_GENERAL, "logging", Logging.doLogging,
                "If true dump a lot of logging information about various things. Useful for debugging.").getBoolean();

        manageOwnership = cfg.get(CATEGORY_GENERAL, "manageOwnership", manageOwnership,
                "If true then blocks using mcjtylib will have ownership tagged on them (useful for the rftools security manager)").getBoolean();
        tallChunkFriendly = cfg.get(CATEGORY_GENERAL, "tallChunkFriendly", tallChunkFriendly,
                "If true then mods using McJtyLib might try to be as friendly as possible to mods that support very tall chunks (taller then 256). No guarantees however! Set to false for more optimal performance").getBoolean();
        maxInfuse = cfg.get(CATEGORY_GENERAL, "maxInfuse", maxInfuse,
                "The maximum amount of dimensional shards that can be infused in a single machine").getInt();
    }

    public static void init(FMLPreInitializationEvent e) {
        modConfigDir = e.getModConfigurationDirectory();
        mainConfig = new Configuration(new File(modConfigDir.getPath(), "mcjtylib.cfg"));
        try {
            mainConfig.load();
            mainConfig.addCustomCategoryComment(CATEGORY_GENERAL, "General settings for all mods using mcjtylib");
            mainConfig.addCustomCategoryComment(StyleConfig.CATEGORY_STYLE, "Style settings for all mods using mcjtylib");
            init(mainConfig);
            StyleConfig.init(mainConfig);
        } catch (RuntimeException e1) {
            FMLLog.log(Level.ERROR, e1, "Problem loading config file: mcjtylib.cfg!");
        } finally {
            if (mainConfig.hasChanged()) {
                mainConfig.save();
            }
        }
    }

}
