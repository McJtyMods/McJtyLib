package mcjty.lib;

import mcjty.lib.base.GeneralConfig;
import mcjty.lib.multipart.MultipartModelLoader;
import mcjty.lib.network.IServerCommand;
import mcjty.lib.preferences.PreferencesProperties;
import mcjty.lib.setup.*;
import mcjty.lib.syncpositional.PositionalDataSyncer;
import mcjty.lib.typed.TypedMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

@Mod(McJtyLib.MODID)
public class McJtyLib {

    public static final String MODID = "mcjtylib";

    public static IProxy proxy = DistExecutor.runForDist(() -> () -> new DefaultClientProxy(), () -> () -> new DefaultServerProxy());
    public static ModSetup setup = new ModSetup();

    public static McJtyLib instance;

    public static SimpleChannel networkHandler;
    public static boolean tesla, cofhapiitem;

    private static final Map<Pair<String, String>, IServerCommand> serverCommands = new HashMap<>();
    private static final Map<Pair<String, String>, IServerCommand> clientCommands = new HashMap<>();

    public static final PositionalDataSyncer SYNCER = new PositionalDataSyncer();

    public McJtyLib() {
        instance = this;
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(setup::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::init);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(MultipartModelLoader::register);
        });

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, GeneralConfig.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, GeneralConfig.SERVER_CONFIG);
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

    public static LazyOptional<PreferencesProperties> getPreferencesProperties(PlayerEntity player) {
        return player.getCapability(ModSetup.PREFERENCES_CAPABILITY);
    }
}
