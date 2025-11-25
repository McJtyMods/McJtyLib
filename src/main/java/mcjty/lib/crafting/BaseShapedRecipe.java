package mcjty.lib.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

public class BaseShapedRecipe extends ShapedRecipe {

    public BaseShapedRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack stack) {
        super(group, category, pattern, stack);
    }

    @Override
    public ItemStack assemble(CraftingInput pInput, HolderLookup.Provider pRegistries) {
        return super.assemble(pInput, pRegistries);
    }
}
