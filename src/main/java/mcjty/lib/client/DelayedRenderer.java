package mcjty.lib.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mcjty.lib.varia.TriConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * A delayed render can be used from within a BER (TER) in order to make it possible
 * to render translucent bits on top of everything else
 */
public class DelayedRenderer {

    // Renderers per render type
    private static final Map<RenderType, List<Pair<BlockPos, BiConsumer<PoseStack, VertexConsumer>>>> renders = new HashMap<>();

    // Global renderers
    private static final Map<BlockPos, TriConsumer<PoseStack, Vec3, RenderType>> delayedRenders = new HashMap<>();
    private static final Map<BlockPos, BiFunction<Level, BlockPos, Boolean>> renderValidations = new HashMap<>();

    public static void render(PoseStack matrixStack) {
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        Set<BlockPos> todelete = new HashSet<>();
        delayedRenders.forEach((pos, consumer) -> {
            if (renderValidations.getOrDefault(pos, (level, blockPos) -> false).apply(Minecraft.getInstance().level, pos)) {
                consumer.accept(matrixStack, projectedView, null);
            } else {
                todelete.add(pos);
            }
        });
        for (BlockPos pos : todelete) {
            delayedRenders.remove(pos);
            renderValidations.remove(pos);
        }

        renders.forEach((type, renderlist) -> {
            VertexConsumer consumer = buffer.getBuffer(type);
            renderlist.forEach(r -> {
                RenderSystem.enableDepthTest();
                BlockPos pos = r.getKey();
                matrixStack.pushPose();
                matrixStack.translate(pos.getX() - projectedView.x, pos.getY() - projectedView.y, pos.getZ() - projectedView.z);
                r.getValue().accept(matrixStack, consumer);
                matrixStack.popPose();
            });
            RenderSystem.enableDepthTest();
            buffer.endBatch(type);
        });
        renders.clear();

        buffer.endBatch();
    }

    public static void addRender(BlockPos pos, TriConsumer<PoseStack, Vec3, RenderType> renderer, BiFunction<Level, BlockPos, Boolean> validator) {
        delayedRenders.put(pos, renderer);
        renderValidations.put(pos, validator);
    }

    public static void removeRender(BlockPos pos) {
        delayedRenders.remove(pos);
        renderValidations.remove(pos);
    }

    public static void addRender(RenderType type, BlockPos pos, BiConsumer<PoseStack, VertexConsumer> renderer) {
        renders.computeIfAbsent(type, renderType -> new ArrayList<>()).add(Pair.of(pos, renderer));
    }
}
