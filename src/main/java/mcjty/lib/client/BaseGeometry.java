package mcjty.lib.client;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public abstract class BaseGeometry<T extends IUnbakedGeometry<T>> implements IUnbakedGeometry<T> {

    public abstract BakedModel bake();

    public abstract Collection<Material> getMaterials();

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBakery bakery, Function spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        return bake();
    }

    @Override
    public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return getMaterials();
    }
}
