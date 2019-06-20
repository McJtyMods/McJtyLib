package mcjty.lib;

import mcjty.lib.base.GeneralConfig;
import mcjty.lib.base.ModBase;
import mcjty.lib.network.IServerCommand;
import mcjty.lib.preferences.PreferencesProperties;
import mcjty.lib.setup.ClientProxy;
import mcjty.lib.setup.IProxy;
import mcjty.lib.setup.ModSetup;
import mcjty.lib.setup.ServerProxy;
import mcjty.lib.typed.TypedMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Mod(McJtyLib.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
//
//@Mod(modid = McJtyLib.PROVIDES, name = "McJtyLib",
//        acceptedMinecraftVersions = "[1.12,1.13)",
//        version = McJtyLib.VERSION,
//        dependencies = "after:forge@[14.23.5.2800,);after:enderio@[5.0.21,)")
public class McJtyLib implements ModBase {

    public static final String VERSION = "3.5.3";
    public static final String MODID = "mcjtylib";

    public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
    public static ModSetup setup = new ModSetup();

    public static McJtyLib instance;

    public static SimpleChannel networkHandler;
    public static boolean tesla, cofhapiitem;

    private static final Map<Pair<String, String>, IServerCommand> serverCommands = new HashMap<>();
    private static final Map<Pair<String, String>, IServerCommand> clientCommands = new HashMap<>();

    private static final Map<String, ModBase> mods = new HashMap<>();

    public McJtyLib() {
        instance = this;
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, GeneralConfig.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, GeneralConfig.COMMON_CONFIG);

        GeneralConfig.loadConfig(GeneralConfig.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve("mcjtylib-client.toml"));
        GeneralConfig.loadConfig(GeneralConfig.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("mcjtylib-common.toml"));
    }

    public static void registerMod(ModBase mod) {
        mods.put(mod.getModId(), mod);
    }

    public static void forEachMod(Consumer<ModBase> consumer) {
        for (ModBase mod : mods.values()) {
            consumer.accept(mod);
        }
    }

    @Override
    public String getModId() {
        return MODID;
    }

    @Override
    public void openManual(PlayerEntity player, int bookindex, String page) {

    }

    public static void registerCommand(String modid, String id, IServerCommand command) {
        serverCommands.put(Pair.of(modid, id), command);
    }

    public static void registerClientCommand(String modid, String id, IServerCommand command) {
        clientCommands.put(Pair.of(modid, id), command);
    }

    public static boolean handleCommand(String modid, String id, PlayerEntity player, TypedMap arguments) {
        IServerCommand command = serverCommands.get(Pair.of(modid, id));
        if (command == null) {
            return false;
        }
        return command.execute(player, arguments);
    }

    public static boolean handleClientCommand(String modid, String id, PlayerEntity player, TypedMap arguments) {
        IServerCommand command = clientCommands.get(Pair.of(modid, id));
        if (command == null) {
            return false;
        }
        return command.execute(player, arguments);
    }

    public void init(final FMLCommonSetupEvent event) {
        setup.init(event);
        proxy.init(event);
    }

    public static LazyOptional<PreferencesProperties> getPreferencesProperties(PlayerEntity player) {
        return player.getCapability(ModSetup.PREFERENCES_CAPABILITY);
    }
}
