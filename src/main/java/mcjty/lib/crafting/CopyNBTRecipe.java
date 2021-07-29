package mcjty.lib.crafting;

import mcjty.lib.setup.Registration;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class CopyNBTRecipe extends AbstractRecipeAdaptor {

    public CopyNBTRecipe(ShapedRecipe recipe) {
        super(recipe);
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {
        ItemStack result = recipe.assemble(inv);
        CompoundTag nbt = null;
        for (int i = 0 ; i < inv.getContainerSize() ; i++) {
            ItemStack stack = inv.getItem(i);

            INBTPreservingIngredient inbt = null;
            if (stack.getItem() instanceof INBTPreservingIngredient) {
                inbt = (INBTPreservingIngredient) stack.getItem();
            } else if (stack.getItem() instanceof BlockItem) {
                if (((BlockItem)stack.getItem()).getBlock() instanceof INBTPreservingIngredient) {
                    inbt = (INBTPreservingIngredient) ((BlockItem)stack.getItem()).getBlock();
                }
            }

            if (inbt != null && stack.getTag() != null) {
                nbt = new CompoundTag();
                for (String tag : inbt.getTagsToPreserve()) {
                    Tag value = stack.getTag().get(tag);
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
    public RecipeSerializer<?> getSerializer() {
        return Registration.COPYNBT_SERIALIZER;
    }
}
