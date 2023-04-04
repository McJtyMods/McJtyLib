package mcjty.lib.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.checkerframework.checker.units.qual.C;

public interface BaseRecipe<C extends Container> extends Recipe<C> {

    static ItemStack assemble(Recipe recipe, CraftingContainer pContainer, Level level) {
        return recipe.assemble(pContainer);
    }

    static ItemStack getResultItem(Recipe recipe, Level level) {
        return recipe.getResultItem();
    }

    @Override
    default ItemStack assemble(C pContainer) {
        return assemble(pContainer, null);
    }

    @Override
    default ItemStack getResultItem() {
        return getResultItem(null);
    }

    ItemStack assemble(C container, RegistryAccess access);

    ItemStack getResultItem(RegistryAccess access);
}
