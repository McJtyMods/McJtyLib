package mcjty.lib.setup;

import mcjty.lib.McJtyLib;
import mcjty.lib.api.container.ItemInventory;
import mcjty.lib.api.infusable.ItemInfusable;
import mcjty.lib.api.modules.ItemModule;
import mcjty.lib.api.power.ItemEnergy;
import mcjty.lib.api.security.ItemSecurity;
import mcjty.lib.crafting.CopyNBTRecipeSerializer;
import mcjty.lib.preferences.PreferencesProperties;
import mcjty.lib.tileentity.BaseBEData;
import mcjty.lib.varia.RedstoneMode;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class Registration {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, McJtyLib.MODID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, McJtyLib.MODID);
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(McJtyLib.MODID);

    public static void init(IEventBus bus) {
        RECIPE_SERIALIZERS.register(bus);
        ATTACHMENT_TYPES.register(bus);
        REGISTRAR.register(bus);
    }

    public static Supplier<CopyNBTRecipeSerializer> COPYNBT_SERIALIZER = RECIPE_SERIALIZERS.register("copy_nbt", CopyNBTRecipeSerializer::new);
    public static final Supplier<AttachmentType<PreferencesProperties>> PREFERENCES_PROPERTIES = ATTACHMENT_TYPES.register(
            "preferences_properties", () -> AttachmentType.serializable(PreferencesProperties::new).build());

    public static final Supplier<AttachmentType<BaseBEData>> BASE_BE_DATA = ATTACHMENT_TYPES.register(
            "base_be_data", () -> AttachmentType.builder(() -> new BaseBEData("", null, -1, RedstoneMode.REDSTONE_IGNORED))
                    .serialize(BaseBEData.CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BaseBEData>> ITEM_BASE_BE_DATA = REGISTRAR.registerComponentType(
            "base_be_data",
            builder -> builder
                    .persistent(BaseBEData.CODEC)
                    .networkSynchronized(BaseBEData.STREAM_CODEC));


    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemInfusable>> ITEM_INFUSABLE = REGISTRAR.registerComponentType(
            "infusable",
            builder -> builder
                    .persistent(ItemInfusable.ITEM_INFUSABLE_CODEC)
                    .networkSynchronized(ItemInfusable.ITEM_INFUSABLE_STREAM_CODEC)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemEnergy>> ITEM_ENERGY = REGISTRAR.registerComponentType(
            "item_energy",
            builder -> builder
                    .persistent(ItemEnergy.ITEM_ENERGY_CODEC)
                    .networkSynchronized(ItemEnergy.ITEM_ENERGY_STREAM_CODEC)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemInventory>> ITEM_INVENTORY = REGISTRAR.registerComponentType(
            "item_inventory",
            builder -> builder
                    .persistent(ItemInventory.ITEM_INVENTORY_CODEC)
                    .networkSynchronized(ItemInventory.ITEM_INVENTORY_STREAM_CODEC)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemSecurity>> ITEM_SECURITY = REGISTRAR.registerComponentType(
            "item_security",
            builder -> builder
                    .persistent(ItemSecurity.ITEM_SECURITY_CODEC)
                    .networkSynchronized(ItemSecurity.ITEM_SECURITY_STREAM_CODEC)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemModule>> ITEM_MODULE = REGISTRAR.registerComponentType(
            "item_module",
            builder -> builder
                    .persistent(ItemModule.ITEM_MODULE_CODEC)
                    .networkSynchronized(ItemModule.ITEM_MODULE_STREAM_CODEC)
    );
}
