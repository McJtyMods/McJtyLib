package mcjty.lib.client;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

public final class CustomRenderTypes {
    private static final RenderPipeline ADDITIVE = blockPipeline("additive", BlendFunction.ADDITIVE);
    private static final RenderPipeline OVERLAY = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath("mcjtylib", "pipeline/overlay_lines"))
            .withDepthStencilState(java.util.Optional.empty()).build();
    private static final RenderPipeline LIGHTNING = blockPipeline("lightning", BlendFunction.LIGHTNING);

    private CustomRenderTypes() { }

    private static RenderPipeline blockPipeline(String name, BlendFunction blend) {
        return RenderPipeline.builder(RenderPipelines.BLOCK_SNIPPET)
                .withLocation(Identifier.fromNamespaceAndPath("mcjtylib", "pipeline/" + name))
                .withColorTargetState(new ColorTargetState(blend))
                .withDepthStencilState(new com.mojang.blaze3d.pipeline.DepthStencilState(
                        com.mojang.blaze3d.platform.CompareOp.GREATER_THAN_OR_EQUAL, false))
                .build();
    }

    private static RenderType blockType(String name, RenderPipeline pipeline) {
        return RenderType.create(name, RenderSetup.builder(pipeline)
                .withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS).useLightmap().createRenderSetup());
    }

    public static final RenderType TRANSLUCENTdup = RenderTypes.translucentMovingBlock();
    public static final RenderType TRANSLUCENT_ADD = blockType("mcjtylib_additive", ADDITIVE);
    // Fullbright geometry uses maximum light coordinates instead of changing global GL lighting state.
    public static final RenderType TRANSLUCENT_ADD_NOLIGHTMAPS = TRANSLUCENT_ADD;
    public static final RenderType TRANSLUCENT_LIGHTNING_NOLIGHTMAPS = blockType("mcjtylib_lightning", LIGHTNING);
    public static final RenderType OVERLAY_LINES = RenderType.create("mcjtylib_overlay_lines", RenderSetup.builder(OVERLAY).createRenderSetup());
    public static final RenderType LINES_LIGHTMAP = RenderTypes.LINES;
    public static final RenderType LINESTRIP = RenderTypes.LINES_TRANSLUCENT;
    public static final RenderType QUADS_NOTEXTURE = RenderTypes.debugQuads();

    public static RenderType translucent() { return TRANSLUCENTdup; }

    public static void registerPipelines(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(ADDITIVE);
        event.registerPipeline(OVERLAY);
        event.registerPipeline(LIGHTNING);
    }
}
