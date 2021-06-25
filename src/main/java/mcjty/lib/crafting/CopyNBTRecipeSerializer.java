package mcjty.lib.crafting;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class CopyNBTRecipeSerializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CopyNBTRecipe> {

    private final ShapedRecipe.Serializer serializer = new ShapedRecipe.Serializer();

    @Override
    public CopyNBTRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        ShapedRecipe recipe = serializer.fromJson(recipeId, json);
        return new CopyNBTRecipe(recipe);
    }

    @Override
    public CopyNBTRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
        ShapedRecipe recipe = serializer.fromNetwork(recipeId, buffer);
        return new CopyNBTRecipe(recipe);
    }

    @Override
    public void toNetwork(PacketBuffer buffer, CopyNBTRecipe recipe) {
        serializer.toNetwork(buffer, recipe.getRecipe());
    }
}
