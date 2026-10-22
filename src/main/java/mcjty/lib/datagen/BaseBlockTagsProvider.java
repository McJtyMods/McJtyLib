package mcjty.lib.datagen;

import net.minecraft.core.HolderLookup;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider.BlockTagsProvider;
import org.jspecify.annotations.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class BaseBlockTagsProvider extends BlockTagsProvider {

    public BaseBlockTagsProvider(FabricPackOutput pGenerator, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId) {
        super(pGenerator, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

    }

    protected void ironPickaxe(Supplier... blocks) {
        for (Supplier b : blocks) {
            Block block = (Block) b.get();
            builder(BlockTags.MINEABLE_WITH_PICKAXE).add(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow());
            builder(BlockTags.NEEDS_IRON_TOOL).add(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow());
        }
    }

    protected void diamondPickaxe(Supplier... blocks) {
        for (Supplier b : blocks) {
            Block block = (Block) b.get();
            builder(BlockTags.MINEABLE_WITH_PICKAXE).add(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow());
            builder(BlockTags.NEEDS_DIAMOND_TOOL).add(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow());
        }
    }

    protected void stonePickaxe(Supplier... blocks) {
        for (Supplier b : blocks) {
            Block block = (Block) b.get();
            builder(BlockTags.MINEABLE_WITH_PICKAXE).add(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow());
            builder(BlockTags.NEEDS_STONE_TOOL).add(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow());
        }
    }
}
