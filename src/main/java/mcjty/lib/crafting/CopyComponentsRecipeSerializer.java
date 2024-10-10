package mcjty.lib.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class CopyComponentsRecipeSerializer implements RecipeSerializer<CopyComponentsRecipe> {

    private static final MapCodec<CopyComponentsRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ShapedRecipe.CODEC.fieldOf("recipe").forGetter(CopyComponentsRecipe::getRecipe)
    ).apply(instance, recipe -> new CopyComponentsRecipe((ShapedRecipe) recipe)));

    private static final StreamCodec<RegistryFriendlyByteBuf, CopyComponentsRecipe> STREAM_CODEC = StreamCodec.of(
            (buf, copyComponentsRecipe) -> {
                ShapedRecipe recipe = copyComponentsRecipe.getRecipe();
                ShapedRecipe.STREAM_CODEC.encode(buf, recipe);
            },
            buf -> {
                Recipe<?> recipe = ShapedRecipe.STREAM_CODEC.decode(buf);
                ShapedRecipe sr = (ShapedRecipe) recipe;
                return new CopyComponentsRecipe(sr);
            }
    );

    @Override
    public MapCodec<CopyComponentsRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CopyComponentsRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
