package mcjty.lib.datagen;

import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;

public interface IRecipeFactory {

    void shaped(ShapedRecipeBuilder builder, String... pattern);

    void shaped(String id, ShapedRecipeBuilder builder, String... pattern);

    void shapeless(ShapelessRecipeBuilder builder);

    void shapeless(String id, ShapelessRecipeBuilder builder);
}
