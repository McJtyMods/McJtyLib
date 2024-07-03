package mcjty.lib.setup;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import mcjty.lib.McJtyLib;
import mcjty.lib.api.infusable.ItemInfusable;
import mcjty.lib.api.power.ItemEnergy;
import mcjty.lib.api.security.ItemSecurity;
import mcjty.lib.crafting.CopyNBTRecipeSerializer;
import mcjty.lib.preferences.PreferencesProperties;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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
    }

    public static Supplier<CopyNBTRecipeSerializer> COPYNBT_SERIALIZER = RECIPE_SERIALIZERS.register("copy_nbt", CopyNBTRecipeSerializer::new);
    public static final Supplier<AttachmentType<PreferencesProperties>> PREFERENCES_PROPERTIES = ATTACHMENT_TYPES.register(
            "preferences_properties", () -> AttachmentType.serializable(PreferencesProperties::new).build());

    public static final Codec<ItemInfusable> ITEM_INFUSABLE_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("infused").forGetter(ItemInfusable::infused)
            ).apply(instance, ItemInfusable::new));

    public static final StreamCodec<ByteBuf, ItemInfusable> ITEM_INFUSABLE_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ItemInfusable::infused,
            ItemInfusable::new);

    public static final Codec<ItemEnergy> ITEM_ENERGY_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("energy").forGetter(ItemEnergy::energy)
            ).apply(instance, ItemEnergy::new));

    public static final StreamCodec<ByteBuf, ItemEnergy> ITEM_ENERGY_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ItemEnergy::energy,
            ItemEnergy::new);

    public static final Codec<ItemSecurity> ITEM_SECURITY_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("owner").forGetter(ItemSecurity::owner),
                    Codec.INT.fieldOf("channel").forGetter(ItemSecurity::channel)
            ).apply(instance, ItemSecurity::new));

    public static final StreamCodec<ByteBuf, ItemSecurity> ITEM_SECURITY_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ItemSecurity::owner,
            ByteBufCodecs.INT, ItemSecurity::channel,
            ItemSecurity::new);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemInfusable>> ITEM_INFUSABLE = REGISTRAR.registerComponentType(
            "infusable",
            builder -> builder
                    .persistent(ITEM_INFUSABLE_CODEC)
                    .networkSynchronized(ITEM_INFUSABLE_STREAM_CODEC)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemEnergy>> ITEM_ENERGY = REGISTRAR.registerComponentType(
            "item_energy",
            builder -> builder
                    .persistent(ITEM_ENERGY_CODEC)
                    .networkSynchronized(ITEM_ENERGY_STREAM_CODEC)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemSecurity>> ITEM_SECURITY = REGISTRAR.registerComponentType(
            "item_security",
            builder -> builder
                    .persistent(ITEM_SECURITY_CODEC)
                    .networkSynchronized(ITEM_SECURITY_STREAM_CODEC)
    );
}
