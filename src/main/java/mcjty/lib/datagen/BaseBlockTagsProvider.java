package mcjty.lib.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BaseBlockTagsProvider extends BlockTagsProvider {

    public BaseBlockTagsProvider(DataGenerator pGenerator, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator.getPackOutput(), lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

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
