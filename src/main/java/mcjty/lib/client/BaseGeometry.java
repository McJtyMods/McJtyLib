package mcjty.lib.client;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public abstract class BaseGeometry<T> implements UnbakedModel {

    public abstract BakedModel bake();

    public abstract Collection<Material> getMaterials();

    public Collection<ResourceLocation> getDependencies() {
        return List.of();
    }

    public void resolveParents(Function<ResourceLocation, UnbakedModel> models) {}

    @Override
    public BakedModel bake(
            ModelBaker modelBaker,
            Function<Material, TextureAtlasSprite> function,
            ModelState modelState) {
        return bake();
    }
}
