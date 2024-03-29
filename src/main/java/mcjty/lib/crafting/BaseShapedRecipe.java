package mcjty.lib.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

public class BaseShapedRecipe extends ShapedRecipe {

    // @todo NEO
//    public BaseShapedRecipe(ResourceLocation id, String group, int width, int height, NonNullList<Ingredient> list, ItemStack stack) {
//        super(id, group, CraftingBookCategory.MISC, width, height, list, stack);
//    }


    public BaseShapedRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack stack, boolean showNotification) {
        super(group, category, pattern, stack, showNotification);
    }

    public BaseShapedRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack stack) {
        super(group, category, pattern, stack);
    }

    @Override
    public ItemStack assemble(CraftingContainer pContainer, RegistryAccess access) {
        return super.assemble(pContainer, access);
    }
}
