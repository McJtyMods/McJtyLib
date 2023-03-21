package mcjty.lib.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public interface BaseRecipe<C extends Container> extends Recipe<C> {

    static ItemStack assemble(Recipe recipe, CraftingContainer pContainer, RegistryAccess access) {
        return recipe.assemble(pContainer, access);
    }

    static ItemStack getResultItem(Recipe recipe, RegistryAccess access) {
        return recipe.getResultItem(access);
    }

    @Override
    ItemStack assemble(C container, RegistryAccess access);

    @Override
    ItemStack getResultItem(RegistryAccess access);
}
