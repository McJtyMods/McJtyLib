package mcjty.lib.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;

import java.util.OptionalDouble;

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


    public static final RenderType TRANSLUCENT_ADD = makeType("translucent_add", DefaultVertexFormats.BLOCK, GL11.GL_QUADS, 262144, true, false,
            State.getBuilder().shadeModel(SHADE_ENABLED)
                    .lightmap(LIGHTMAP_ENABLED)
                    .texture(BLOCK_SHEET_MIPPED)
                    .transparency(ADDITIVE_TRANSPARENCY)
                    .writeMask(COLOR_WRITE)
                    .build(true));

    public static final RenderType TRANSLUCENT_ADD_NOLIGHTMAPS = makeType("translucent_add_nolightmaps", DefaultVertexFormats.BLOCK, GL11.GL_QUADS, 262144, true, false,
            State.getBuilder().shadeModel(SHADE_ENABLED)
                    .lightmap(RenderState.LIGHTMAP_DISABLED)
                    .texture(BLOCK_SHEET_MIPPED)
                    .transparency(ADDITIVE_TRANSPARENCY)
                    .writeMask(COLOR_WRITE)
                    .build(true));


    public static final RenderType TRANSLUCENT_LIGHTNING_NOLIGHTMAPS = makeType("translucent_lightning_nolightmaps", DefaultVertexFormats.BLOCK, GL11.GL_QUADS, 262144, true, false,
            State.getBuilder().shadeModel(SHADE_ENABLED)
                    .lightmap(RenderState.LIGHTMAP_DISABLED)
                    .texture(BLOCK_SHEET_MIPPED)
                    .transparency(LIGHTNING_TRANSPARENCY)
                    .writeMask(COLOR_WRITE)
                    .build(true));


    private static final LineState THICK_LINES = new LineState(OptionalDouble.of(2.0D));

    public static final RenderType OVERLAY_LINES = makeType("overlay_lines",
            DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINES, 256,
            State.getBuilder().line(THICK_LINES)
                    .layer(VIEW_OFFSET_Z_LAYERING)
                    .transparency(TRANSLUCENT_TRANSPARENCY)
                    .texture(NO_TEXTURE)
                    .depthTest(DEPTH_ALWAYS)
                    .cull(CULL_DISABLED)
                    .lightmap(LIGHTMAP_DISABLED)
                    .writeMask(COLOR_WRITE)
                    .build(false));

    public static final RenderType QUADS_NOTEXTURE = makeType("quads_notexture",
            DefaultVertexFormats.POSITION_COLOR_LIGHTMAP, GL11.GL_QUADS, 2097152, true, false,
            State.getBuilder()
                    .texture(NO_TEXTURE)
                    .shadeModel(SHADE_ENABLED).lightmap(LIGHTMAP_ENABLED)
                    .build(false));

    public static final RenderType LINES_LIGHTMAP = makeType("lines_lightmap",
            DefaultVertexFormats.POSITION_COLOR_LIGHTMAP, GL11.GL_LINES, 256, true, false,
            State.getBuilder()
                    .line(new LineState(OptionalDouble.of(1.0)))
                    .texture(NO_TEXTURE)
                    .shadeModel(SHADE_ENABLED).lightmap(LIGHTMAP_ENABLED)
                    .build(false));

    public static final RenderState.TransparencyState LINESTRIP_TRANSP = new RenderState.TransparencyState("linestrip_transp", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    public static final RenderType LINESTRIP = makeType("linestrip",
            DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINE_STRIP, 256, true, false,
            State.getBuilder()
                    .line(new LineState(OptionalDouble.of(2.0)))
                    .transparency(LINESTRIP_TRANSP)
                    .texture(NO_TEXTURE)
                    .shadeModel(SHADE_ENABLED).lightmap(RenderState.LIGHTMAP_DISABLED)
                    .build(false));
}
