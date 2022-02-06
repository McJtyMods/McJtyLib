package mcjty.lib.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * A delayed render can be used from within a BER (TER) in order to make it possible
 * to render translucent bits on top of everything else
 */
public class DelayedRenderer {

    private static final List<Pair<BlockPos, BiConsumer<PoseStack, MultiBufferSource>>> renders = new ArrayList<>();

    public static void render(PoseStack matrixStack) {
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        renders.forEach(r -> {
            BlockPos pos = r.getKey();
            matrixStack.pushPose();
            matrixStack.translate(pos.getX() - projectedView.x, pos.getY() - projectedView.y, pos.getZ() - projectedView.z);
            r.getValue().accept(matrixStack, buffer);
            matrixStack.popPose();
        });
        renders.clear();
        RenderSystem.enableDepthTest();
        buffer.endBatch(CustomRenderTypes.TRANSLUCENT_LIGHTNING_NOLIGHTMAPS);
        buffer.endBatch(CustomRenderTypes.TRANSLUCENT_ADD_NOLIGHTMAPS);
    }

    public static void addRender(BlockPos pos, BiConsumer<PoseStack, MultiBufferSource> renderer) {
        renders.add(Pair.of(pos, renderer));
    }
}
