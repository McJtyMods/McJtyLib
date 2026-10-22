package mcjty.lib.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;

public interface BaseRecipe<C extends RecipeInput> extends Recipe<C> {
    static ItemStack assemble(Recipe recipe, RecipeInput input, Level level) {
        return recipe.assemble(input);
    }
}
