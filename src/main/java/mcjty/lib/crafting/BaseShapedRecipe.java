package mcjty.lib.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class BaseShapedRecipe extends ShapedRecipe {

    public BaseShapedRecipe(ResourceLocation id, String group, int width, int height, NonNullList<Ingredient> list, ItemStack stack) {
        super(id, group, CraftingBookCategory.MISC, width, height, list, stack);
    }
}
