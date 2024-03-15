package mcjty.lib.setup;

import mcjty.lib.McJtyLib;
import mcjty.lib.network.Networking;
import mcjty.lib.preferences.PreferencesProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;

import static mcjty.lib.McJtyLib.MODID;

public class ModSetup extends DefaultModSetup {

    public static boolean patchouli = false;

    // @todo NEO
//    @SubscribeEvent
//    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
//        CapabilityContainerProvider.register(event);
//        CapabilityInfusable.register(event);
//        CapabilityPowerInformation.register(event);
//        CapabilityModuleSupport.register(event);
//        PreferencesProperties.register(event);
//    }

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);
        NeoForge.EVENT_BUS.register(new EventHandler());
    }

    @Override
    protected void setupModCompat() {
        patchouli = ModList.get().isLoaded("patchouli");
    }

    public static class EventHandler {

        @SubscribeEvent
        public void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
            if (event.phase == TickEvent.Phase.START && !event.player.getCommandSenderWorld().isClientSide) {
                PreferencesProperties properties = McJtyLib.getPreferencesProperties(event.player);
                if (properties != null) {
                    properties.tick((ServerPlayer) event.player);
                }
            }
        }

        // @todo NEO
//        @SubscribeEvent
//        public void onEntityConstructing(AttachCapabilitiesEvent<Entity> event){
//            if (event.getObject() instanceof Player) {
//                if (!event.getCapabilities().containsKey(PREFERENCES_CAPABILITY_KEY) && !event.getObject().getCapability(PREFERENCES_CAPABILITY).isPresent()) {
//                    event.addCapability(PREFERENCES_CAPABILITY_KEY, new PreferencesDispatcher());
//                } else {
//                    throw new IllegalStateException(event.getObject().toString());
//                }
//            }
//        }

        // @todo multipart
//        @SubscribeEvent
//        public void onPlayerInteract(PlayerInteractEvent.LeftClickBlock event) {
//            Level world = event.getLevel();
//            BlockPos pos = event.getPos();
//            BlockState state = world.getBlockState(pos);
//            if (state.getBlock() instanceof MultipartBlock) {
//                BlockEntity tileEntity = world.getBlockEntity(pos);
//                if (tileEntity instanceof MultipartTE) {
//                    if (!world.isClientSide) {
//
//                        // @todo 1.14 until LeftClickBlock has 'hitVec' again we need to do this:
//                        Player player = event.getEntity();
//                        Vec3 start = player.getEyePosition(1.0f);
//                        Vec3 vec31 = player.getViewVector(1.0f);
//                        float dist = 20;
//                        Vec3 end = start.add(vec31.x * dist, vec31.y * dist, vec31.z * dist);
//                        ClipContext context = new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player);
//                        HitResult result = player.getCommandSenderWorld().clip(context);
//                        Vec3 hitVec = result.getLocation();
//
//                        if (MultipartHelper.removePart((MultipartTE) tileEntity, state, player, hitVec/*@todo*/)) {
//                            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
//                        }
//                    }
//                }
//                event.setCanceled(true);
//            }
//        }
//
    }

}
