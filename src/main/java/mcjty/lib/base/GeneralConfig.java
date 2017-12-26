package mcjty.lib.base;

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

    public static void init(Configuration cfg) {
        manageOwnership = cfg.get(CATEGORY_GENERAL, "manageOwnership", manageOwnership,
                "If true then blocks using mcjtylib will have ownership tagged on them (useful for the rftools security manager)").getBoolean();
        maxInfuse = cfg.get(CATEGORY_GENERAL, "maxInfuse", maxInfuse,
                "The maximum amount of dimensional shards that can be infused in a single machine").getInt();
    }

    public static void preInit(FMLPreInitializationEvent e) {
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
