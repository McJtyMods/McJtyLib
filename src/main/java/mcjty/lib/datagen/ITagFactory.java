package mcjty.lib.datagen;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public interface ITagFactory {

    void blockTags(TagKey<Block>... tags);
}
