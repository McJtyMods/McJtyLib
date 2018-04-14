package mcjty.lib;

import mcjty.lib.network.Arguments;
import mcjty.lib.network.IServerCommand;
import mcjty.lib.network.PacketSendPreferencesToClient;
import mcjty.lib.network.PacketSetGuiStyle;
import mcjty.lib.preferences.PreferencesDispatcher;
import mcjty.lib.preferences.PreferencesProperties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Elec332 on 24-3-2016.
 */
@SuppressWarnings("unused")
@Mod(modid = McJtyLib.PROVIDES, name = "McJtyLib",
        acceptedMinecraftVersions = "[1.12,1.13)",
        version = McJtyLib.VERSION,
        dependencies = "after:enderio@[5.0.21,)")
public class McJtyLib {

    public static final String VERSION = "2.6.7";
    public static final String PROVIDES = "mcjtylib_ng";

    private static final ResourceLocation PREFERENCES_CAPABILITY_KEY;

    @CapabilityInject(PreferencesProperties.class)
    public static Capability<PreferencesProperties> PREFERENCES_CAPABILITY;

    public static SimpleNetworkWrapper networkHandler;
    private static boolean init;
    public static boolean redstoneflux;

    private static final Map<Pair<String, String>, IServerCommand> serverCommands = new HashMap<>();
    private static final Map<Pair<String, String>, IServerCommand> clientCommands = new HashMap<>();

    public static void registerCommand(String modid, String id, IServerCommand command) {
        serverCommands.put(Pair.of(modid, id), command);
    }

    public static void registerClientCommand(String modid, String id, IServerCommand command) {
        clientCommands.put(Pair.of(modid, id), command);
    }

    public static boolean handleCommand(String modid, String id, EntityPlayer player, Arguments arguments) {
        IServerCommand command = serverCommands.get(Pair.of(modid, id));
        if (command == null) {
            return false;
        }
        return command.execute(player, arguments);
    }

    public static boolean handleClientCommand(String modid, String id, EntityPlayer player, Arguments arguments) {
        IServerCommand command = clientCommands.get(Pair.of(modid, id));
        if (command == null) {
            return false;
        }
        return command.execute(player, arguments);
    }

    public static void preInit(FMLPreInitializationEvent event){
        if (init) {
            return;
        }
        registerCapabilities();
        networkHandler = new SimpleNetworkWrapper(PROVIDES);
        networkHandler.registerMessage(PacketSendPreferencesToClient.Handler.class, PacketSendPreferencesToClient.class, 0, Side.CLIENT);
        networkHandler.registerMessage(PacketSetGuiStyle.Handler.class, PacketSetGuiStyle.class, 1, Side.SERVER);
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        init = true;
        redstoneflux = Loader.isModLoaded("redstoneflux");
    }

    public static PreferencesProperties getPreferencesProperties(EntityPlayer player) {
        return player.getCapability(PREFERENCES_CAPABILITY, null);
    }

    public static class EventHandler {

        private EventHandler(){
        }

        @SubscribeEvent
        public void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
            if (event.phase == TickEvent.Phase.START && !event.player.getEntityWorld().isRemote) {
                PreferencesProperties preferencesProperties = getPreferencesProperties(event.player);
                preferencesProperties.tick((EntityPlayerMP) event.player);
            }
        }

        @SubscribeEvent
        public void onEntityConstructing(AttachCapabilitiesEvent<Entity> event){
            if (event.getObject() instanceof EntityPlayer) {
                if (!event.getCapabilities().containsKey(PREFERENCES_CAPABILITY_KEY) && !event.getObject().hasCapability(PREFERENCES_CAPABILITY, null)) {
                    event.addCapability(PREFERENCES_CAPABILITY_KEY, new PreferencesDispatcher());
                } else {
                    throw new IllegalStateException(event.getObject().toString());
                }
            }
        }
    }

    private static void registerCapabilities(){
        CapabilityManager.INSTANCE.register(PreferencesProperties.class, new Capability.IStorage<PreferencesProperties>() {

            @Override
            public NBTBase writeNBT(Capability<PreferencesProperties> capability, PreferencesProperties instance, EnumFacing side) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void readNBT(Capability<PreferencesProperties> capability, PreferencesProperties instance, EnumFacing side, NBTBase nbt) {
                throw new UnsupportedOperationException();
            }

        }, () -> {
            throw new UnsupportedOperationException();
        });
    }

    static {
        PREFERENCES_CAPABILITY_KEY = new ResourceLocation(PROVIDES, "Preferences");
    }

}
