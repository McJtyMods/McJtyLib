package mcjty.lib.datagen;

import mcjty.lib.crafting.CopyNBTRecipeBuilder;
import mcjty.lib.crafting.IRecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IRecipeFactory {

    void recipe(Supplier<IRecipeBuilder> supplier);

    void recipeConsumer(Supplier<Consumer<RecipeOutput>> consumerSupplier);

    void recipe(String id, Supplier<IRecipeBuilder> supplier);

    void shapedNBT(CopyNBTRecipeBuilder builder, String... pattern);

    void shapedNBT(String id, CopyNBTRecipeBuilder builder, String... pattern);

    void shaped(ShapedRecipeBuilder builder, String... pattern);

    void shaped(String id, ShapedRecipeBuilder builder, String... pattern);

    void shapeless(ShapelessRecipeBuilder builder);

    void shapeless(String id, ShapelessRecipeBuilder builder);
}
