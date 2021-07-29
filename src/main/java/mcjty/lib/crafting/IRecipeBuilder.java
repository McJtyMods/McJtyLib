package mcjty.lib.crafting;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public interface IRecipeBuilder<T extends IRecipeBuilder<T>> {
    T define(Character symbol, Tag<Item> tagIn);

    T define(Character symbol, ItemLike itemIn);

    T define(Character symbol, Ingredient ingredientIn);

    T patternLine(String patternIn);

    T setGroup(String groupIn);

    void build(Consumer<FinishedRecipe> consumerIn);

    void build(Consumer<FinishedRecipe> consumerIn, String save);

    void build(Consumer<FinishedRecipe> consumerIn, ResourceLocation id);
}
