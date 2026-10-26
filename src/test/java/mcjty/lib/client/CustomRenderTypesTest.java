package mcjty.lib.client;

import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import net.minecraft.client.renderer.RenderPipelines;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomRenderTypesTest {
    @Test
    void overlayMatchesTheWorldPassColorAttachment() {
        var targets = CustomRenderTypes.OVERLAY_LINES.pipeline().getColorTargetStates();
        assertEquals(RenderPipelines.LINES.getColorTargetStates().size(), targets.size(),
                "Connector overlays must match the world pass color attachment count");
        assertEquals(1, targets.size());
        assertNotNull(targets.getFirst());
    }

    @Test
    void overlayPreservesAlphaBlendingAndVisibilityThroughBlocks() {
        var pipeline = CustomRenderTypes.OVERLAY_LINES.pipeline();
        assertEquals(java.util.List.of(new ColorTargetState(BlendFunction.TRANSLUCENT)), pipeline.getColorTargetStates());
        assertNull(pipeline.getDepthStencilState(), "Cable and highlighted-block overlays must remain visible through blocks");
    }
}
