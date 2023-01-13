package mcjty.lib.varia;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Collection;
import java.util.stream.Collectors;

public class TagTools {

    public static TagKey<Item> createItemTagKey(ResourceLocation rl) {
        return TagKey.create(Registries.ITEM, rl);
    }

    public static TagKey<Block> createBlockTagKey(ResourceLocation rl) {
        return TagKey.create(Registries.BLOCK, rl);
    }

    public static Iterable<Holder<Block>> getBlocksForTag(ResourceLocation rl) {
        return BuiltInRegistries.BLOCK.getTagOrEmpty(TagKey.create(Registries.BLOCK, rl));
    }

    public static Iterable<Holder<Block>> getBlocksForTag(TagKey<Block> rl) {
        return BuiltInRegistries.BLOCK.getTagOrEmpty(rl);
    }

    public static Iterable<Holder<Item>> getItemsForTag(ResourceLocation rl) {
        return BuiltInRegistries.ITEM.getTagOrEmpty(TagKey.create(Registries.ITEM, rl));
    }

    public static Iterable<Holder<Item>> getItemsForTag(TagKey<Item> rl) {
        return BuiltInRegistries.ITEM.getTagOrEmpty(rl);
    }

    public static boolean hasTag(Block block, TagKey<Block> tag) {
        return BuiltInRegistries.BLOCK.getHolderOrThrow(block.builtInRegistryHolder().key()).is(tag);
    }

    public static boolean hasTag(Item item, TagKey<Item> tag) {
        return BuiltInRegistries.ITEM.getHolderOrThrow(item.builtInRegistryHolder().key()).is(tag);
    }

    public static Collection<TagKey<Item>> getTags(Item item) {
        return item.builtInRegistryHolder().tags().collect(Collectors.toSet());
    }

    public static Collection<TagKey<Block>> getTags(Block block) {
        return block.builtInRegistryHolder().tags().collect(Collectors.toSet());
    }
}
