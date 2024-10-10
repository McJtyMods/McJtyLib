package mcjty.lib.crafting;

import mcjty.lib.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

import javax.annotation.Nonnull;

public class CopyComponentsRecipe extends AbstractRecipeAdaptor {

    public CopyComponentsRecipe(ShapedRecipe recipe) {
        super(recipe);
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;   // @todo 1.19.3. Is this right?
    }

    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider access) {
        ItemStack result = getRecipe().assemble(inv, access);
        CompoundTag nbt = null;
        for (int i = 0 ; i < inv.size() ; i++) {
            ItemStack stack = inv.getItem(i);

            IComponentsToPreserve inbt = null;
            if (stack.getItem() instanceof IComponentsToPreserve ingredient) {
                inbt = ingredient;
            } else if (stack.getItem() instanceof BlockItem blockItem) {
                if (blockItem.getBlock() instanceof IComponentsToPreserve ingredient) {
                    inbt = ingredient;
                }
            }

            if (inbt != null) {
                for (DataComponentType type : inbt.getComponentsToPreserve()) {
                    Object o = stack.get(type);
                    if (o != null) {
                        result.set(type, o);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return getRecipe().getResultItem(provider);
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return Registration.COPYNBT_SERIALIZER.get();
    }
}
