package mcjty.lib.setup;

import com.mojang.serialization.Codec;
import mcjty.lib.McJtyLib;
import mcjty.lib.api.container.ItemInventory;
import mcjty.lib.api.fluids.ItemFluids;
import mcjty.lib.api.infusable.ItemInfusable;
import mcjty.lib.api.modules.ItemModule;
import mcjty.lib.api.power.ItemEnergy;
import mcjty.lib.api.security.ItemSecurity;
import mcjty.lib.crafting.CopyComponentsRecipe;
import mcjty.lib.crafting.CopyComponentsRecipeSerializer;
import mcjty.lib.preferences.PreferencesProperties;
import mcjty.lib.tileentity.BaseBEData;
import mcjty.lib.varia.RedstoneMode;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

public final class Registration {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, McJtyLib.MODID);
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, McJtyLib.MODID);

    public static final Supplier<RecipeSerializer<CopyComponentsRecipe>> COPYNBT_SERIALIZER = RECIPE_SERIALIZERS.register("copy_components", CopyComponentsRecipeSerializer::create);
    public static final AttachmentType<PreferencesProperties> PREFERENCES_PROPERTIES = AttachmentRegistry.create(
            id("preferences_properties"), builder -> builder.initializer(PreferencesProperties::new).persistent(PreferencesProperties.CODEC));
    public static final AttachmentType<BaseBEData> BASE_BE_DATA = AttachmentRegistry.create(
            id("base_be_data"), builder -> builder.initializer(() -> new BaseBEData("", null, -1, RedstoneMode.REDSTONE_IGNORED)).persistent(BaseBEData.CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BaseBEData>> ITEM_BASE_BE_DATA = component("base_be_data", BaseBEData.CODEC, BaseBEData.STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemInfusable>> ITEM_INFUSABLE = component("infusable", ItemInfusable.ITEM_INFUSABLE_CODEC, ItemInfusable.ITEM_INFUSABLE_STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemEnergy>> ITEM_ENERGY = component("item_energy", ItemEnergy.ITEM_ENERGY_CODEC, ItemEnergy.ITEM_ENERGY_STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemInventory>> ITEM_INVENTORY = component("item_inventory", ItemInventory.ITEM_INVENTORY_CODEC, ItemInventory.ITEM_INVENTORY_STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemFluids>> ITEM_FLUIDS = component("item_fluids", ItemFluids.ITEM_FLUIDS_CODEC, ItemFluids.ITEM_FLUIDS_STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemSecurity>> ITEM_SECURITY = component("item_security", ItemSecurity.ITEM_SECURITY_CODEC, ItemSecurity.ITEM_SECURITY_STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemModule>> ITEM_MODULE = component("item_module", ItemModule.ITEM_MODULE_CODEC, ItemModule.ITEM_MODULE_STREAM_CODEC);

    // Other Fabric entrypoints can reference these components before our initializer runs.
    static { init(); }

    private static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(McJtyLib.MODID, name);
    }

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> component(
            String name, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return REGISTRAR.registerComponentType(name, builder -> builder.persistent(codec).networkSynchronized(streamCodec));
    }

    public static void init() {
        RECIPE_SERIALIZERS.register();
        REGISTRAR.register();
    }
}
