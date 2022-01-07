package mcjty.lib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.stream.Collectors;

public class HudRenderHelper {

    public static void renderHudItems(PoseStack matrixStack, MultiBufferSource buffer, List<Pair<ItemStack, String>> messages,
                                      HudPlacement hudPlacement,
                                      HudOrientation hudOrientation,
                                      Direction orientation,
                                      double x, double y, double z, float scale) {
        matrixStack.pushPose();

        if (hudPlacement == HudPlacement.HUD_FRONT) {
            matrixStack.translate((float) x + 0.5F, (float) y + 0.75F, (float) z + 0.5F);
        } else if (hudPlacement == HudPlacement.HUD_CENTER) {
            matrixStack.translate((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        } else {
            matrixStack.translate((float) x + 0.5F, (float) y + 1.75F, (float) z + 0.5F);
        }

        Quaternion quaternion = Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation();
        switch (hudOrientation) {
            case HUD_SOUTH -> matrixStack.mulPose(new Quaternion(new Vector3f(0, 1, 0), -getHudAngle(orientation), true));
            case HUD_TOPLAYER_HORIZ ->
                    // @todo 1.15 change to correct quaternion? This is most likely not correct?
                    matrixStack.mulPose(quaternion);   // @todo 1.15 test

//                GlStateManager.rotatef(-Minecraft.getInstance().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
//                GlStateManager.rotatef(180, 0.0F, 1.0F, 0.0F);
            case HUD_TOPLAYER -> matrixStack.mulPose(quaternion);   // @todo 1.15 test

//                GlStateManager.rotatef(-Minecraft.getInstance().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
//                GlStateManager.rotatef(Minecraft.getInstance().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
//                GlStateManager.rotatef(180, 0.0F, 1.0F, 0.0F);
        }

        if (hudPlacement == HudPlacement.HUD_FRONT || hudPlacement == HudPlacement.HUD_ABOVE_FRONT) {
            matrixStack.translate(0.0F, -0.2500F, -0.4375F + .9f);
        } else if (hudPlacement != HudPlacement.HUD_CENTER){
            matrixStack.translate(0.0F, -0.2500F, -0.4375F + .4f);
        }

        renderText(matrixStack, buffer, Minecraft.getInstance().font, messages, 11, scale);

//        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
//        Minecraft.getInstance().gameRenderer.getLightTexture().disableLightmap();
//        GlStateManager.disableBlend();
//        GlStateManager.disableLighting();
//
//        Minecraft.getInstance().gameRenderer.getLightTexture().enableLightmap();
//
////        RenderHelper.enableStandardItemLighting();
//        GlStateManager.enableLighting();
//        GlStateManager.enableBlend();
//        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        matrixStack.popPose();
    }

    public static void renderHud(PoseStack stack, MultiBufferSource buffer, List<String> messages,
                                 HudPlacement hudPlacement,
                                 HudOrientation hudOrientation,
                                 Direction orientation,
                                 double x, double y, double z, float scale) {
        renderHudItems(stack, buffer, messages.stream().map(s -> Pair.of(ItemStack.EMPTY, s)).collect(Collectors.toList()),
                hudPlacement, hudOrientation, orientation, x, y, z, scale);
    }

    private static float getHudAngle(Direction orientation) {
        float f3 = 0.0f;

        if (orientation != null) {
            f3 = switch (orientation) {
                case NORTH -> 180.0F;
                case WEST -> 90.0F;
                case EAST -> -90.0F;
                default -> 0.0f;
            };
        }
        return f3;
    }

    private static void renderText(PoseStack matrixStack, MultiBufferSource buffer, Font fontrenderer, List<Pair<ItemStack, String>> messages, int lines, float scale) {
        matrixStack.translate(-0.5F, 0.5F, 0.07F);
        float f3 = 0.0075F;
        matrixStack.scale(f3 * scale, -f3 * scale, f3);
//        matrixStack.normal3f(0.0F, 0.0F, 1.0F);
//        matrixStack.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        renderLog(matrixStack, buffer, fontrenderer, messages, lines);
    }

    private static void renderLog(PoseStack matrixStack, MultiBufferSource buffer, Font fontrenderer, List<Pair<ItemStack, String>> messages, int lines) {
        int currenty = 7;
        int height = 10;
        int logsize = messages.size();
        int i = 0;
        for (Pair<ItemStack, String> pair : messages) {
            ItemStack stack = pair.getLeft();
            String s = pair.getRight();
            if (i >= logsize - lines) {
                // Check if this module has enough room
                if (currenty + height <= 124) {
                    String prefix = "";
                    if (!stack.isEmpty()) {
                        matrixStack.pushPose();
                        matrixStack.translate(14f, currenty +4f, 0);
                        matrixStack.scale(10, -10, 16);
//                        matrixStack.translate(0, 0, -150);
                        // @todo 1.15 this needs more work! we ignore 'currenty'!
                        ItemRenderer itemRender = Minecraft.getInstance().getItemRenderer();
                        BakedModel ibakedmodel = itemRender.getModel(stack, Minecraft.getInstance().level, null, 1);
                        itemRender.render(stack, ItemTransforms.TransformType.GUI, false, matrixStack, buffer, RenderHelper.MAX_BRIGHTNESS, OverlayTexture.NO_OVERLAY, ibakedmodel);

//                        itemRender.renderItemAndEffectIntoGUI(stack, 0, currenty);
                        prefix = "    ";
                        matrixStack.popPose();
                    }

                    fontrenderer.drawInBatch(fontrenderer.plainSubstrByWidth(prefix + s, 115), 7, currenty, 0xffffff, false, matrixStack.last().pose(), buffer, false, 0, RenderHelper.MAX_BRIGHTNESS);
//                    fontrenderer.drawString(fontrenderer.trimStringToWidth(prefix + s, 115), 7, currenty, 0xffffff);
                    currenty += height;
                }
            }
            i++;
        }
    }

    public static enum HudPlacement {
        HUD_ABOVE,
        HUD_ABOVE_FRONT,
        HUD_FRONT,
        HUD_CENTER
    }

    public static enum HudOrientation {
        HUD_SOUTH,
        HUD_TOPLAYER_HORIZ,
        HUD_TOPLAYER
    }
}
