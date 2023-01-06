package mcjty.lib.datagen;

import mcjty.lib.crafting.CopyNBTRecipeBuilder;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;

public interface IRecipeFactory {

    void shapedNBT(CopyNBTRecipeBuilder builder, String... pattern);

    void shapedNBT(String id, CopyNBTRecipeBuilder builder, String... pattern);

    void shaped(ShapedRecipeBuilder builder, String... pattern);

    void shaped(String id, ShapedRecipeBuilder builder, String... pattern);

    void shapeless(ShapelessRecipeBuilder builder);

    void shapeless(String id, ShapelessRecipeBuilder builder);
}
