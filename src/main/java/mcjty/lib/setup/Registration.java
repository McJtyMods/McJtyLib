package mcjty.lib.setup;

import mcjty.lib.McJtyLib;
import mcjty.lib.crafting.CopyNBTRecipeSerializer;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class Registration {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registry.RECIPE_SERIALIZER_REGISTRY, McJtyLib.MODID);

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        RECIPE_SERIALIZERS.register(bus);
    }

//    @ObjectHolder(McJtyLib.MODID + ":generic")
//    public static MenuType GENERIC_CONTAINER_TYPE;
//    @ObjectHolder(McJtyLib.MODID + ":multipart")
//    public static MultipartBlock MULTIPART_BLOCK;
//    @ObjectHolder(McJtyLib.MODID + ":multipart")
//    public static MultipartItemBlock MULTIPART_ITEMBLOCK;

    public static RegistryObject<CopyNBTRecipeSerializer> COPYNBT_SERIALIZER = RECIPE_SERIALIZERS.register("copy_nbt", CopyNBTRecipeSerializer::new);

//    @ObjectHolder(McJtyLib.MODID + ":multipart")
//    public static BlockEntityType<?> TYPE_MULTIPART;
}
