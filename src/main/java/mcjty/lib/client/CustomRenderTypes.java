package mcjty.lib.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.opengl.GL11;

import java.util.OptionalDouble;

public class CustomRenderTypes extends RenderType {

    // Dummy
    public CustomRenderTypes(String name, VertexFormat format, VertexFormat.Mode p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable setup, Runnable clear) {
        super(name, format, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, setup, clear);
    }

//    public static final VertexFormat POSITION_COLOR_LIGHTMAP_NORMAL;
//
//    static {
//        POSITION_COLOR_LIGHTMAP_NORMAL = new VertexFormat(ImmutableList.<VertexFormatElement>builder()
//                .add(POSITION_3F).add(COLOR_4UB).add(TEX_2SB).add(NORMAL_3B)
//                .build());
//    }


    public static final RenderType TRANSLUCENT_ADD = create("translucent_add", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 262144, true, false,
            CompositeState.builder()
                    .setShaderState(RENDERTYPE_TRANSLUCENT_SHADER)
//                    .setShadeModelState(SMOOTH_SHADE) // @todo 1.17 check
                    .setLightmapState(LIGHTMAP)
                    .setTextureState(BLOCK_SHEET_MIPPED)
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(true));

    public static final RenderType TRANSLUCENT_ADD_NOLIGHTMAPS = create("translucent_add_nolightmaps", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 262144, true, false,
            CompositeState.builder()
                    .setShaderState(RENDERTYPE_TRANSLUCENT_SHADER)
//                    .setShadeModelState(SMOOTH_SHADE) // @todo 1.17 check
                    .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                    .setTextureState(BLOCK_SHEET_MIPPED)
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(true));


    public static final RenderType TRANSLUCENT_LIGHTNING_NOLIGHTMAPS = create("translucent_lightning_nolightmaps", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 262144, true, false,
            CompositeState.builder()
                    .setShaderState(RENDERTYPE_TRANSLUCENT_SHADER)
//                    .setShadeModelState(SMOOTH_SHADE) // @todo 1.17 check
                    .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                    .setTextureState(BLOCK_SHEET_MIPPED)
                    .setTransparencyState(LIGHTNING_TRANSPARENCY)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(true));


    private static final LineStateShard THICK_LINES = new LineStateShard(OptionalDouble.of(2.0D));

    public static final RenderType OVERLAY_LINES = create("overlay_lines",
            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.LINES, 256,
            false, false,
            CompositeState.builder().setLineState(THICK_LINES)
                    .setShaderState(RENDERTYPE_LINES_SHADER)
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setTextureState(NO_TEXTURE)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setCullState(NO_CULL)
                    .setLightmapState(NO_LIGHTMAP)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false));

    public static final RenderType QUADS_NOTEXTURE = create("quads_notexture",
            DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.QUADS, 2097152, true, false,
            CompositeState.builder()
//                    .setShaderState(REN)  // @todo 1.17?
                    .setTextureState(NO_TEXTURE)
//                    .setShadeModelState(SMOOTH_SHADE) // @todo 1.17 check
                    .setLightmapState(LIGHTMAP)
                    .createCompositeState(false));

    public static final RenderType LINES_LIGHTMAP = create("lines_lightmap",
            DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.QUADS, 256, true, false,
            CompositeState.builder()
//                    .setShaderState(REN)  // @todo 1.17?
                    .setLineState(new LineStateShard(OptionalDouble.of(1.0)))
                    .setTextureState(NO_TEXTURE)
//                    .setShadeModelState(SMOOTH_SHADE) // @todo 1.17 check
                    .setLightmapState(LIGHTMAP)
                    .createCompositeState(false));

    // Workaround for a weird bug with lambda's
    public static final Runnable TRANSP_ENABLE = new Runnable() {
        @Override
        public void run() {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
        }
    };
    public static final Runnable TRANSP_DISABLE = new Runnable() {
        @Override
        public void run() {
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
        }
    };
    public static final RenderStateShard.TransparencyStateShard LINESTRIP_TRANSP = new RenderStateShard.TransparencyStateShard("linestrip_transp", TRANSP_ENABLE, TRANSP_DISABLE);

    public static final RenderType LINESTRIP = create("linestrip",
            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.LINE_STRIP, 256, true, false,
            CompositeState.builder()
                    //.setShaderState(...) @todo 1.17
                    .setLineState(new LineStateShard(OptionalDouble.of(2.0)))
                    .setTransparencyState(LINESTRIP_TRANSP)
                    .setTextureState(NO_TEXTURE)
//                    .setShadeModelState(SMOOTH_SHADE) @todo 1.16
                    .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                    .createCompositeState(false));
}
