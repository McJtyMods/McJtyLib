package mcjty.lib.crafting;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class CopyNBTRecipeSerializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CopyNBTRecipe> {

    private final ShapedRecipe.Serializer serializer = new ShapedRecipe.Serializer();

    @Override
    public CopyNBTRecipe read(ResourceLocation recipeId, JsonObject json) {
        ShapedRecipe recipe = serializer.read(recipeId, json);
        return new CopyNBTRecipe(recipe);
    }

    @Override
    public CopyNBTRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        ShapedRecipe recipe = serializer.read(recipeId, buffer);
        return new CopyNBTRecipe(recipe);
    }

    @Override
    public void write(PacketBuffer buffer, CopyNBTRecipe recipe) {
        serializer.write(buffer, recipe.getRecipe());
    }
}
