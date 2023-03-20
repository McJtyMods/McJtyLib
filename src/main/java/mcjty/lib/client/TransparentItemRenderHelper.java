package mcjty.lib.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

// Code borrowed from XFactHD
public final class TransparentItemRenderHelper {

    private TransparentItemRenderHelper() {
    }

    private static final RenderType TRANSLUCENT = RenderType.entityTranslucentCull(TextureAtlas.LOCATION_BLOCKS);

    public static void renderItemWithAlpha(PoseStack poseStack, ItemStack stack, int x, int y, int alpha) {
        if (stack.isEmpty()) {
            return;
        }

        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();

        BakedModel model = renderer.getModel(stack, null, Minecraft.getInstance().player, 0);
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, 50);
        renderItemModelWithAlpha(poseStack, stack, x, y, alpha, model, renderer);
        poseStack.popPose();
    }

    /**
     * {@link ItemRenderer::renderGuiItem} but with alpha
     */
    public static void renderItemModelWithAlpha(PoseStack poseStack, ItemStack stack, int x, int y, int alpha, BakedModel model, ItemRenderer renderer) {
        Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);

        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // @todo 1.19.4 use this or RenderSystem.getModelViewStack()?
        PoseStack modelViewStack = poseStack;
        modelViewStack.pushPose();
        modelViewStack.translate(x, y, 100.0F);//@todo 1.19.4 + renderer.blitOffset);
        modelViewStack.translate(8.0D, 8.0D, 0.0D);
        modelViewStack.scale(1.0F, -1.0F, 1.0F);
        modelViewStack.scale(16.0F, 16.0F, 16.0F);
        RenderSystem.applyModelViewMatrix();

        boolean flatLight = !model.usesBlockLight();
        if (flatLight) {
            Lighting.setupForFlatItems();
        }

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        renderer.render(
                stack,
                ItemDisplayContext.GUI,
                false,
                new PoseStack(),
                wrapBuffer(buffer, alpha, alpha < 255),
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                model
        );
        buffer.endBatch();

        RenderSystem.enableDepthTest();

        if (flatLight) {
            Lighting.setupFor3DItems();
        }

        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    private static MultiBufferSource wrapBuffer(MultiBufferSource buffer, int alpha, boolean forceTranslucent) {
        return renderType -> new GhostVertexConsumer(buffer.getBuffer(forceTranslucent ? TRANSLUCENT : renderType), alpha);
    }


    public record GhostVertexConsumer(VertexConsumer wrapped, int alpha) implements VertexConsumer {
        @Override
        public VertexConsumer vertex(double x, double y, double z) {
            return wrapped.vertex(x, y, z);
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            return wrapped.color(red, green, blue, (alpha * this.alpha) / 0xFF);
        }

        @Override
        public VertexConsumer uv(float u, float v) {
            return wrapped.uv(u, v);
        }

        @Override
        public VertexConsumer overlayCoords(int u, int v) {
            return wrapped.overlayCoords(u, v);
        }

        @Override
        public VertexConsumer uv2(int u, int v) {
            return wrapped.uv2(u, v);
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            return wrapped.normal(x, y, z);
        }

        @Override
        public void endVertex() {
            wrapped.endVertex();
        }

        @Override
        public void defaultColor(int r, int g, int b, int a) {
            wrapped.defaultColor(r, g, b, a);
        }

        @Override
        public void unsetDefaultColor() {
            wrapped.unsetDefaultColor();
        }
    }
}
