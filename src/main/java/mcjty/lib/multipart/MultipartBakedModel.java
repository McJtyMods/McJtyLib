package mcjty.lib.multipart;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultipartBakedModel implements IBakedModel {

    private VertexFormat format;
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
    public MultipartBakedModel(VertexFormat format, TextureAtlasSprite particleTexture) {
        this.format = format;
        this.particleTexture = particleTexture;
    }

    private void putVertex(UnpackedBakedQuad.Builder builder, Vec3d normal,
                           double x, double y, double z, float u, float v, TextureAtlasSprite sprite, float color) {
        for (int e = 0; e < format.getElementCount(); e++) {
            switch (format.getElement(e).getUsage()) {
                case POSITION:
                    builder.put(e, (float)x, (float)y, (float)z, 1.0f);
                    break;
                case COLOR:
                    builder.put(e, color, color, color, 1.0f);
                    break;
                case UV:
                    if (format.getElement(e).getIndex() == 0) {
                        u = sprite.getInterpolatedU(u);
                        v = sprite.getInterpolatedV(v);
                        builder.put(e, u, v, 0f, 1f);
                        break;
                    }
                case NORMAL:
                    builder.put(e, (float) normal.x, (float) normal.y, (float) normal.z, 0f);
                    break;
                default:
                    builder.put(e);
                    break;
            }
        }
    }

    private void createQuadI(List<BakedQuad> quads, Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, TextureAtlasSprite sprite, float hilight) {
        Vec3d normal = v3.subtract(v2).crossProduct(v1.subtract(v2)).normalize();

        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
        builder.setTexture(sprite);
        putVertex(builder, normal, v1.x, v1.y, v1.z, 0, 0, sprite, hilight);
        putVertex(builder, normal, v2.x, v2.y, v2.z, 0, 16, sprite, hilight);
        putVertex(builder, normal, v3.x, v3.y, v3.z, 16, 16, sprite, hilight);
        putVertex(builder, normal, v4.x, v4.y, v4.z, 16, 0, sprite, hilight);
        quads.add(builder.build());
    }

    private static Vec3d v(double x, double y, double z) {
        return new Vec3d(x, y, z);
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        if (state == null) {
            return Collections.emptyList();
        }

        if (side != null || (MinecraftForgeClient.getRenderLayer() != BlockRenderLayer.CUTOUT_MIPPED)) {
            return Collections.emptyList();
        }

        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;

        List<Part> parts = new ArrayList<>();
        List<BakedQuad> quads = new ArrayList<>();

        for (Part part : parts) {
            part.addGeometry(new IGeometryCollector() {
                @Override
                public void addQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, TextureAtlasSprite sprite, float hilight) {
                    createQuadI(quads, v1, v2, v3, v4, sprite, hilight);
                }

                @Override
                public void addCube(Vec3d min, Vec3d max, TextureAtlasSprite sprite, float hilight) {
                    double mx = min.x;
                    double my = min.y;
                    double mz = min.z;
                    double px = max.x;
                    double py = max.y;
                    double pz = max.z;
                }
            });
        }
        return quads;
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
