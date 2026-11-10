package mcjty.lib.client;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractDynamicBakedModel implements BakedModel, FabricBakedModel {

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

        int[] data = new int[32];
        Vec3[] points = {v1, v2, v3, v4};
        float[] us = {0, 0, 1, 1};
        float[] vs = {0, 1, 1, 0};
        int color = ((int) (a * 255) << 24) | ((int) (b * 255) << 16) | ((int) (g * 255) << 8) | (int) (r * 255);
        int packedNormal = ((int) (normal.x * 127) & 255) | (((int) (normal.y * 127) & 255) << 8) | (((int) (normal.z * 127) & 255) << 16);
        for (int i = 0; i < 4; i++) {
            int offset = i * 8;
            data[offset] = Float.floatToRawIntBits((float) points[i].x);
            data[offset + 1] = Float.floatToRawIntBits((float) points[i].y);
            data[offset + 2] = Float.floatToRawIntBits((float) points[i].z);
            data[offset + 3] = color;
            data[offset + 4] = Float.floatToRawIntBits(sprite.getU(us[i]));
            data[offset + 5] = Float.floatToRawIntBits(sprite.getV(vs[i]));
            data[offset + 6] = 0;
            data[offset + 7] = packedNormal;
        }
        return new BakedQuad(data, -1, Direction.getNearest(normal.x, normal.y, normal.z), sprite, true);
    }

    protected BakedQuad createQuad(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, TextureAtlasSprite sprite, float highlight) {
        return createQuad(v1, v2, v3, v4, sprite, highlight, highlight, highlight, highlight);
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

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) {
        return List.of();
    }

    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random, ModelData data, RenderType layer) {
        return getQuads(state, side, random);
    }

    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData data) {
        return data;
    }

    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data) {
        return ChunkRenderTypeSet.of(state == null ? RenderType.cutout() : ItemBlockRenderTypes.getChunkRenderType(state));
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter level, BlockState state, BlockPos pos, Supplier<RandomSource> random, RenderContext context) {
        ModelData data = getModelData(level, pos, state, ModelData.EMPTY);
        var renderer = RendererAccess.INSTANCE.getRenderer();
        var emitter = context.getEmitter();
        for (var layer : getRenderTypes(state, random.get(), data)) {
            var material = renderer.materialFinder().blendMode(0, BlendMode.fromRenderLayer(layer)).find();
            for (int side = 0; side < 7; side++) {
                Direction face = side == 6 ? null : Direction.values()[side];
                for (var quad : getQuads(state, face, random.get(), data, layer)) {
                    emitter.fromVanilla(quad, material, face).emit();
                }
            }
        }
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> random, RenderContext context) {
        var material = RendererAccess.INSTANCE.getRenderer().materialFinder().find();
        for (int side = 0; side < 7; side++) {
            Direction face = side == 6 ? null : Direction.values()[side];
            for (var quad : getQuads(null, face, random.get(), ModelData.EMPTY, null)) {
                context.getEmitter().fromVanilla(quad, material, null).emit();
            }
        }
    }

}
