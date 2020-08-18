package mcjty.lib.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.stream.Collectors;

public class HudRenderHelper {

    public static void renderHudItems(MatrixStack matrixStack, IRenderTypeBuffer buffer, List<Pair<ItemStack, String>> messages,
                                      HudPlacement hudPlacement,
                                      HudOrientation hudOrientation,
                                      Direction orientation,
                                      double x, double y, double z, float scale) {
        matrixStack.push();

        if (hudPlacement == HudPlacement.HUD_FRONT) {
            matrixStack.translate((float) x + 0.5F, (float) y + 0.75F, (float) z + 0.5F);
        } else if (hudPlacement == HudPlacement.HUD_CENTER) {
            matrixStack.translate((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        } else {
            matrixStack.translate((float) x + 0.5F, (float) y + 1.75F, (float) z + 0.5F);
        }

        Quaternion quaternion = Minecraft.getInstance().getRenderManager().getCameraOrientation();
        switch (hudOrientation) {
            case HUD_SOUTH:
                matrixStack.rotate(new Quaternion(new Vector3f(0, 1, 0), -getHudAngle(orientation), true));
                break;
            case HUD_TOPLAYER_HORIZ:
                // @todo 1.15 change to correct quaternion? This is most likely not correct?
                matrixStack.rotate(quaternion);   // @todo 1.15 test
//                GlStateManager.rotatef(-Minecraft.getInstance().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
//                GlStateManager.rotatef(180, 0.0F, 1.0F, 0.0F);
                break;
            case HUD_TOPLAYER:
                matrixStack.rotate(quaternion);   // @todo 1.15 test
//                GlStateManager.rotatef(-Minecraft.getInstance().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
//                GlStateManager.rotatef(Minecraft.getInstance().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
//                GlStateManager.rotatef(180, 0.0F, 1.0F, 0.0F);
                break;
        }

        if (hudPlacement == HudPlacement.HUD_FRONT || hudPlacement == HudPlacement.HUD_ABOVE_FRONT) {
            matrixStack.translate(0.0F, -0.2500F, -0.4375F + .9f);
        } else if (hudPlacement != HudPlacement.HUD_CENTER){
            matrixStack.translate(0.0F, -0.2500F, -0.4375F + .4f);
        }

        renderText(matrixStack, buffer, Minecraft.getInstance().fontRenderer, messages, 11, scale);

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

        matrixStack.pop();
    }

    public static void renderHud(MatrixStack stack, IRenderTypeBuffer buffer, List<String> messages,
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
            switch (orientation) {
                case NORTH:
                    f3 = 180.0F;
                    break;
                case WEST:
                    f3 = 90.0F;
                    break;
                case EAST:
                    f3 = -90.0F;
                    break;
                default:
                    f3 = 0.0f;
            }
        }
        return f3;
    }

    private static void renderText(MatrixStack matrixStack, IRenderTypeBuffer buffer, FontRenderer fontrenderer, List<Pair<ItemStack, String>> messages, int lines, float scale) {
        matrixStack.translate(-0.5F, 0.5F, 0.07F);
        float f3 = 0.0075F;
        matrixStack.scale(f3 * scale, -f3 * scale, f3);
//        matrixStack.normal3f(0.0F, 0.0F, 1.0F);
//        matrixStack.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        renderLog(matrixStack, buffer, fontrenderer, messages, lines);
    }

    private static void renderLog(MatrixStack matrixStack, IRenderTypeBuffer buffer, FontRenderer fontrenderer, List<Pair<ItemStack, String>> messages, int lines) {
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
                        matrixStack.push();
                        matrixStack.translate((float)14f, (float)currenty+4f, 0);
                        matrixStack.scale(10, -10, 16);
//                        matrixStack.translate(0, 0, -150);
                        // @todo 1.15 this needs more work! we ignore 'currenty'!
                        ItemRenderer itemRender = Minecraft.getInstance().getItemRenderer();
                        IBakedModel ibakedmodel = itemRender.getItemModelWithOverrides(stack, Minecraft.getInstance().world, (LivingEntity)null);
                        itemRender.renderItem(stack, ItemCameraTransforms.TransformType.GUI, false, matrixStack, buffer, 0xf000f0, OverlayTexture.NO_OVERLAY, ibakedmodel);

//                        itemRender.renderItemAndEffectIntoGUI(stack, 0, currenty);
                        prefix = "    ";
                        matrixStack.pop();
                    }

                    fontrenderer.renderString(fontrenderer.func_238412_a_(prefix + s, 115), 7, currenty, 0xffffff, false, matrixStack.getLast().getMatrix(), buffer, false, 0, 140);
//                    fontrenderer.drawString(fontrenderer.func_238412_a_(prefix + s, 115), 7, currenty, 0xffffff);
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
