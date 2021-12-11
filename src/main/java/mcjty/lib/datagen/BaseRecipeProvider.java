package mcjty.lib.datagen;

import mcjty.lib.crafting.IRecipeBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.Tags;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;

public class BaseRecipeProvider extends RecipeProvider {

    private final Map<Character, Ingredient> defaultIngredients = new HashMap<>();
    private String group = "";

    protected BaseRecipeProvider group(String group) {
        this.group = group;
        return this;
    }

    protected BaseRecipeProvider add(char c, Tag<Item> itemTag) {
        defaultIngredients.put(c, Ingredient.of(itemTag));
        return this;
    }

    protected BaseRecipeProvider add(char c, ItemLike itemProvider) {
        defaultIngredients.put(c, Ingredient.of(itemProvider));
        return this;
    }

    protected BaseRecipeProvider add(char c, Ingredient ingredient) {
        defaultIngredients.put(c, ingredient);
        return this;
    }

    private void buildIntern(Consumer<FinishedRecipe> consumer,
                             Consumer<String> lineConsumer, BiConsumer<Character, Ingredient> ingredientConsumer, String... lines) {
        Set<Character> characters = new HashSet<>();
        for (String line : lines) {
            lineConsumer.accept(line);
            line.chars().forEach(value -> characters.add((char)value));
        }

        for (Character c :characters){
            if (defaultIngredients.containsKey(c)) {
                ingredientConsumer.accept(c, defaultIngredients.get(c));
            }
        }
    }

    protected void build(Consumer<FinishedRecipe> consumer, IRecipeBuilder<?> builder, String... lines) {
        buildIntern(consumer, builder::patternLine, builder::define, lines);
        builder.setGroup(group).build(consumer);
    }

    protected void build(Consumer<FinishedRecipe> consumer, ShapedRecipeBuilder builder, String... lines) {
        buildIntern(consumer, builder::pattern, builder::define, lines);
        builder.group(group).save(consumer);
    }

    protected void build(Consumer<FinishedRecipe> consumer, ShapelessRecipeBuilder builder) {
        builder.group(group).save(consumer);
    }

    protected void build(Consumer<FinishedRecipe> consumer, ResourceLocation id, ShapelessRecipeBuilder builder) {
        builder.group(group).save(consumer, id);
    }

    protected void build(Consumer<FinishedRecipe> consumer, ResourceLocation id, IRecipeBuilder<?> builder, String... lines) {
        buildIntern(consumer, builder::patternLine, builder::define, lines);
        builder.setGroup(group).build(consumer, id);
    }

    protected void build(Consumer<FinishedRecipe> consumer, ResourceLocation id, ShapedRecipeBuilder builder, String... lines) {
        buildIntern(consumer, builder::pattern, builder::define, lines);
        builder.group(group).save(consumer, id);
    }

    public BaseRecipeProvider(DataGenerator datagen) {
        super(datagen);
        add('d', Items.DIAMOND);
        add('e', Items.EMERALD);
        add('o', Tags.Items.ENDER_PEARLS);
        add('r', Items.REDSTONE);
        add('R', Items.REDSTONE_BLOCK);
        add('i', Tags.Items.INGOTS_IRON);
        add('p', Items.PAPER);
        add('c', ItemTags.COALS);
        add('B', Blocks.BRICKS);
        add('W', Items.WATER_BUCKET);
        add('L', Items.LAVA_BUCKET);
        add('b', Items.BUCKET);
        add('T', Items.REDSTONE_TORCH);
        add('D', Blocks.DIRT);
        add('G', Tags.Items.GLASS);
        add('O', Blocks.OBSIDIAN);
    }
}
