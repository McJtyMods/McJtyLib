package mcjty.lib.multipart;

import mcjty.lib.McJtyLib;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MultipartBakedModel implements IBakedModel {

    public static final ModelResourceLocation MODEL = new ModelResourceLocation(McJtyLib.PROVIDES + ":multipart");

    private TextureAtlasSprite particleTexture;

//    private static void initTextures() {
//        if (cableTextures == null) {
//            CableTextures[] tt = new CableTextures[CableColor.VALUES.length];
//            for (CableColor color : CableColor.VALUES) {
//                int i = color.ordinal();
//                String typeName = color.getName();
//                tt[i] = new CableTextures();
//                tt[i].spriteConnector = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Ariente.MODID + ":blocks/cables/" + typeName + "/connector");
//                tt[i].spriteNormalCable = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Ariente.MODID + ":blocks/cables/" + typeName + "/normal_netcable");
//                tt[i].spriteNoneCable = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Ariente.MODID + ":blocks/cables/" + typeName + "/normal_none_netcable");
//                tt[i].spriteEndCable = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Ariente.MODID + ":blocks/cables/" + typeName + "/normal_end_netcable");
//                tt[i].spriteCornerCable = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Ariente.MODID + ":blocks/cables/" + typeName + "/normal_corner_netcable");
//                tt[i].spriteThreeCable = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Ariente.MODID + ":blocks/cables/" + typeName + "/normal_three_netcable");
//                tt[i].spriteCrossCable = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Ariente.MODID + ":blocks/cables/" + typeName + "/normal_cross_netcable");
//            }
//
//            spriteSide = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Ariente.MODID + ":blocks/cables/connector_side");
//            cableTextures = tt;
//        }
//    }
//
    public MultipartBakedModel(TextureAtlasSprite particleTexture) {
        this.particleTexture = particleTexture;
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        if (state == null) {
            return Collections.emptyList();
        }

        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;

        Map<PartSlot, MultipartTE.Part> parts = extendedBlockState.getValue(MultipartBlock.PARTS);
        if (parts != null) {
            List<BakedQuad> quads = new ArrayList<>();
            BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();

            for (Map.Entry<PartSlot, MultipartTE.Part> entry : parts.entrySet()) {
                MultipartTE.Part part = entry.getValue();
                IBlockState blockState = part.getState();
                if (layer == null || blockState.getBlock().canRenderInLayer(blockState, layer)) {
                    IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(blockState);
                    try {
                        quads.addAll(model.getQuads(state, side, rand++));
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
        return particleTexture;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

}
