package mcjty.lib.datagen;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public interface ITagFactory {

    void blockTags(Supplier<? extends Block> blockSupplier, TagKey<Block>... tags);

    void itemTags(Supplier<? extends Item> itemSupplier, TagKey<Item>... tags);
}
