package mcjty.lib.crafting;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public interface IRecipeBuilder<T extends IRecipeBuilder<T>> {
    T define(Character symbol, TagKey<Item> tagIn);

    T define(Character symbol, ItemLike itemIn);

    T define(Character symbol, Ingredient ingredientIn);

    T patternLine(String patternIn);

    T setGroup(String groupIn);

    void build(RecipeOutput consumerIn);

    void build(RecipeOutput consumerIn, String save);

    void build(RecipeOutput consumerIn, ResourceLocation id);
}
