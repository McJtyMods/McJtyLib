package mcjty.lib;

import mcjty.lib.base.GeneralConfig;
import mcjty.lib.blockcommands.CommandInfo;
import mcjty.lib.multipart.MultipartModelLoader;
import mcjty.lib.network.IServerCommand;
import mcjty.lib.preferences.PreferencesProperties;
import mcjty.lib.setup.*;
import mcjty.lib.syncpositional.PositionalDataSyncer;
import mcjty.lib.typed.TypedMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
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
import java.util.function.BiConsumer;
import java.util.function.Function;

@Mod(McJtyLib.MODID)
public class McJtyLib {

    public static final String MODID = "mcjtylib";

    public static final IProxy proxy = DistExecutor.runForDist(() -> () -> new DefaultClientProxy(), () -> () -> new DefaultServerProxy());
    public static final ModSetup setup = new ModSetup();

    public static McJtyLib instance;

    public static SimpleChannel networkHandler;
    public static boolean tesla;
    public static boolean cofhapiitem;

    private static final Map<Pair<String, String>, IServerCommand> serverCommands = new HashMap<>();
    private static final Map<Pair<String, String>, IServerCommand> clientCommands = new HashMap<>();
    private static final Map<String, CommandInfo> commandInfos = new HashMap<>();

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

    public static <T> void registerCommandInfo(String command, Class<T> type, Function<PacketBuffer, T> deserializer, BiConsumer<PacketBuffer, T> serializer) {
        if (commandInfos.containsKey(command)) {
            throw new IllegalStateException(("The command '" + command + "' is already registered!"));
        }
        System.out.println("McJtyLib.registerCommandInfo: " + command);
        commandInfos.put(command, new CommandInfo<T>(type, deserializer, serializer));
    }

    public static CommandInfo getCommandInfo(String command) {
        return commandInfos.get(command);
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
