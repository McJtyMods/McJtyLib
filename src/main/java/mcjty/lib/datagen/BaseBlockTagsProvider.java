package mcjty.lib.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class BaseBlockTagsProvider extends BlockTagsProvider  {

    public BaseBlockTagsProvider(DataGenerator pGenerator, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, modId, existingFileHelper);
    }

    protected void ironPickaxe(RegistryObject... blocks) {
        for (RegistryObject b : blocks) {
            Block block = (Block) b.get();
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
            tag(BlockTags.NEEDS_IRON_TOOL).add(block);
        }
    }

    protected void diamondPickaxe(RegistryObject... blocks) {
        for (RegistryObject b : blocks) {
            Block block = (Block) b.get();
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
            tag(BlockTags.NEEDS_DIAMOND_TOOL).add(block);
        }
    }

    protected void stonePickaxe(RegistryObject... blocks) {
        for (RegistryObject b : blocks) {
            Block block = (Block) b.get();
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
            tag(BlockTags.NEEDS_STONE_TOOL).add(block);
        }
    }
}
