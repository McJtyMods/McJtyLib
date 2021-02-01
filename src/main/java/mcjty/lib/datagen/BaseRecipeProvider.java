package mcjty.lib.datagen;

import mcjty.lib.crafting.IRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BaseRecipeProvider extends RecipeProvider {

    private final Map<Character, Ingredient> defaultIngredients = new HashMap<>();
    private String group = "";

    protected BaseRecipeProvider group(String group) {
        this.group = group;
        return this;
    }

    protected BaseRecipeProvider add(char c, ITag<Item> itemTag) {
        defaultIngredients.put(c, Ingredient.fromTag(itemTag));
        return this;
    }

    protected BaseRecipeProvider add(char c, IItemProvider itemProvider) {
        defaultIngredients.put(c, Ingredient.fromItems(itemProvider));
        return this;
    }

    protected BaseRecipeProvider add(char c, Ingredient ingredient) {
        defaultIngredients.put(c, ingredient);
        return this;
    }

    private void buildIntern(Consumer<IFinishedRecipe> consumer,
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

    protected void build(Consumer<IFinishedRecipe> consumer, IRecipeBuilder builder, String... lines) {
        buildIntern(consumer, builder::patternLine, builder::key, lines);
        builder.setGroup(group).build(consumer);
    }

    protected void build(Consumer<IFinishedRecipe> consumer, ShapedRecipeBuilder builder, String... lines) {
        buildIntern(consumer, builder::patternLine, builder::key, lines);
        builder.setGroup(group).build(consumer);
    }

    protected void build(Consumer<IFinishedRecipe> consumer, ShapelessRecipeBuilder builder) {
        builder.setGroup(group).build(consumer);
    }

    protected void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id, ShapelessRecipeBuilder builder) {
        builder.setGroup(group).build(consumer, id);
    }

    protected void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id, IRecipeBuilder builder, String... lines) {
        buildIntern(consumer, builder::patternLine, builder::key, lines);
        builder.setGroup(group).build(consumer, id);
    }

    protected void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id, ShapedRecipeBuilder builder, String... lines) {
        buildIntern(consumer, builder::patternLine, builder::key, lines);
        builder.setGroup(group).build(consumer, id);
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
