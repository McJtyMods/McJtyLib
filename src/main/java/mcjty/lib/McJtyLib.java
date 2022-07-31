package mcjty.lib;

import mcjty.lib.base.GeneralConfig;
import mcjty.lib.blockcommands.CommandInfo;
import mcjty.lib.network.IServerCommand;
import mcjty.lib.preferences.PreferencesProperties;
import mcjty.lib.setup.ClientSetup;
import mcjty.lib.setup.ModSetup;
import mcjty.lib.setup.Registration;
import mcjty.lib.syncpositional.PositionalDataSyncer;
import mcjty.lib.typed.TypedMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Mod(McJtyLib.MODID)
public class McJtyLib {

    public static final String MODID = "mcjtylib";

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
        Registration.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(setup::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
            modbus.addListener(ClientSetup::init);
            modbus.addListener(ClientSetup::registerKeyBinds);
            modbus.addListener(ClientSetup::registerClientComponentTooltips);
//            modbus.addListener(MultipartModelLoader::register);
        });

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, GeneralConfig.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, GeneralConfig.SERVER_CONFIG);
    }

    /**
     * This is automatically called by annotated ListCommands (@ServerCommand) if they have
     * an associated type parameter
     */
    public static <T> void registerListCommandInfo(String command, Class<T> type, Function<FriendlyByteBuf, T> deserializer, BiConsumer<FriendlyByteBuf, T> serializer) {
        commandInfos.put(command, new CommandInfo<T>(type, deserializer, serializer));
    }

    public static CommandInfo getCommandInfo(String command) {
        return commandInfos.get(command);
    }

    /**
     * Used in combination with PacketSendServerCommand for a more global command
     */
    public static void registerCommand(String modid, String id, IServerCommand command) {
        serverCommands.put(Pair.of(modid, id), command);
    }

    public static void registerClientCommand(String modid, String id, IServerCommand command) {
        clientCommands.put(Pair.of(modid, id), command);
    }

    public static boolean handleCommand(String modid, String id, Player player, TypedMap arguments) {
        IServerCommand command = serverCommands.get(Pair.of(modid, id));
        if (command == null) {
            return false;
        }
        return command.execute(player, arguments);
    }

    public static boolean handleClientCommand(String modid, String id, Player player, TypedMap arguments) {
        IServerCommand command = clientCommands.get(Pair.of(modid, id));
        if (command == null) {
            return false;
        }
        return command.execute(player, arguments);
    }

    public static LazyOptional<PreferencesProperties> getPreferencesProperties(Player player) {
        return player.getCapability(ModSetup.PREFERENCES_CAPABILITY);
    }
}
