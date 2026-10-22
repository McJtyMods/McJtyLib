package mcjty.lib;

import mcjty.lib.base.GeneralConfig;
import mcjty.lib.blockcommands.CommandInfo;
import mcjty.lib.network.IServerCommand;
import mcjty.lib.network.Networking;
import mcjty.lib.preferences.PreferencesProperties;
import mcjty.lib.setup.ClientSetup;
import mcjty.lib.setup.ModSetup;
import mcjty.lib.setup.Registration;
import mcjty.lib.typed.TypedMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.tuple.Pair;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class McJtyLib implements net.fabricmc.api.ModInitializer {

    public static final String MODID = "mcjtylib";

    public static final ModSetup setup = new ModSetup();

    public static McJtyLib instance;

    private static final Map<Pair<String, String>, IServerCommand> serverCommands = new HashMap<>();
    private static final Map<Pair<String, String>, IServerCommand> clientCommands = new HashMap<>();
    private static final Map<String, CommandInfo> commandInfos = new HashMap<>();

    @Override public void onInitialize() {
        instance = this;
        Registration.init();
        mcjty.lib.varia.ItemCapabilityProvider.register();
        mcjty.lib.varia.GlobalLootFunctions.register();
        mcjty.lib.varia.LevelTools.registerLifecycle();
        Networking.registerMessages();
        setup.init();
        fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry.INSTANCE.register(MODID, net.neoforged.fml.config.ModConfig.Type.CLIENT, GeneralConfig.CLIENT_CONFIG);
        fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry.INSTANCE.register(MODID, net.neoforged.fml.config.ModConfig.Type.SERVER, GeneralConfig.SERVER_CONFIG);
        fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents.loading(MODID).register(GeneralConfig::onLoad);
        fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents.reloading(MODID).register(GeneralConfig::onFileChange);
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

    public static PreferencesProperties getPreferencesProperties(Player player) {
        return ((net.fabricmc.fabric.api.attachment.v1.AttachmentTarget) player).getAttachedOrCreate(Registration.PREFERENCES_PROPERTIES);
    }

    public static void setPreferencesProperties(Player player, PreferencesProperties properties) {
        ((net.fabricmc.fabric.api.attachment.v1.AttachmentTarget) player).setAttached(Registration.PREFERENCES_PROPERTIES, properties);
    }
}
