package mcjty.lib.api.container;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.List;

// Used as a data component for items that can store items
public record ItemInventory(List<ItemStack> items) {
    public static final Codec<ItemInventory> ITEM_INVENTORY_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.list(ItemStack.OPTIONAL_CODEC).fieldOf("items").forGetter(ItemInventory::items)
            ).apply(instance, ItemInventory::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemInventory> ITEM_INVENTORY_STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_LIST_STREAM_CODEC, ItemInventory::items,
            ItemInventory::new);

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ItemInventory that)) return false;
        return ItemStack.listMatches(items, that.items);
    }

    @Override
    public int hashCode() {
        return ItemStack.hashStackList(items);
    }
}
