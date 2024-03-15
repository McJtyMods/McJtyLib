package mcjty.lib.setup;

import mcjty.lib.McJtyLib;
import mcjty.lib.crafting.CopyNBTRecipeSerializer;
import mcjty.lib.preferences.PreferencesProperties;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class Registration {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, McJtyLib.MODID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, McJtyLib.MODID);

    public static void init(IEventBus bus) {
        RECIPE_SERIALIZERS.register(bus);
        ATTACHMENT_TYPES.register(bus);
    }

    public static Supplier<CopyNBTRecipeSerializer> COPYNBT_SERIALIZER = RECIPE_SERIALIZERS.register("copy_nbt", CopyNBTRecipeSerializer::new);
    private static final Supplier<AttachmentType<PreferencesProperties>> PREFERENCES_PROPERTIES = ATTACHMENT_TYPES.register(
            "preferences_properties", () -> AttachmentType.serializable(PreferencesProperties::new).build());

}
