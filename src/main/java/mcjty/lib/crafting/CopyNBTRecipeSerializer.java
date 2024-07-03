package mcjty.lib.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class CopyNBTRecipeSerializer implements RecipeSerializer<CopyNBTRecipe> {

    private final ShapedRecipe.Serializer serializer = new ShapedRecipe.Serializer();

    private final static StreamCodec<RegistryFriendlyByteBuf, CopyNBTRecipe> STREAM_CODEC = StreamCodec.of(
            (buf, copyNBTRecipe) -> {
                ShapedRecipe recipe = copyNBTRecipe.getRecipe();
                ShapedRecipe.STREAM_CODEC.encode(buf, recipe);
            },
            buf -> {
                Recipe<?> recipe = ShapedRecipe.STREAM_CODEC.decode(buf);
                ShapedRecipe sr = (ShapedRecipe) recipe;
                return new CopyNBTRecipe(sr);
            }
    );

    // @todo NEO
    @Override
    public MapCodec<CopyNBTRecipe> codec() {
        return null;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CopyNBTRecipe> streamCodec() {
        return STREAM_CODEC;
    }

    // @todo NEO
//    @Override
//    @Nonnull
//    public CopyNBTRecipe fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
//        ShapedRecipe recipe = serializer.fromJson(recipeId, json);
//        return new CopyNBTRecipe(recipe);
//    }
}
