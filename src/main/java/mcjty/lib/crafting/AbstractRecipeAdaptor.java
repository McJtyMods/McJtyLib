package mcjty.lib.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public abstract class AbstractRecipeAdaptor implements CraftingRecipe, net.minecraftforge.common.crafting.IShapedRecipe<CraftingContainer> {

    private final ShapedRecipe recipe;

    public AbstractRecipeAdaptor(ShapedRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return recipe.canCraftInDimensions(width, height);
    }

    @Override
    @Nonnull
    public NonNullList<ItemStack> getRemainingItems(@Nonnull CraftingContainer inv) {
        return recipe.getRemainingItems(inv);
    }

    @Override
    @Nonnull
    public NonNullList<Ingredient> getIngredients() {
        return recipe.getIngredients();
    }

    @Override
    public boolean isSpecial() {
        return recipe.isSpecial();
    }

    @Override
    @Nonnull
    public String getGroup() {
        return recipe.getGroup();
    }

    @Override
    @Nonnull
    public ItemStack getToastSymbol() {
        return recipe.getToastSymbol();
    }

    public ShapedRecipe getRecipe() {
        return recipe;
    }

    @Override
    public int getRecipeWidth() {
        return recipe.getRecipeWidth();
    }

    @Override
    public int getRecipeHeight() {
        return recipe.getRecipeHeight();
    }

    @Override
    public boolean matches(@Nonnull CraftingContainer inv, @Nonnull Level worldIn) {
        return recipe.matches(inv, worldIn);
    }

    @Override
    @Nonnull
    public ResourceLocation getId() {
        return recipe.getId();
    }

    @Override
    @Nonnull
    public RecipeType<?> getType() {
        return recipe.getType();
    }
}
