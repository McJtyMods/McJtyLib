package mcjty.lib.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;

import javax.annotation.Nonnull;

public abstract class AbstractDynamicBakedModel implements IDynamicBakedModel {

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    public static TextureAtlasSprite getTexture(ResourceLocation resource) {
        //noinspection deprecation
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(resource);
    }

    protected static Vec3 v(double x, double y, double z) {
        return new Vec3(x, y, z);
    }

    protected BakedQuad createQuad(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, TextureAtlasSprite sprite,
                                   float r, float g, float b, float a) {
        Vec3 normal = v3.subtract(v2).cross(v1.subtract(v2)).normalize();

        QuadBakingVertexConsumer builder = new QuadBakingVertexConsumer();
        builder.setSprite(sprite);
        builder.setDirection(Direction.getNearest(normal.x, normal.y, normal.z));
        RenderHelper.putVertex(builder, normal, v1.x, v1.y, v1.z, 0, 0, sprite, r, g, b, a);
        RenderHelper.putVertex(builder, normal, v2.x, v2.y, v2.z, 0, 1, sprite, r, g, b, a);
        RenderHelper.putVertex(builder, normal, v3.x, v3.y, v3.z, 1, 1, sprite, r, g, b, a);
        RenderHelper.putVertex(builder, normal, v4.x, v4.y, v4.z, 1, 0, sprite, r, g, b, a);
        return builder.bakeQuad();
    }


    protected BakedQuad createQuad(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, TextureAtlasSprite sprite, float hilight) {
        Vec3 normal = v3.subtract(v2).cross(v1.subtract(v2)).normalize();

        QuadBakingVertexConsumer builder = new QuadBakingVertexConsumer();
        builder.setSprite(sprite);
        builder.setDirection(Direction.getNearest(normal.x, normal.y, normal.z));
        RenderHelper.putVertex(builder, normal, v1.x, v1.y, v1.z, 0, 0, sprite, hilight, hilight, hilight, hilight);
        RenderHelper.putVertex(builder, normal, v2.x, v2.y, v2.z, 0, 1, sprite, hilight, hilight, hilight, hilight);
        RenderHelper.putVertex(builder, normal, v3.x, v3.y, v3.z, 1, 1, sprite, hilight, hilight, hilight, hilight);
        RenderHelper.putVertex(builder, normal, v4.x, v4.y, v4.z, 1, 0, sprite, hilight, hilight, hilight, hilight);
        return builder.bakeQuad();
    }


    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    @Nonnull
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }


}
