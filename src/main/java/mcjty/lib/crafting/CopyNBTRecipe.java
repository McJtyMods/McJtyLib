package mcjty.lib.crafting;

import mcjty.lib.setup.Registration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

import javax.annotation.Nonnull;

public class CopyNBTRecipe extends AbstractRecipeAdaptor {

    public CopyNBTRecipe(ShapedRecipe recipe) {
        super(recipe);
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;   // @todo 1.19.3. Is this right?
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull CraftingContainer inv) {
        ItemStack result = getRecipe().assemble(inv);
        CompoundTag nbt = null;
        for (int i = 0 ; i < inv.getContainerSize() ; i++) {
            ItemStack stack = inv.getItem(i);

            INBTPreservingIngredient inbt = null;
            if (stack.getItem() instanceof INBTPreservingIngredient ingredient) {
                inbt = ingredient;
            } else if (stack.getItem() instanceof BlockItem blockItem) {
                if (blockItem.getBlock() instanceof INBTPreservingIngredient ingredient) {
                    inbt = ingredient;
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
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return Registration.COPYNBT_SERIALIZER.get();
    }
}
