package mcjty.lib.crafting;

import mcjty.lib.setup.Registration;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class CopyNBTRecipe implements ICraftingRecipe, net.minecraftforge.common.crafting.IShapedRecipe<CraftingInventory> {

    private final ShapedRecipe recipe;

    public CopyNBTRecipe(ShapedRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public boolean canFit(int width, int height) {
        return recipe.canFit(width, height);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        return recipe.getRemainingItems(inv);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return recipe.getIngredients();
    }

    @Override
    public boolean isDynamic() {
        return recipe.isDynamic();
    }

    @Override
    public String getGroup() {
        return recipe.getGroup();
    }

    @Override
    public ItemStack getIcon() {
        return recipe.getIcon();
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
    public boolean matches(CraftingInventory inv, World worldIn) {
        return recipe.matches(inv, worldIn);
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack result = recipe.getCraftingResult(inv);
        CompoundNBT nbt = null;
        for (int i = 0 ; i < inv.getSizeInventory() ; i++) {
            ItemStack stack = inv.getStackInSlot(i);

            INBTPreservingIngredient inbt = null;
            if (stack.getItem() instanceof INBTPreservingIngredient) {
                inbt = (INBTPreservingIngredient) stack.getItem();
            } else if (stack.getItem() instanceof BlockItem) {
                if (((BlockItem)stack.getItem()).getBlock() instanceof INBTPreservingIngredient) {
                    inbt = (INBTPreservingIngredient) ((BlockItem)stack.getItem()).getBlock();
                }
            }

            if (inbt != null && stack.getTag() != null) {
                nbt = new CompoundNBT();
                for (String tag : inbt.getTagsToPreserve()) {
                    INBT value = stack.getTag().get(tag);
                    if (value != null) {
                        nbt.put(tag, value);
                    }
                }
            }
        }
        if (nbt != null) {
            result.setTag(nbt);
        }
        return result;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return recipe.getRecipeOutput();
    }

    @Override
    public ResourceLocation getId() {
        return recipe.getId();
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Registration.COPYNBT_SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return recipe.getType();
    }
}
