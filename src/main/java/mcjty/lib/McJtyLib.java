package mcjty.lib;

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
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by Elec332 on 24-3-2016.
 */
@SuppressWarnings("unused")
public class McJtyLib {

    public static final String VERSION = "2.4.4";
    public static final String OWNER = "McJty", PROVIDES = "mcjtylib_ng";

    private static final ResourceLocation PREFERENCES_CAPABILITY_KEY;

    @CapabilityInject(PreferencesProperties.class)
    public static Capability<PreferencesProperties> PREFERENCES_CAPABILITY;

    public static SimpleNetworkWrapper networkHandler;
    private static boolean init;
    public static boolean redstoneflux;

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
