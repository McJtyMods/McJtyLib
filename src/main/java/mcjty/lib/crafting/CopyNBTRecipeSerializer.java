package mcjty.lib.crafting;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

import javax.annotation.Nonnull;

public class CopyNBTRecipeSerializer implements RecipeSerializer<CopyNBTRecipe> {

    private final ShapedRecipe.Serializer serializer = new ShapedRecipe.Serializer();

    // @todo NEO
    @Override
    public Codec<CopyNBTRecipe> codec() {
        return null;
    }


    // @todo NEO
//    @Override
//    @Nonnull
//    public CopyNBTRecipe fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
//        ShapedRecipe recipe = serializer.fromJson(recipeId, json);
//        return new CopyNBTRecipe(recipe);
//    }

    @Override
    public CopyNBTRecipe fromNetwork(@Nonnull FriendlyByteBuf buffer) {
        ShapedRecipe recipe = serializer.fromNetwork(buffer);
        return new CopyNBTRecipe(recipe);
    }

    @Override
    public void toNetwork(@Nonnull FriendlyByteBuf buffer, CopyNBTRecipe recipe) {
        serializer.toNetwork(buffer, recipe.getRecipe());
    }
}
