package mcjty.lib.datagen;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Supplier;

public interface ITagFactory {

    void blockTags(Supplier<? extends Block> blockSupplier, List<TagKey> tags);

    void itemTags(Supplier<? extends Item> itemSupplier, List<TagKey> tags);
}
