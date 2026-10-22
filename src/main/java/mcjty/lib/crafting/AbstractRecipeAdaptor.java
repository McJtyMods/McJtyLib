package mcjty.lib.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;
import java.util.List;

public abstract class AbstractRecipeAdaptor implements CraftingRecipe {
    private final ShapedRecipe recipe;
    public AbstractRecipeAdaptor(ShapedRecipe recipe) { this.recipe = recipe; }
    public ShapedRecipe getRecipe() { return recipe; }
    @Override public boolean matches(CraftingInput input, Level level) { return recipe.matches(input, level); }
    @Override public CraftingBookCategory category() { return recipe.category(); }
    @Override public NonNullList<ItemStack> getRemainingItems(CraftingInput input) { return recipe.getRemainingItems(input); }
    @Override public boolean isSpecial() { return recipe.isSpecial(); }
    @Override public String group() { return recipe.group(); }
    @Override public boolean showNotification() { return recipe.showNotification(); }
    @Override public PlacementInfo placementInfo() { return recipe.placementInfo(); }
    @Override public List<RecipeDisplay> display() { return recipe.display(); }
}
