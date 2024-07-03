package mcjty.lib.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;

public interface BaseRecipe<C extends RecipeInput> extends Recipe<C> {

    static ItemStack assemble(Recipe recipe, RecipeInput pContainer, Level level) {
        return recipe.assemble(pContainer, level.registryAccess());
    }

    static ItemStack getResultItem(Recipe recipe, Level level) {
        if (level == null) {
            return recipe.getResultItem(null);
        } else {
            return recipe.getResultItem(level.registryAccess());
        }
    }

    @Override
    ItemStack assemble(C c, HolderLookup.Provider provider);

    @Override
    ItemStack getResultItem(HolderLookup.Provider provider);
}
