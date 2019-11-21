package mcjty.lib.setup;

import mcjty.lib.McJtyLib;
import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.information.CapabilityPowerInformation;
import mcjty.lib.api.infusable.CapabilityInfusable;
import mcjty.lib.api.module.CapabilityModuleSupport;
import mcjty.lib.multipart.MultipartBlock;
import mcjty.lib.multipart.MultipartHelper;
import mcjty.lib.multipart.MultipartTE;
import mcjty.lib.network.PacketHandler;
import mcjty.lib.preferences.PreferencesDispatcher;
import mcjty.lib.preferences.PreferencesProperties;
import mcjty.lib.varia.WrenchChecker;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkRegistry;

import static mcjty.lib.McJtyLib.MODID;

public class ModSetup extends DefaultModSetup {

    public static ResourceLocation PREFERENCES_CAPABILITY_KEY;

    @CapabilityInject(PreferencesProperties.class)
    public static Capability<PreferencesProperties> PREFERENCES_CAPABILITY;

    private static void registerCapabilities(){
        CapabilityContainerProvider.register();
        CapabilityInfusable.register();
        CapabilityPowerInformation.register();
        CapabilityModuleSupport.register();
        PreferencesProperties.register();
    }

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);
        registerCapabilities();
        McJtyLib.networkHandler = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> "1.0", s -> true, s -> true);
        PacketHandler.registerMessages(McJtyLib.networkHandler);
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        McJtyLib.tesla = ModList.get().isLoaded("tesla");
        McJtyLib.cofhapiitem = ModList.get().isLoaded("cofhapi|item");
    }

    @Override
    protected void setupModCompat() {
        // @todo 1.14: check (right place?)
        WrenchChecker.init();
    }


    public static class EventHandler {

        private EventHandler(){
        }

        @SubscribeEvent
        public void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
            if (event.phase == TickEvent.Phase.START && !event.player.getEntityWorld().isRemote) {
                McJtyLib.getPreferencesProperties(event.player).ifPresent(handler -> handler.tick((ServerPlayerEntity) event.player));
            }
        }

        @SubscribeEvent
        public void onEntityConstructing(AttachCapabilitiesEvent<Entity> event){
            if (event.getObject() instanceof PlayerEntity) {
                if (!event.getCapabilities().containsKey(PREFERENCES_CAPABILITY_KEY) && !event.getObject().getCapability(PREFERENCES_CAPABILITY).isPresent()) {
                    event.addCapability(PREFERENCES_CAPABILITY_KEY, new PreferencesDispatcher());
                } else {
                    throw new IllegalStateException(event.getObject().toString());
                }
            }
        }

        @SubscribeEvent
        public void onPlayerInteract(PlayerInteractEvent.LeftClickBlock event) {
            World world = event.getWorld();
            BlockPos pos = event.getPos();
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof MultipartBlock) {
                TileEntity tileEntity = world.getTileEntity(pos);
                if (tileEntity instanceof MultipartTE) {
                    if (!world.isRemote) {

                        // @todo 1.14 until LeftClickBlock has 'hitVec' again we need to do this:
                        PlayerEntity player = event.getEntityPlayer();
                        Vec3d start = player.getEyePosition(1.0f);
                        Vec3d vec31 = player.getLook(1.0f);
                        float dist = 20;
                        Vec3d end = start.add(vec31.x * dist, vec31.y * dist, vec31.z * dist);
                        RayTraceContext context = new RayTraceContext(start, end, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player);
                        RayTraceResult result = player.getEntityWorld().rayTraceBlocks(context);
                        Vec3d hitVec = result == null ? null : result.getHitVec();

                        if (MultipartHelper.removePart((MultipartTE) tileEntity, state, player, hitVec/*@todo*/)) {
                            world.setBlockState(pos, Blocks.AIR.getDefaultState());
                        }
                    }
                }
                event.setCanceled(true);
            }
        }

    }

    static {
        ModSetup.PREFERENCES_CAPABILITY_KEY = new ResourceLocation(MODID, "preferences");
    }


}
