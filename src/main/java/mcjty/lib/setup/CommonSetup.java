package mcjty.lib.setup;

import mcjty.lib.McJtyLib;
import mcjty.lib.McJtyRegister;
import mcjty.lib.multipart.MultipartBlock;
import mcjty.lib.multipart.MultipartTE;
import mcjty.lib.setup.DefaultCommonSetup;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonSetup extends DefaultCommonSetup {

    public static MultipartBlock multipartBlock;

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        multipartBlock = new MultipartBlock();
        GameRegistry.registerTileEntity(MultipartTE.class, new ResourceLocation(McJtyLib.PROVIDES, "multipart_te"));
        MinecraftForge.EVENT_BUS.register(new BlockRegister());
    }

    @Override
    protected void setupModCompat() {

    }

    @Override
    public void createTabs() {
    }

    private static class BlockRegister {
        @SubscribeEvent
        public void registerBlocks(RegistryEvent.Register<Block> event) {
            McJtyRegister.registerBlocks(McJtyLib.instance, event.getRegistry());
        }

        @SubscribeEvent
        public void registerItems(RegistryEvent.Register<Item> event) {
            McJtyRegister.registerItems(McJtyLib.instance, event.getRegistry());
        }

    }
}
