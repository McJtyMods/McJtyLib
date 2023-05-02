package mcjty.lib.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public interface BaseRecipe<C extends Container> extends Recipe<C> {

    static ItemStack assemble(Recipe recipe, CraftingContainer pContainer, Level level) {
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
    ItemStack assemble(C container, RegistryAccess access);

    @Override
    ItemStack getResultItem(RegistryAccess access);
}
