package mcjty.lib.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * A delayed render can be used from within a BER (TER) in order to make it possible
 * to render translucent bits on top of everything else
 */
public class DelayedRenderer {

    private static final List<Pair<BlockPos, BiConsumer<MatrixStack, IRenderTypeBuffer>>> renders = new ArrayList<>();

    public static void render(MatrixStack matrixStack) {
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        Vector3d projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
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

    public static void addRender(BlockPos pos, BiConsumer<MatrixStack, IRenderTypeBuffer> renderer) {
        renders.add(Pair.of(pos, renderer));
    }
}
