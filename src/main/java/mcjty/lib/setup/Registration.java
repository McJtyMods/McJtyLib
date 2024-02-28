package mcjty.lib.setup;

import mcjty.lib.McJtyLib;
import mcjty.lib.crafting.CopyNBTRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.eventbus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryObject;

public class Registration {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, McJtyLib.MODID);

    public static void init(IEventBus bus) {
        RECIPE_SERIALIZERS.register(bus);
    }

    public static RegistryObject<CopyNBTRecipeSerializer> COPYNBT_SERIALIZER = RECIPE_SERIALIZERS.register("copy_nbt", CopyNBTRecipeSerializer::new);
}
