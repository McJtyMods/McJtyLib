package mcjty.lib.multipart;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public class MultipartModel implements IUnbakedModel {

    @Nullable
    @Override
    public IBakedModel bake(ModelBakery bakery, Function spriteGetter, ISprite sprite, VertexFormat format) {
        return new MultipartBakedModel(Minecraft.getInstance().getTextureMap().getSprite(MissingTextureSprite.getLocation()));
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.emptyList();
    }

    @Override
    public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
        return Collections.emptyList();
    }

    @Override
    public IModelState getDefaultState() {
        return TRSRTransformation.identity();
    }
}
