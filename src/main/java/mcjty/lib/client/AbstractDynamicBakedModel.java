package mcjty.lib.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.core.*;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import java.util.List;

public abstract class AbstractDynamicBakedModel implements DynamicBlockStateModel {
    public static TextureAtlasSprite getTexture(Identifier resource) {
        return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(resource);
    }
    @Override public int materialFlags() { return 0; }
    @Override public Material.Baked particleMaterial() { return new Material.Baked(getTexture(Identifier.withDefaultNamespace("missingno")), false); }
    protected static Vec3 v(double x, double y, double z) {
        return new Vec3(x, y, z);
    }

    protected BakedQuad createQuad(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, TextureAtlasSprite sprite,
                                   float r, float g, float b, float a) {
        Vec3 normal = v3.subtract(v2).cross(v1.subtract(v2)).normalize();

        QuadBakingVertexConsumer builder = new QuadBakingVertexConsumer();
        builder.setSprite(new net.minecraft.client.resources.model.sprite.Material.Baked(sprite, false));
        builder.setDirection(Direction.getApproximateNearest(normal.x, normal.y, normal.z));
        RenderHelper.putVertex(builder, normal, v1.x, v1.y, v1.z, 0, 0, sprite, r, g, b, a);
        RenderHelper.putVertex(builder, normal, v2.x, v2.y, v2.z, 0, 1, sprite, r, g, b, a);
        RenderHelper.putVertex(builder, normal, v3.x, v3.y, v3.z, 1, 1, sprite, r, g, b, a);
        RenderHelper.putVertex(builder, normal, v4.x, v4.y, v4.z, 1, 0, sprite, r, g, b, a);
        return builder.bakeQuad();
    }


    protected BakedQuad createQuad(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, TextureAtlasSprite sprite, float hilight) {
        Vec3 normal = v3.subtract(v2).cross(v1.subtract(v2)).normalize();

        QuadBakingVertexConsumer builder = new QuadBakingVertexConsumer();
        builder.setSprite(new net.minecraft.client.resources.model.sprite.Material.Baked(sprite, false));
        builder.setDirection(Direction.getApproximateNearest(normal.x, normal.y, normal.z));
        RenderHelper.putVertex(builder, normal, v1.x, v1.y, v1.z, 0, 0, sprite, hilight, hilight, hilight, hilight);
        RenderHelper.putVertex(builder, normal, v2.x, v2.y, v2.z, 0, 1, sprite, hilight, hilight, hilight, hilight);
        RenderHelper.putVertex(builder, normal, v3.x, v3.y, v3.z, 1, 1, sprite, hilight, hilight, hilight, hilight);
        RenderHelper.putVertex(builder, normal, v4.x, v4.y, v4.z, 1, 0, sprite, hilight, hilight, hilight, hilight);
        return builder.bakeQuad();
    }



}
