package mcjty.lib.base;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import mcjty.lib.varia.Logging;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;

@Mod.EventBusSubscriber
public class GeneralConfig {

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec COMMON_CONFIG;
    public static final ForgeConfigSpec SERVER_CONFIG;
    public static final ForgeConfigSpec CLIENT_CONFIG;

    static {
        StyleConfig.init(CLIENT_BUILDER);
        GeneralConfig.init(SERVER_BUILDER);

        COMMON_CONFIG = COMMON_BUILDER.build();
        SERVER_CONFIG = SERVER_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }




    public static final String CATEGORY_GENERAL = "general";

    public static ForgeConfigSpec.IntValue maxInfuse;

    public static ForgeConfigSpec.BooleanValue manageOwnership;
    public static ForgeConfigSpec.BooleanValue tallChunkFriendly;

    public static void init(ForgeConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("General settings for all mods using mcjtylib").push(CATEGORY_GENERAL);

        Logging.doLogging = SERVER_BUILDER.comment("If true dump a lot of logging information about various things. Useful for debugging")
                .define("logging", false);
        manageOwnership = SERVER_BUILDER.comment("If true then blocks using mcjtylib will have ownership tagged on them (useful for the rftools security manager)")
                .define("manageOwnership", true);
        tallChunkFriendly = SERVER_BUILDER.comment("If true then mods using McJtyLib might try to be as friendly as possible to mods that support very tall chunks (taller then 256). No guarantees however! Set to false for more optimal performance")
                .define("tallChunkFriendly", false);
        maxInfuse = SERVER_BUILDER.comment("The maximum amount of dimensional shards that can be infused in a single machine")
                .defineInRange("maxInfuse", 256, 1, Integer.MAX_VALUE);

        SERVER_BUILDER.pop();
    }


    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();
        spec.setConfig(configData);
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
        StyleConfig.updateColors();
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfig.Reloading configEvent) {
        StyleConfig.updateColors();
    }
}
