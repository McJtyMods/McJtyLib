package mcjty.lib.base;

import mcjty.lib.McJtyLib;
import mcjty.lib.varia.Logging;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class GeneralConfig {

    private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec SERVER_CONFIG;
    public static final ModConfigSpec CLIENT_CONFIG;

    static {
        StyleConfig.init(CLIENT_BUILDER);
        GeneralConfig.init(SERVER_BUILDER);

        SERVER_CONFIG = SERVER_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    public static final String CATEGORY_GENERAL = "general";

    public static ModConfigSpec.IntValue maxInfuse;

    public static ModConfigSpec.BooleanValue manageOwnership;

    public static void init(ModConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("General settings for all mods using mcjtylib").push(CATEGORY_GENERAL);

        Logging.doLogging = SERVER_BUILDER.comment("If true dump a lot of logging information about various things. Useful for debugging")
                .define("logging", false);
        manageOwnership = SERVER_BUILDER.comment("If true then blocks using mcjtylib will have ownership tagged on them (useful for the rftools security manager)")
                .define("manageOwnership", true);
        maxInfuse = SERVER_BUILDER.comment("The maximum amount of dimensional shards that can be infused in a single machine")
                .defineInRange("maxInfuse", 256, 1, Integer.MAX_VALUE);

        SERVER_BUILDER.pop();
    }

    public static void onLoad(final ModConfigEvent.Loading configEvent) {
        StyleConfig.updateColors();
    }

    public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
        StyleConfig.updateColors();
    }
}
