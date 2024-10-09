package mcjty.lib.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class CopyNBTRecipeSerializer implements RecipeSerializer<CopyNBTRecipe> {

    private static final MapCodec<CopyNBTRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ShapedRecipe.CODEC.fieldOf("recipe").forGetter(CopyNBTRecipe::getRecipe)
    ).apply(instance, recipe -> new CopyNBTRecipe((ShapedRecipe) recipe)));

    private static final StreamCodec<RegistryFriendlyByteBuf, CopyNBTRecipe> STREAM_CODEC = StreamCodec.of(
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

    @Override
    public MapCodec<CopyNBTRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CopyNBTRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
