package mcjty.lib.datagen;

import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;
import java.util.function.Supplier;

public record Dob(
        Supplier<? extends Block> blockSupplier,
        Supplier<? extends Item> itemSupplier,
        Consumer<ILootFactory> loot,
        Consumer<BaseBlockStateProvider> blockstate,
        Consumer<IItemFactory> item,
        Consumer<ITagFactory> blockTags,
        Consumer<ITagFactory> itemTags,
        Consumer<IRecipeFactory> recipe) {

    public static Builder builder(Supplier<? extends Block> blockSupplier, Supplier<? extends Item> itemSupplier) {
        return new Builder(blockSupplier, itemSupplier);
    }

    public static Builder blockBuilder(Supplier<? extends Block> blockSupplier) {
        return new Builder(blockSupplier, null);
    }

    public static Builder itemBuilder(Supplier<? extends Item> itemSupplier) {
        return new Builder(null, itemSupplier);
    }

    public static class Builder {
        private final Supplier<? extends Block> blockSupplier;
        private final Supplier<? extends Item> itemSupplier;
        private Consumer<ILootFactory> loot = f -> {};
        private Consumer<BaseBlockStateProvider> blockstate = p -> {};
        private Consumer<IItemFactory> item = f -> {};
        private Consumer<ITagFactory> blockTags = f -> {};
        private Consumer<ITagFactory> itemTags = f -> {};
        private Consumer<IRecipeFactory> recipe = f -> {};

        public Builder(Supplier<? extends Block> blockSupplier, Supplier<? extends Item> itemSupplier) {
            this.blockSupplier = blockSupplier;
            this.itemSupplier = itemSupplier;
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

        public Builder blockstate(Consumer<BaseBlockStateProvider> factory) {
            this.blockstate = factory;
            return this;
        }

        public Builder simpleBlockstate() {
            this.blockstate = provider -> provider.simpleBlock(blockSupplier.get());
            return this;
        }

        public Builder generatedItem(String texture) {
            this.item = f -> f.generated(itemSupplier, texture);
            return this;
        }

        public Builder cubeAll(ResourceLocation texture) {
            this.item = f -> f.cubeAll(itemSupplier, texture);
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
            this.blockTags = f -> f.blockTags(blockSupplier, tags);
            return this;
        }

        public Builder itemTags(@SuppressWarnings("rawtypes") TagKey... tags) {
            this.blockTags = f -> f.itemTags(itemSupplier, tags);
            return this;
        }

        public Builder shaped(ShapedRecipeBuilder builder, String... pattern) {
            this.recipe = f -> f.shaped(builder, pattern);
            return this;
        }

        public Dob build() {
            return new Dob(blockSupplier, itemSupplier, loot, blockstate, item, blockTags, itemTags, recipe);
        }
    }
}
