package mcjty.lib.datagen;

import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public record Dob(
        Supplier<? extends Block> blockSupplier,
        Consumer<ILootFactory> loot,
        BiConsumer<IBlockStateFactory, BaseBlockStateProvider> blockstate,
        Consumer<IItemFactory> item,
        Consumer<ITagFactory> blockTags,
        Consumer<IRecipeFactory> recipe) {

    public static Builder builder(Supplier<? extends Block> blockSupplier) {
        return new Builder(blockSupplier);
    }

    public static class Builder {
        private final Supplier<? extends Block> blockSupplier;
        private Consumer<ILootFactory> loot = f -> {};
        private BiConsumer<IBlockStateFactory, BaseBlockStateProvider> blockstate = (f,p) -> {};
        private Consumer<IItemFactory> item = f -> {};
        private Consumer<ITagFactory> blockTags = f -> {};
        private Consumer<IRecipeFactory> recipe = f -> {};

        public Builder(Supplier<? extends Block> blockSupplier) {
            this.blockSupplier = blockSupplier;
        }

        public Builder loot(Consumer<ILootFactory> loot) {
            this.loot = loot;
            return this;
        }

        public Builder simpleLoot() {
            this.loot = f -> f.simpleTable(blockSupplier);
            return this;
        }

        public Builder standardLoot(@SuppressWarnings("rawtypes") RegistryObject be) {
            this.loot = f -> f.standardTable(blockSupplier, (Supplier<? extends BlockEntityType<?>>) be);
            return this;
        }

        public Builder blockstate(BiConsumer<IBlockStateFactory, BaseBlockStateProvider> factory) {
            this.blockstate = factory;
            return this;
        }

        public Builder simpleBlockstate() {
            this.blockstate = (f, provider) -> f.simple(blockSupplier, provider);
            return this;
        }

        public Builder parentedItem(String model) {
            this.item = f -> f.parented(blockSupplier, model);
            return this;
        }

        public Builder parentedItem() {
            this.item = f -> f.parented(blockSupplier);
            return this;
        }

        public Builder blockTags(@SuppressWarnings("rawtypes") TagKey... tags) {
            this.blockTags = f -> f.blockTags(tags);
            return this;
        }

        public Builder shaped(ShapedRecipeBuilder builder, String... pattern) {
            this.recipe = f -> f.shaped(builder, pattern);
            return this;
        }

        public Dob build() {
            return new Dob(blockSupplier, loot, blockstate, item, blockTags, recipe);
        }
    }
}
