package mcjty.lib.crafting;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public final class CopyComponentsRecipeSerializer {
    private CopyComponentsRecipeSerializer() { }
    public static RecipeSerializer<CopyComponentsRecipe> create() {
        return new RecipeSerializer<>(
            ShapedRecipe.MAP_CODEC.codec().fieldOf("recipe").xmap(CopyComponentsRecipe::new, CopyComponentsRecipe::getRecipe),
            ShapedRecipe.STREAM_CODEC.map(CopyComponentsRecipe::new, CopyComponentsRecipe::getRecipe));
    }
}
