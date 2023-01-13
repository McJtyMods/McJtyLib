package mcjty.lib.multipart;

import mcjty.lib.McJtyLib;
import mcjty.lib.client.AbstractDynamicBakedModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class MultipartBakedModel extends AbstractDynamicBakedModel {

    public static final ModelResourceLocation MODEL = new ModelResourceLocation(new ResourceLocation(McJtyLib.MODID, "multipart"), "multipart");

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    @NotNull
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
//        if (state == null) {
//            return Collections.emptyList();
//        }
//
//        Map<PartSlot, MultipartTE.Part> parts = extraData.getData(MultipartTE.PARTS);
//
//        if (parts != null) {
//            List<BakedQuad> quads = new ArrayList<>();
//            RenderType layer = MinecraftForgeClient.getRenderType();
//
//            for (Map.Entry<PartSlot, MultipartTE.Part> entry : parts.entrySet()) {
//                MultipartTE.Part part = entry.getValue();
//                BlockState blockState = part.getState();
//                if (layer == null || ItemBlockRenderTypes.canRenderInLayer(blockState, layer)) {
//                    BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(blockState);
//                    try {
//                        if (!(model instanceof MultipartBakedModel)) {  // @todo safety
//                            quads.addAll(model.getQuads(state, side, rand));
//                        }
//                    } catch (Exception ignore) {
//                        System.out.println("MultipartBakedModel.getQuads");
//                    }
//                }
//            }
//            return quads;
//        }
        return Collections.emptyList();
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

    @Nonnull
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return getTexture(new ResourceLocation("minecraft", "missingno"));  // @todo 1.15
    }

    @Nonnull
    @Override
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }

    @Nonnull
    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

}
