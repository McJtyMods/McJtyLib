package mcjty.lib.setup;

import mcjty.lib.McJtyLib;
import mcjty.lib.crafting.CopyNBTRecipeSerializer;
import mcjty.lib.multipart.MultipartBlock;
import mcjty.lib.multipart.MultipartItemBlock;
import mcjty.lib.multipart.MultipartTE;
import mcjty.lib.varia.Tools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = McJtyLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registration {

    @ObjectHolder(McJtyLib.MODID + ":generic")
    public static MenuType GENERIC_CONTAINER_TYPE;
    @ObjectHolder(McJtyLib.MODID + ":multipart")
    public static MultipartBlock MULTIPART_BLOCK;
    @ObjectHolder(McJtyLib.MODID + ":multipart")
    public static MultipartItemBlock MULTIPART_ITEMBLOCK;

    @ObjectHolder(McJtyLib.MODID + ":copy_nbt")
    public static CopyNBTRecipeSerializer COPYNBT_SERIALIZER;

    @ObjectHolder(McJtyLib.MODID + ":multipart")
    public static BlockEntityType<?> TYPE_MULTIPART;

    @SubscribeEvent
    public static void registerTiles(final RegistryEvent.Register<BlockEntityType<?>> registry) {
        registry.getRegistry().register(TYPE_MULTIPART = BlockEntityType.Builder.of(MultipartTE::new).build(null).setRegistryName(new ResourceLocation(McJtyLib.MODID, "multipart")));
    }

    @SubscribeEvent
    public static void onBlockRegister(final RegistryEvent.Register<Block> e) {
        e.getRegistry().register(new MultipartBlock());
    }

    @SubscribeEvent
    public static void onItemRegister(final RegistryEvent.Register<Item> e) {
        e.getRegistry().register(new MultipartItemBlock(MULTIPART_BLOCK).setRegistryName(Tools.getId(MULTIPART_BLOCK)));
    }

    @SubscribeEvent
    public static void onRecipeRegister(final RegistryEvent.Register<RecipeSerializer<?>> e) {
        e.getRegistry().register(new CopyNBTRecipeSerializer().setRegistryName(new ResourceLocation(McJtyLib.MODID, "copy_nbt")));
    }
}
