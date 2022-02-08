package mcjty.lib.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * A delayed render can be used from within a BER (TER) in order to make it possible
 * to render translucent bits on top of everything else
 */
public class DelayedRenderer {

    private static final Map<RenderType, List<Pair<BlockPos, BiConsumer<PoseStack, VertexConsumer>>>> renders = new HashMap<>();

    private static final ChunkBufferBuilderPack fixedBufferPack = new ChunkBufferBuilderPack();
    private static final SortedMap<RenderType, BufferBuilder> fixedBuffers = Util.make(new Object2ObjectLinkedOpenHashMap<>(), (p_110100_) -> {
        p_110100_.put(Sheets.solidBlockSheet(), fixedBufferPack.builder(RenderType.solid()));
//        p_110100_.put(Sheets.cutoutBlockSheet(), this.fixedBufferPack.builder(RenderType.cutout()));
//        p_110100_.put(Sheets.bannerSheet(), this.fixedBufferPack.builder(RenderType.cutoutMipped()));
//        p_110100_.put(Sheets.translucentCullBlockSheet(), this.fixedBufferPack.builder(RenderType.translucent()));
        put(p_110100_, Sheets.shieldSheet());
        put(p_110100_, Sheets.bedSheet());
        put(p_110100_, Sheets.shulkerBoxSheet());
        put(p_110100_, Sheets.signSheet());
        put(p_110100_, Sheets.chestSheet());
        put(p_110100_, RenderType.translucentNoCrumbling());
        put(p_110100_, RenderType.armorGlint());
        put(p_110100_, RenderType.armorEntityGlint());
        put(p_110100_, RenderType.glint());
        put(p_110100_, RenderType.glintDirect());
        put(p_110100_, RenderType.glintTranslucent());
        put(p_110100_, RenderType.entityGlint());
        put(p_110100_, RenderType.entityGlintDirect());
        put(p_110100_, RenderType.waterMask());
        put(p_110100_, CustomRenderTypes.TRANSLUCENT_LIGHTNING_NOLIGHTMAPS);
        put(p_110100_, CustomRenderTypes.TRANSLUCENT_ADD_NOLIGHTMAPS);
        put(p_110100_, CustomRenderTypes.TRANSLUCENT_ADD);
        put(p_110100_, CustomRenderTypes.OVERLAY_LINES);
        ModelBakery.DESTROY_TYPES.forEach((p_173062_) -> {
            put(p_110100_, p_173062_);
        });
    });

    private static void put(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> pMapBuilders, RenderType pRenderType) {
        pMapBuilders.put(pRenderType, new BufferBuilder(pRenderType.bufferSize()));
    }

    private static final MultiBufferSource.BufferSource buffer = MultiBufferSource.immediateWithBuffers(fixedBuffers, Tesselator.getInstance().getBuilder());

    public static void render(PoseStack matrixStack) {
//        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        renders.forEach((type, renderlist) -> {
            buffer.endLastBatch();
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

    public static void addRender(RenderType type, BlockPos pos, BiConsumer<PoseStack, VertexConsumer> renderer) {
        renders.computeIfAbsent(type, renderType -> new ArrayList<>()).add(Pair.of(pos, renderer));
    }
}
