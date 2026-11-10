package mcjty.lib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class BakedModelWrapper<T extends BakedModel> extends AbstractDynamicBakedModel {

    protected final T originalModel;

    public BakedModelWrapper(T originalModel) {
        this.originalModel = originalModel;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) {
        return originalModel.getQuads(state, side, random);
    }

    @Override
    public List<BakedQuad> getQuads(
            BlockState state,
            Direction side,
            RandomSource random,
            ModelData data,
            RenderType layer) {
        return ModelTools.quads(originalModel, state, side, random, data, layer);
    }

    @Override
    public ModelData getModelData(
            BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData data) {
        return ModelTools.data(originalModel, level, pos, state, data);
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(
            BlockState state, RandomSource random, ModelData data) {
        return ModelTools.renderTypes(originalModel, state, random, data);
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return originalModel.getParticleIcon();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return originalModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return originalModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return originalModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return originalModel.isCustomRenderer();
    }

    @Override
    public ItemTransforms getTransforms() {
        return originalModel.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return originalModel.getOverrides();
    }

    public BakedModel applyTransform(
            ItemDisplayContext context, PoseStack poses, boolean leftHand) {
        originalModel.getTransforms().getTransform(context).apply(leftHand, poses);
        return this;
    }

    public List<BakedModel> getRenderPasses(ItemStack stack, boolean fabulous) {
        return List.of(this);
    }
}
