package mcjty.lib.crafting;

import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.JsonUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CopyNBTRecipeFactory implements IRecipeSerializer {

    // @todo 1.14
    @Override
    public IRecipe<?> read(ResourceLocation recipeId, JsonObject json) {
        return null;
    }

    @Override
    public IRecipe<?> read(ResourceLocation recipeId, PacketBuffer buffer) {
        return null;
    }

    @Override
    public void write(PacketBuffer buffer, IRecipe recipe) {

    }

    @Override
    public Object setRegistryName(ResourceLocation name) {
        return null;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return null;
    }

    @Override
    public Class getRegistryType() {
        return null;
    }

    // @todo 1.14
    /*
    @Override
    public IRecipe parse(JsonContext context, JsonObject json) {
        ShapedRecipe recipe = ShapedRecipe.factory(context, json);

        ShapedPrimer primer = new ShapedPrimer();
        primer.width = recipe.getWidth();
        primer.height = recipe.getHeight();
        primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
        primer.input = recipe.getIngredients();

        return new CopyNBTRecipe(new ResourceLocation("mcjtylib", "copy_nbt_crafting"), recipe.getRecipeOutput(), primer);
    }

    public static class CopyNBTRecipe extends ShapedOreRecipe {
        public CopyNBTRecipe(ResourceLocation group, ItemStack result, ShapedPrimer primer) {
            super(group, result, primer);
        }

        @Override
        @Nonnull
        public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1) {
            ItemStack newOutput = this.output.copy();

            ItemStack itemstack = ItemStack.EMPTY;

            for (int i = 0; i < var1.getSizeInventory(); ++i) {
                ItemStack stack = var1.getStackInSlot(i);

                if (!stack.isEmpty()) {
                    if (stack.getItem() instanceof INBTPreservingIngredient) {
                        itemstack = stack;
                    } else if (Block.getBlockFromItem(stack.getItem()) instanceof INBTPreservingIngredient) {
                        itemstack = stack;
                    }
                }
            }

            if (itemstack.hasTagCompound()) {
                newOutput.setTagCompound(itemstack.getTagCompound().copy());
            }

            return newOutput;
        }
    }
    */
}