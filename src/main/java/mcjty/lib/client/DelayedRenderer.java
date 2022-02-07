package mcjty.lib.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * A delayed render can be used from within a BER (TER) in order to make it possible
 * to render translucent bits on top of everything else
 */
public class DelayedRenderer {

    private static final Map<RenderType, List<Pair<BlockPos, BiConsumer<PoseStack, MultiBufferSource>>>> renders = new HashMap<>();

    public static void render(PoseStack matrixStack) {
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        renders.entrySet().forEach(entry -> {
            entry.getValue().forEach(r -> {
                BlockPos pos = r.getKey();
                matrixStack.pushPose();
                matrixStack.translate(pos.getX() - projectedView.x, pos.getY() - projectedView.y, pos.getZ() - projectedView.z);
                r.getValue().accept(matrixStack, buffer);
                matrixStack.popPose();
            });
            RenderSystem.enableDepthTest();
//            buffer.endBatch(entry.getKey());
        });
        renders.clear();
//        RenderSystem.enableDepthTest();
//        buffer.endBatch(CustomRenderTypes.TRANSLUCENT_LIGHTNING_NOLIGHTMAPS);
//        buffer.endBatch(CustomRenderTypes.TRANSLUCENT_ADD_NOLIGHTMAPS);
    }

    public static void addRender(RenderType type, BlockPos pos, BiConsumer<PoseStack, MultiBufferSource> renderer) {
        renders.computeIfAbsent(type, renderType -> new ArrayList<>()).add(Pair.of(pos, renderer));
    }
}
