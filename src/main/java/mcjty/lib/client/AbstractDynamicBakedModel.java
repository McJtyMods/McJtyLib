package mcjty.lib.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;

public abstract class AbstractDynamicBakedModel implements IDynamicBakedModel {

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    public static TextureAtlasSprite getTexture(ResourceLocation resource) {
        //noinspection deprecation
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(resource);
    }

    protected void putVertex(BakedQuadBuilder builder, Vec3 normal,
                           double x, double y, double z, float u, float v, TextureAtlasSprite sprite, float r, float g, float b, float a) {
        ImmutableList<VertexFormatElement> elements = builder.getVertexFormat().getElements().asList();
        for (int e = 0; e < elements.size(); e++) {
            switch (elements.get(e).getUsage()) {
                case POSITION:
                    builder.put(e, (float)x, (float)y, (float)z);
                    break;
                case COLOR:
                    builder.put(e, r, g, b, a);
                    break;
                case UV:
                    switch (elements.get(e).getIndex()) {
                        case 0:
                            float iu = sprite.getU(u);
                            float iv = sprite.getV(v);
                            builder.put(e, iu, iv);
                            break;
                        case 2:
                            builder.put(e, (short) 0, (short) 0);
//                            builder.put(e, 0f, 1f);
                            break;
                        default:
                            builder.put(e);
                            break;
                    }
                    break;
                case NORMAL:
                    builder.put(e, (float) normal.x, (float) normal.y, (float) normal.z);
                    break;
                default:
                    builder.put(e);
                    break;
            }
        }
    }

    protected static Vec3 v(double x, double y, double z) {
        return new Vec3(x, y, z);
    }

    protected BakedQuad createQuad(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, TextureAtlasSprite sprite,
                                   float r, float g, float b, float a) {
        Vec3 normal = v3.subtract(v2).cross(v1.subtract(v2)).normalize();

        BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
        builder.setQuadOrientation(Direction.getNearest(normal.x, normal.y, normal.z));
        putVertex(builder, normal, v1.x, v1.y, v1.z, 0, 0, sprite, r, g, b, a);
        putVertex(builder, normal, v2.x, v2.y, v2.z, 0, 16, sprite, r, g, b, a);
        putVertex(builder, normal, v3.x, v3.y, v3.z, 16, 16, sprite, r, g, b, a);
        putVertex(builder, normal, v4.x, v4.y, v4.z, 16, 0, sprite, r, g, b, a);
        return builder.build();
    }


    protected BakedQuad createQuad(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, TextureAtlasSprite sprite) {
        Vec3 normal = v3.subtract(v2).cross(v1.subtract(v2)).normalize();

        BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
        builder.setQuadOrientation(Direction.getNearest(normal.x, normal.y, normal.z));
        putVertex(builder, normal, v1.x, v1.y, v1.z, 0, 0, sprite, 1.0f, 1.0f, 1.0f, 1.0f);
        putVertex(builder, normal, v2.x, v2.y, v2.z, 0, 16, sprite, 1.0f, 1.0f, 1.0f, 1.0f);
        putVertex(builder, normal, v3.x, v3.y, v3.z, 16, 16, sprite, 1.0f, 1.0f, 1.0f, 1.0f);
        putVertex(builder, normal, v4.x, v4.y, v4.z, 16, 0, sprite, 1.0f, 1.0f, 1.0f, 1.0f);
        return builder.build();
    }

    protected BakedQuad createQuad(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, TextureAtlasSprite sprite, float hilight) {
        Vec3 normal = v3.subtract(v2).cross(v1.subtract(v2)).normalize();

        BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
        builder.setQuadOrientation(Direction.getNearest(normal.x, normal.y, normal.z));
        putVertex(builder, normal, v1.x, v1.y, v1.z, 0, 0, sprite, hilight, hilight, hilight, hilight);
        putVertex(builder, normal, v2.x, v2.y, v2.z, 0, 16, sprite, hilight, hilight, hilight, hilight);
        putVertex(builder, normal, v3.x, v3.y, v3.z, 16, 16, sprite, hilight, hilight, hilight, hilight);
        putVertex(builder, normal, v4.x, v4.y, v4.z, 16, 0, sprite, hilight, hilight, hilight, hilight);
        return builder.build();
    }


    protected BakedQuad createQuadReversed(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, TextureAtlasSprite sprite) {
        Vec3 normal = v3.subtract(v1).cross(v2.subtract(v1)).normalize();

        BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
        builder.setQuadOrientation(Direction.getNearest(normal.x, normal.y, normal.z));
        putVertex(builder, normal, v1.x, v1.y, v1.z, 0, 0, sprite, 1.0f, 1.0f, 1.0f, 1.0f);
        putVertex(builder, normal, v2.x, v2.y, v2.z, 0, 16, sprite, 1.0f, 1.0f, 1.0f, 1.0f);
        putVertex(builder, normal, v3.x, v3.y, v3.z, 16, 16, sprite, 1.0f, 1.0f, 1.0f, 1.0f);
        putVertex(builder, normal, v4.x, v4.y, v4.z, 16, 0, sprite, 1.0f, 1.0f, 1.0f, 1.0f);
        return builder.build();
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
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }


}
