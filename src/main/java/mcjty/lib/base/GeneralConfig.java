package mcjty.lib.base;

import mcjty.lib.McJtyLib;
import mcjty.lib.varia.Logging;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = McJtyLib.MODID)
public class GeneralConfig {

    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec SERVER_CONFIG;
    public static final ForgeConfigSpec CLIENT_CONFIG;

    static {
        StyleConfig.init(CLIENT_BUILDER);
        GeneralConfig.init(SERVER_BUILDER);

        SERVER_CONFIG = SERVER_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    public static final String CATEGORY_GENERAL = "general";

    public static ForgeConfigSpec.IntValue maxInfuse;

    public static ForgeConfigSpec.BooleanValue manageOwnership;

    public static void init(ForgeConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("General settings for all mods using mcjtylib").push(CATEGORY_GENERAL);

        Logging.doLogging = SERVER_BUILDER.comment("If true dump a lot of logging information about various things. Useful for debugging")
                .define("logging", false);
        manageOwnership = SERVER_BUILDER.comment("If true then blocks using mcjtylib will have ownership tagged on them (useful for the rftools security manager)")
                .define("manageOwnership", true);
        maxInfuse = SERVER_BUILDER.comment("The maximum amount of dimensional shards that can be infused in a single machine")
                .defineInRange("maxInfuse", 256, 1, Integer.MAX_VALUE);

        SERVER_BUILDER.pop();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent) {
        StyleConfig.updateColors();
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
        StyleConfig.updateColors();
    }
}
