package mcjty.lib.crafting;

import mcjty.lib.setup.Registration;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;

public class CopyNBTRecipe extends AbstractRecipeAdaptor {

    public CopyNBTRecipe(ShapedRecipe recipe) {
        super(recipe);
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
    public IRecipeSerializer<?> getSerializer() {
        return Registration.COPYNBT_SERIALIZER;
    }
}
