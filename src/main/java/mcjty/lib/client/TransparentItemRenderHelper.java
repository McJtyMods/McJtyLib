package mcjty.lib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import java.util.List;

public final class TransparentItemRenderHelper {
    private TransparentItemRenderHelper() { }

    public static void renderItemModelWithAlpha(PoseStack pose, SubmitNodeCollector collector, List<BakedQuad> quads, int[] tintLayers, int light, int overlay, int alpha) {
        int opacity = Math.clamp(alpha, 0, 255);
        for (var quad : quads) {
            var material = quad.materialInfo();
            int tint = material.isTinted() && material.tintIndex() < tintLayers.length ? tintLayers[material.tintIndex()] : 0xffffffff;
            int color = (tint & 0xffffff) | (opacity << 24);
            collector.submitCustomGeometry(pose, RenderTypes.entityTranslucent(material.sprite().atlasLocation()), (captured, buffer) -> {
                var instance = new com.mojang.blaze3d.vertex.QuadInstance();
                instance.setColor(color);
                instance.setLightCoords(light);
                instance.setOverlayCoords(overlay);
                buffer.putBakedQuad(captured, quad, instance);
            });
        }
    }
}
