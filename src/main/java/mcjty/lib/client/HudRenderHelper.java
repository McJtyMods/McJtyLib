package mcjty.lib.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.stream.Collectors;

public class HudRenderHelper {

    public static void renderHudItems(List<Pair<ItemStack, String>> messages,
                                 HudPlacement hudPlacement,
                                 HudOrientation hudOrientation,
                                 Direction orientation,
                                 double x, double y, double z, float scale) {
        GlStateManager.pushMatrix();

        if (hudPlacement == HudPlacement.HUD_FRONT) {
            GlStateManager.translatef((float) x + 0.5F, (float) y + 0.75F, (float) z + 0.5F);
        } else if (hudPlacement == HudPlacement.HUD_CENTER) {
            GlStateManager.translatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        } else {
            GlStateManager.translatef((float) x + 0.5F, (float) y + 1.75F, (float) z + 0.5F);
        }

        switch (hudOrientation) {
            case HUD_SOUTH:
                GlStateManager.rotatef(-getHudAngle(orientation), 0.0F, 1.0F, 0.0F);
                break;
            case HUD_TOPLAYER_HORIZ:
                GlStateManager.rotatef(-Minecraft.getInstance().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotatef(180, 0.0F, 1.0F, 0.0F);
                break;
            case HUD_TOPLAYER:
                GlStateManager.rotatef(-Minecraft.getInstance().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotatef(Minecraft.getInstance().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotatef(180, 0.0F, 1.0F, 0.0F);
                break;
        }

        if (hudPlacement == HudPlacement.HUD_FRONT || hudPlacement == HudPlacement.HUD_ABOVE_FRONT) {
            GlStateManager.translatef(0.0F, -0.2500F, -0.4375F + .9f);
        } else if (hudPlacement != HudPlacement.HUD_CENTER){
            GlStateManager.translatef(0.0F, -0.2500F, -0.4375F + .4f);
        }

        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        Minecraft.getInstance().gameRenderer.disableLightmap();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();

        renderText(Minecraft.getInstance().fontRenderer, messages, 11, scale);
        Minecraft.getInstance().gameRenderer.enableLightmap();

//        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager.popMatrix();
    }

    public static void renderHud(List<String> messages,
                                 HudPlacement hudPlacement,
                                 HudOrientation hudOrientation,
                                 Direction orientation,
                                 double x, double y, double z, float scale) {
        renderHudItems(messages.stream().map(s -> Pair.of(ItemStack.EMPTY, s)).collect(Collectors.toList()),
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

    private static void renderText(FontRenderer fontrenderer, List<Pair<ItemStack, String>> messages, int lines, float scale) {
        GlStateManager.translatef(-0.5F, 0.5F, 0.07F);
        float f3 = 0.0075F;
        GlStateManager.scalef(f3 * scale, -f3 * scale, f3);
        GlStateManager.normal3f(0.0F, 0.0F, 1.0F);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        renderLog(fontrenderer, messages, lines);
    }

    private static void renderLog(FontRenderer fontrenderer, List<Pair<ItemStack, String>> messages, int lines) {
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
                        GlStateManager.pushMatrix();
                        GlStateManager.translatef(0, 0, -150);
                        ItemRenderer itemRender = Minecraft.getInstance().getItemRenderer();
                        itemRender.renderItemAndEffectIntoGUI(stack, 0, currenty);
                        prefix = "    ";
                        GlStateManager.popMatrix();
                    }

                    fontrenderer.drawString(fontrenderer.trimStringToWidth(prefix + s, 115), 7, currenty, 0xffffff);
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
