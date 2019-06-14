package mcjty.lib.setup;

import mcjty.lib.McJtyLib;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.multipart.MultipartBlock;
import mcjty.lib.multipart.MultipartItemBlock;
import mcjty.lib.multipart.MultipartTE;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = McJtyLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registration {

    @ObjectHolder("mcjtylib:generic")
    public static ContainerType GENERIC_CONTAINER_TYPE;
    @ObjectHolder("mcjtylib:multipart")
    public static MultipartBlock multipartBlock;
    @ObjectHolder("mcjtylib:multipart")
    public static MultipartItemBlock multipartItemBlock;

    public static TileEntityType<?> TYPE_MULTIPART;

    @SubscribeEvent
    public static void registerTiles(final RegistryEvent.Register<TileEntityType<?>> registry) {
        registry.getRegistry().register(TYPE_MULTIPART = TileEntityType.Builder.create(MultipartTE::new).build(null).setRegistryName(new ResourceLocation(McJtyLib.MODID, "multipart")));
    }

    @SubscribeEvent
    public static void onContainerTypeRegister(final RegistryEvent.Register<ContainerType<?>> e) {
        e.getRegistry().register(
                new ContainerType<>((id, inv) -> new GenericContainer(id)).setRegistryName(McJtyLib.MODID, "generic"));
    }

    @SubscribeEvent
    public static void onBlockRegister(final RegistryEvent.Register<Block> e) {
        e.getRegistry().register(new MultipartBlock());
    }

    @SubscribeEvent
    public static void onItemRegister(final RegistryEvent.Register<Item> e) {
        e.getRegistry().register(new MultipartItemBlock(multipartBlock).setRegistryName(multipartBlock.getRegistryName()));
    }
}
