package mcjty.test;

import mcjty.lib.McJtyLib;
import mcjty.lib.McJtyLibClient;
import mcjty.lib.base.ModBase;
import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Logger;

import static mcjty.test.TestMod.MODID;

@Mod(modid = MODID, name="testmod")
public class TestMod implements ModBase {

    public static final String MODID = "testmod";

    public static final int TESTGUI = 1;

    @Override
    public String getModId() {
        return MODID;
    }

    @Override
    public void openManual(EntityPlayer player, int bookindex, String page) {

    }

    @SidedProxy
    public static CommonProxy proxy;

    @Mod.Instance
    public static TestMod instance;

    public static Logger logger;

    public static SimpleNetworkWrapper network;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }

    public static class CommonProxy {
        public void preInit(FMLPreInitializationEvent e) {
            McJtyLib.preInit(e);

            // Initialize our packet handler. Make sure the name is
            // 20 characters or less!
            network = PacketHandler.registerMessages(MODID, "testmod_msg");
//            RFToolsMessages.registerNetworkMessages(network);

            // Initialization of blocks and items typically goes here:
            ModBlocks.init();
//            ModItems.init();

            MainCompatHandler.registerWaila();
            MainCompatHandler.registerTOP();

        }

        public void init(FMLInitializationEvent e) {
            NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiProxy());
        }

        public void postInit(FMLPostInitializationEvent e) {

        }
    }


    public static class ClientProxy extends CommonProxy {
        @Override
        public void preInit(FMLPreInitializationEvent e) {
            super.preInit(e);

//            OBJLoader.INSTANCE.addDomain(MODID);
//            ModelLoaderRegistry.registerLoader(new BakedModelLoader());

            // Typically initialization of models and such goes here:
            ModBlocks.initModels();
//            ModItems.initModels();

            McJtyLibClient.preInit(e);
        }

        @Override
        public void init(FMLInitializationEvent e) {
            super.init(e);

//            ModBlocks.initItemModels();
        }
    }

    @SuppressWarnings("EmptyClass")
    public static class ServerProxy extends CommonProxy {

    }
}
