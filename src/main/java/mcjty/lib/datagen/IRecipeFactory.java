package mcjty.lib.datagen;

import net.minecraft.data.recipes.ShapedRecipeBuilder;

public interface IRecipeFactory {

    void shaped(ShapedRecipeBuilder builder, String... pattern);
}
