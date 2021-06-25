package mcjty.lib.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;

import java.util.OptionalDouble;

import net.minecraft.client.renderer.RenderState.LineState;
import net.minecraft.client.renderer.RenderType.State;

public class CustomRenderTypes extends RenderType {

    // Dummy
    public CustomRenderTypes(String name, VertexFormat format, int p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable setup, Runnable clear) {
        super(name, format, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, setup, clear);
    }

//    public static final VertexFormat POSITION_COLOR_LIGHTMAP_NORMAL;
//
//    static {
//        POSITION_COLOR_LIGHTMAP_NORMAL = new VertexFormat(ImmutableList.<VertexFormatElement>builder()
//                .add(POSITION_3F).add(COLOR_4UB).add(TEX_2SB).add(NORMAL_3B)
//                .build());
//    }


    public static final RenderType TRANSLUCENT_ADD = create("translucent_add", DefaultVertexFormats.BLOCK, GL11.GL_QUADS, 262144, true, false,
            State.builder().setShadeModelState(SMOOTH_SHADE)
                    .setLightmapState(LIGHTMAP)
                    .setTextureState(BLOCK_SHEET_MIPPED)
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(true));

    public static final RenderType TRANSLUCENT_ADD_NOLIGHTMAPS = create("translucent_add_nolightmaps", DefaultVertexFormats.BLOCK, GL11.GL_QUADS, 262144, true, false,
            State.builder().setShadeModelState(SMOOTH_SHADE)
                    .setLightmapState(RenderState.NO_LIGHTMAP)
                    .setTextureState(BLOCK_SHEET_MIPPED)
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(true));


    public static final RenderType TRANSLUCENT_LIGHTNING_NOLIGHTMAPS = create("translucent_lightning_nolightmaps", DefaultVertexFormats.BLOCK, GL11.GL_QUADS, 262144, true, false,
            State.builder().setShadeModelState(SMOOTH_SHADE)
                    .setLightmapState(RenderState.NO_LIGHTMAP)
                    .setTextureState(BLOCK_SHEET_MIPPED)
                    .setTransparencyState(LIGHTNING_TRANSPARENCY)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(true));


    private static final LineState THICK_LINES = new LineState(OptionalDouble.of(2.0D));

    public static final RenderType OVERLAY_LINES = create("overlay_lines",
            DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINES, 256,
            State.builder().setLineState(THICK_LINES)
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setTextureState(NO_TEXTURE)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setCullState(NO_CULL)
                    .setLightmapState(NO_LIGHTMAP)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false));

    public static final RenderType QUADS_NOTEXTURE = create("quads_notexture",
            DefaultVertexFormats.POSITION_COLOR_LIGHTMAP, GL11.GL_QUADS, 2097152, true, false,
            State.builder()
                    .setTextureState(NO_TEXTURE)
                    .setShadeModelState(SMOOTH_SHADE).setLightmapState(LIGHTMAP)
                    .createCompositeState(false));

    public static final RenderType LINES_LIGHTMAP = create("lines_lightmap",
            DefaultVertexFormats.POSITION_COLOR_LIGHTMAP, GL11.GL_LINES, 256, true, false,
            State.builder()
                    .setLineState(new LineState(OptionalDouble.of(1.0)))
                    .setTextureState(NO_TEXTURE)
                    .setShadeModelState(SMOOTH_SHADE).setLightmapState(LIGHTMAP)
                    .createCompositeState(false));

    public static final RenderState.TransparencyState LINESTRIP_TRANSP = new RenderState.TransparencyState("linestrip_transp", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    public static final RenderType LINESTRIP = create("linestrip",
            DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINE_STRIP, 256, true, false,
            State.builder()
                    .setLineState(new LineState(OptionalDouble.of(2.0)))
                    .setTransparencyState(LINESTRIP_TRANSP)
                    .setTextureState(NO_TEXTURE)
                    .setShadeModelState(SMOOTH_SHADE).setLightmapState(RenderState.NO_LIGHTMAP)
                    .createCompositeState(false));
}
