package mcjty.lib.multipart;

import mcjty.lib.McJtyLib;
import mcjty.lib.client.AbstractDynamicBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class MultipartBakedModel extends AbstractDynamicBakedModel {

    public static final ModelResourceLocation MODEL = new ModelResourceLocation(McJtyLib.MODID + ":multipart");

//    private static void initTextures() {
//        if (cableTextures == null) {
//            CableTextures[] tt = new CableTextures[CableColor.VALUES.length];
//            for (CableColor color : CableColor.VALUES) {
//                int i = color.ordinal();
//                String typeName = color.getName();
//                tt[i] = new CableTextures();
//                tt[i].spriteConnector = Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(Ariente.MODID + ":blocks/cables/" + typeName + "/connector");
//                tt[i].spriteNormalCable = Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(Ariente.MODID + ":blocks/cables/" + typeName + "/normal_netcable");
//                tt[i].spriteNoneCable = Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(Ariente.MODID + ":blocks/cables/" + typeName + "/normal_none_netcable");
//                tt[i].spriteEndCable = Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(Ariente.MODID + ":blocks/cables/" + typeName + "/normal_end_netcable");
//                tt[i].spriteCornerCable = Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(Ariente.MODID + ":blocks/cables/" + typeName + "/normal_corner_netcable");
//                tt[i].spriteThreeCable = Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(Ariente.MODID + ":blocks/cables/" + typeName + "/normal_three_netcable");
//                tt[i].spriteCrossCable = Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(Ariente.MODID + ":blocks/cables/" + typeName + "/normal_cross_netcable");
//            }
//
//            spriteSide = Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(Ariente.MODID + ":blocks/cables/connector_side");
//            cableTextures = tt;
//        }
//    }
//

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        if (state == null) {
            return Collections.emptyList();
        }

        Map<PartSlot, MultipartTE.Part> parts = extraData.getData(MultipartTE.PARTS);

        if (parts != null) {
            List<BakedQuad> quads = new ArrayList<>();
            RenderType layer = MinecraftForgeClient.getRenderLayer();

            for (Map.Entry<PartSlot, MultipartTE.Part> entry : parts.entrySet()) {
                MultipartTE.Part part = entry.getValue();
                BlockState blockState = part.getState();
                if (layer == null || RenderTypeLookup.canRenderInLayer(blockState, layer)) {
                    IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getModel(blockState);
                    try {
                        if (!(model instanceof MultipartBakedModel)) {  // @todo safety
                            quads.addAll(model.getQuads(state, side, rand));
                        }
                    } catch (Exception ignore) {
                        System.out.println("MultipartBakedModel.getQuads");
                    }
                }
            }
            return quads;
        }
        return Collections.emptyList();
    }


    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return getTexture(new ResourceLocation("minecraft", "missingno"));  // @todo 1.15
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }

}
