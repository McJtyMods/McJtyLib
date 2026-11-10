package mcjty.lib.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public final class ModelTools {

    public static void registerModelBakeEvent(Consumer<Map<ModelResourceLocation, BakedModel>> consumer) {
        ModelLoadingPlugin.register(plugin -> plugin.modifyModelAfterBake().register((model, context) -> {
            if (context.topLevelId() == null) return model;
            Map<ModelResourceLocation, BakedModel> models = new HashMap<>();
            models.put(context.topLevelId(), model);
            consumer.accept(models);
            return models.get(context.topLevelId());
        }));
    }

    public static List<BakedQuad> quads(BakedModel model, BlockState state, Direction side, RandomSource random, ModelData data, RenderType layer) {
        return model instanceof AbstractDynamicBakedModel dynamic ? dynamic.getQuads(state, side, random, data, layer) : model.getQuads(state, side, random);
    }

    public static ModelData data(BakedModel model, BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData data) {
        return model instanceof AbstractDynamicBakedModel dynamic ? dynamic.getModelData(level, pos, state, data) : data;
    }

    public static ChunkRenderTypeSet renderTypes(BakedModel model, BlockState state, RandomSource random, ModelData data) {
        return model instanceof AbstractDynamicBakedModel dynamic ? dynamic.getRenderTypes(state, random, data) : ChunkRenderTypeSet.of(ItemBlockRenderTypes.getChunkRenderType(state));
    }

}
