package mcjty.lib.crafting;

import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public interface IRecipeBuilder<T extends IRecipeBuilder<T>> {
    T key(Character symbol, ITag<Item> tagIn);

    T key(Character symbol, IItemProvider itemIn);

    T key(Character symbol, Ingredient ingredientIn);

    T patternLine(String patternIn);

    T setGroup(String groupIn);

    void build(Consumer<IFinishedRecipe> consumerIn);

    void build(Consumer<IFinishedRecipe> consumerIn, String save);

    void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id);
}
