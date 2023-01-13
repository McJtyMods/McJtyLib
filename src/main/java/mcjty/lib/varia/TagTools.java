package mcjty.lib.varia;

import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Collection;
import java.util.stream.Collectors;

public class TagTools {

    public static TagKey<Item> createItemTagKey(ResourceLocation rl) {
        return TagKey.create(Registry.ITEM.key(), rl);
    }

    public static TagKey<Block> createBlockTagKey(ResourceLocation rl) {
        return TagKey.create(Registry.BLOCK.key(), rl);
    }

    public static Iterable<Holder<Block>> getBlocksForTag(ResourceLocation rl) {
        DefaultedRegistry<Block> registry = Registry.BLOCK;
        return registry.getTagOrEmpty(TagKey.create(registry.key(), rl));
    }

    public static Iterable<Holder<Block>> getBlocksForTag(TagKey<Block> rl) {
        DefaultedRegistry<Block> registry = Registry.BLOCK;
        return registry.getTagOrEmpty(rl);
    }

    public static Iterable<Holder<Item>> getItemsForTag(ResourceLocation rl) {
        DefaultedRegistry<Item> registry = Registry.ITEM;
        return registry.getTagOrEmpty(TagKey.create(registry.key(), rl));
    }

    public static Iterable<Holder<Item>> getItemsForTag(TagKey<Item> rl) {
        DefaultedRegistry<Item> registry = Registry.ITEM;
        return registry.getTagOrEmpty(rl);
    }

    public static boolean hasTag(Block block, TagKey<Block> tag) {
        return Registry.BLOCK.getHolderOrThrow(block.builtInRegistryHolder().key()).is(tag);
    }

    public static boolean hasTag(Item item, TagKey<Item> tag) {
        return Registry.ITEM.getHolderOrThrow(item.builtInRegistryHolder().key()).is(tag);
    }

    public static Collection<TagKey<Item>> getTags(Item item) {
        return item.builtInRegistryHolder().tags().collect(Collectors.toSet());
    }

    public static Collection<TagKey<Block>> getTags(Block block) {
        return block.builtInRegistryHolder().tags().collect(Collectors.toSet());
    }
}
