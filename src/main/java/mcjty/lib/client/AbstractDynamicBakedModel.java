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
import java.util.List;

public abstract class AbstractDynamicBakedModel implements net.minecraft.client.renderer.block.dispatch.BlockStateModel, net.fabricmc.fabric.api.client.renderer.v1.model.FabricBlockStateModel {

    public static TextureAtlasSprite getTexture(Identifier resource) {

        return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(resource);

    }

    @Override
    public int materialFlags() {
        return 0;
    }

    @Override
    public Material.Baked particleMaterial() {
        return new Material.Baked(getTexture(Identifier.withDefaultNamespace("missingno")), false);
    }

    protected static Vec3 v(double x, double y, double z) {

        return new Vec3(x, y, z);

    }

    private final java.util.concurrent.ConcurrentMap<BakedQuad,Integer> colors=new com.google.common.collect.MapMaker().weakKeys().makeMap();

    public abstract void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts);

    @Override
    public void collectParts(RandomSource random,List<BlockStateModelPart> parts) {
        collectParts(BlockAndTintGetter.EMPTY,BlockPos.ZERO,net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(),random,parts);
    }

    @Override
    public Object createGeometryKey(BlockAndTintGetter level,BlockPos pos,BlockState state,RandomSource random) {
        return null;
    }

    @Override
    public void emitQuads(net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter emitter,BlockAndTintGetter level,BlockPos pos,BlockState state,RandomSource random,java.util.function.Predicate<Direction> cullTest) {

        var parts=new java.util.ArrayList<BlockStateModelPart>();
        collectParts(level,pos,state,random,parts);

        for(var part:parts) {

            for(Direction side:Direction.values())if(!cullTest.test(side))emitPart(emitter,part,side);

            emitPart(emitter,part,null);

        }

    }

    private void emitPart(net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter emitter,BlockStateModelPart part,Direction side) {

        for(var quad:part.getQuads(side)) {

            emitter.fromBakedQuad(quad).cullFace(side).ambientOcclusion(net.fabricmc.fabric.api.util.TriState.of(part.useAmbientOcclusion()));

            int color=colors.getOrDefault(quad,0xffffffff);
            emitter.color(color,color,color,color).emit();

        }

    }

    private static org.joml.Vector3f point(Vec3 v) {
        return new org.joml.Vector3f((float)v.x,(float)v.y,(float)v.z);
    }

    private static long uv(TextureAtlasSprite sprite,float u,float v) {
        return net.minecraft.client.model.geom.builders.UVPair.pack(sprite.getU(u),sprite.getV(v));
    }

    protected BakedQuad createQuad(Vec3 v1,Vec3 v2,Vec3 v3,Vec3 v4,TextureAtlasSprite sprite,float r,float g,float b,float a) {

        var normal=v3.subtract(v2).cross(v1.subtract(v2)).normalize();

        var material=BakedQuad.MaterialInfo.of(new Material.Baked(sprite,false),a < 1 ? com.mojang.blaze3d.platform.Transparency.TRANSLUCENT : sprite.contents().transparency(),-1,Direction.getApproximateNearest(normal.x, normal.y, normal.z),0);

        var quad=new BakedQuad(point(v1),point(v2),point(v3),point(v4),uv(sprite,0,0),uv(sprite,0,1),uv(sprite,1,1),uv(sprite,1,0),Direction.getApproximateNearest(normal.x,normal.y,normal.z),material);

        colors.put(quad,net.minecraft.util.ARGB.colorFromFloat(a,r,g,b));
        return quad;

    }

    protected BakedQuad createQuad(Vec3 v1,Vec3 v2,Vec3 v3,Vec3 v4,TextureAtlasSprite sprite,float highlight) {
        return createQuad(v1,v2,v3,v4,sprite,highlight,highlight,highlight,highlight);
    }

}
