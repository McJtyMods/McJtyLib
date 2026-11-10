package mcjty.lib.datagen;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.BlockTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

public class BaseBlockTagsProvider extends BlockTagProvider {

    public BaseBlockTagsProvider(FabricDataOutput pGenerator, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId) {
        super(pGenerator, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

    }

    protected void ironPickaxe(Supplier... blocks) {
        for (Supplier b : blocks) {
            Block block = (Block) b.get();
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow());
            tag(BlockTags.NEEDS_IRON_TOOL).add(BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow());
        }
    }

    protected void diamondPickaxe(Supplier... blocks) {
        for (Supplier b : blocks) {
            Block block = (Block) b.get();
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow());
            tag(BlockTags.NEEDS_DIAMOND_TOOL).add(BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow());
        }
    }

    protected void stonePickaxe(Supplier... blocks) {
        for (Supplier b : blocks) {
            Block block = (Block) b.get();
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow());
            tag(BlockTags.NEEDS_STONE_TOOL).add(BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow());
        }
    }
}
