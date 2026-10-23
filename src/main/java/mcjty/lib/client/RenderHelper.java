package mcjty.lib.client;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.core.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import mcjty.lib.base.StyleConfig;
import javax.annotation.Nonnull;

public class RenderHelper {
    public static final int MAX_BRIGHTNESS = LightCoordsUtil.FULL_BRIGHT;
    public static final RenderSettings DEFAULT_SETTINGS = RenderSettings.builder().build();
    public static final RenderSettings FULLBRIGHT_SETTINGS = RenderSettings.builder().brightness(MAX_BRIGHTNESS).build();
    public enum FaceRotation { ZERO, CLOCKWISE_90, UPSIDE_DOWN, COUNTERCLOCKWISE_90 }

    public static void renderAndDecorateItem(GuiGraphicsExtractor graphics, ItemStack stack, int x, int y) {
        graphics.item(stack, x, y);
        graphics.itemDecorations(Minecraft.getInstance().font, stack, x, y);
    }

    private static void submitItem(PoseStack pose, SubmitNodeCollector collector, ItemStack stack, ItemDisplayContext context, int light, int overlay) {
        var mc = Minecraft.getInstance();
        var state = new ItemStackRenderState();
        mc.getItemModelResolver().updateForTopItem(state, stack, context, mc.level, mc.player, 0);
        state.submit(pose, collector, light, overlay, 0);
    }
    public static void renderItemGround(PoseStack pose, SubmitNodeCollector collector, ItemStack stack, int light, int overlay) {
        submitItem(pose, collector, stack, ItemDisplayContext.GROUND, light, overlay);
    }
    public static void renderItemGui(PoseStack pose, SubmitNodeCollector collector, ItemStack stack, int light, int overlay) {
        submitItem(pose, collector, stack, ItemDisplayContext.GUI, light, overlay);
    }
    public static void renderStaticFixed(PoseStack pose, SubmitNodeCollector collector, ItemStack stack, int light, int overlay) {
        submitItem(pose, collector, stack, ItemDisplayContext.FIXED, light, overlay);
    }
    public static void renderText(Font font, String text, int x, int y, int color, PoseStack pose, SubmitNodeCollector collector, int light) {
        pose.pushPose();
        pose.translate(x, y, 0);
        collector.submitText(pose, 0, 0, net.minecraft.util.FormattedCharSequence.forward(text, net.minecraft.network.chat.Style.EMPTY), false, Font.DisplayMode.NORMAL, light, color, 0, 0);
        pose.popPose();
    }
    public static boolean renderObject(GuiGraphicsExtractor graphics, int x, int y, Object object, boolean highlight) {
        return renderObject(graphics, Minecraft.getInstance().getItemModelResolver(), x, y, object, highlight, 0);
    }
    public static boolean renderObject(GuiGraphicsExtractor graphics, ItemModelResolver resolver, int x, int y, Object object, boolean highlight, float level) {
        if (object instanceof ItemStack stack) return renderItemStackWithCount(graphics, resolver, stack, x, y, highlight);
        if (object instanceof Item item) return renderObject(graphics, x, y, new ItemStack(item), highlight);
        if (object instanceof Block block) return renderObject(graphics, x, y, new ItemStack(block), highlight);
        if (object instanceof FluidStack fluid) return renderFluidStack(graphics, fluid, x, y, highlight);
        if (object instanceof TextureAtlasSprite sprite) {
            graphics.blitSprite(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, sprite, x, y, 16, 16);
            return true;
        }
        if (object instanceof Entity entity) {
            var state = Minecraft.getInstance().getEntityRenderDispatcher().extractEntity(entity, 1);
            graphics.entity(state, 16, new Vector3f(0, state.boundingBoxHeight / 2, 0), new Quaternionf().rotateZ((float)Math.PI), null, x, y, x + 16, y + 16);
            return true;
        }
        return false;
    }
    public static boolean renderItemStackWithCount(GuiGraphicsExtractor graphics, ItemModelResolver resolver, ItemStack stack, int x, int y, boolean highlight) {
        int count = stack.getCount();
        String text = count <= 1 ? "" : count < 100000 ? Integer.toString(count) : count < 1000000 ? count / 1000 + "k" : count < 1000000000 ? count / 1000000 + "m" : count / 1000000000 + "g";
        return renderItemStack(graphics, resolver, stack, x, y, text, highlight);
    }
    public static boolean renderItemStack(GuiGraphicsExtractor graphics, ItemModelResolver resolver, ItemStack stack, int x, int y, String text, boolean highlight) {
        if (stack.isEmpty()) return false;
        if (highlight) graphics.fillGradient(x, y, x + 16, y + 16, 0x80ffffff, 0xffffffff);
        graphics.item(stack, x, y);
        graphics.itemDecorations(Minecraft.getInstance().font, stack, x, y, text);
        return true;
    }
    public static boolean renderFluidStack(GuiGraphicsExtractor graphics, FluidStack fluid, int x, int y, boolean highlight) {
        if (fluid.isEmpty()) return false;
        var model = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluid.getFluid().defaultFluidState());
        int color = model.fluidTintSource() == null ? 0xffffffff : model.fluidTintSource().colorAsStack(fluid);
        graphics.blitSprite(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, model.stillMaterial().sprite(), x, y, 16, 16, color);
        return true;
    }
    public static int renderText(GuiGraphicsExtractor graphics, int x, int y, String text) { return renderText(graphics, x, y, text, 0xffffffff); }
    public static int renderText(GuiGraphicsExtractor graphics, int x, int y, String text, int color) {
        var font = Minecraft.getInstance().font;
        graphics.text(font, text, x, y, color | 0xff000000);
        return font.width(text);
    }
    public static void drawVerticalGradientRect(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int color1, int color2) {
        graphics.fillGradient(x1, y1, x2, y2, color1, color2);
    }
    public static void drawHorizontalGradientRect(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int color1, int color2) {
        int width = x2 - x1;
        for (int x = 0; x < width; x++) {
            float t = width <= 1 ? 0 : (float)x / (width - 1);
            int color = 0;
            for (int shift = 0; shift <= 24; shift += 8) {
                int a = (color1 >>> shift) & 255, b = (color2 >>> shift) & 255;
                color |= Math.round(a + (b - a) * t) << shift;
            }
            graphics.fill(x1 + x, y1, x1 + x + 1, y2, color);
        }
    }
    public static void line(VertexConsumer builder, PoseStack matrixStack, float x1, float y1, float z1, float x2, float y2, float z2,
                            float red, float green, float blue, float alpha) {
        drawLine3d(builder, matrixStack, new Vec3(x1, y1, z1), new Vec3(x2, y2, z2), red, green, blue, alpha);

    }

    // Adjust the given matrix to the specified direction
    public static void adjustTransformToDirection(PoseStack matrixStack, Direction facing) {
        matrixStack.translate(0.5F, 0.5F, 0.5F);

        switch (facing) {
            case DOWN -> {
                matrixStack.rotate(Axis.XP.rotationDegrees(90.0F));
            }
            case UP -> {
                matrixStack.rotate(Axis.XP.rotationDegrees(-90.0F));
            }
            case NORTH -> {
                matrixStack.rotate(Axis.YP.rotationDegrees(-180.0F));
            }
            case SOUTH -> {
            }
            case WEST -> {
                matrixStack.rotate(Axis.YP.rotationDegrees(-90.0F));
            }
            case EAST -> {
                matrixStack.rotate(Axis.YP.rotationDegrees(90.0F));
            }
        }

        matrixStack.translate(-0.5F, -0.5F, -0.5F);
    }

    public static void renderNorthSouthQuad(PoseStack poseStack, VertexConsumer builder, TextureAtlasSprite sprite, FaceRotation rotation, float offset) {
        Matrix4f matrix = poseStack.last().pose();
        switch (rotation) {
            case ZERO -> {
                RenderHelper.vt(builder, matrix, 0, 1, .73f, sprite.getU0(), sprite.getV1());
                RenderHelper.vt(builder, matrix, 1, 1, .73f, sprite.getU1(), sprite.getV1());
                RenderHelper.vt(builder, matrix, 1, 0, .73f, sprite.getU1(), sprite.getV0());
                RenderHelper.vt(builder, matrix, 0, 0, .73f, sprite.getU0(), sprite.getV0());
            }
            case CLOCKWISE_90 -> {
                RenderHelper.vt(builder, matrix, 0, 1, .73f, sprite.getU1(), sprite.getV0());
                RenderHelper.vt(builder, matrix, 1, 1, .73f, sprite.getU0(), sprite.getV0());
                RenderHelper.vt(builder, matrix, 1, 0, .73f, sprite.getU0(), sprite.getV1());
                RenderHelper.vt(builder, matrix, 0, 0, .73f, sprite.getU1(), sprite.getV1());
            }
            case UPSIDE_DOWN -> {
                RenderHelper.vt(builder, matrix, 0, 1, .73f, sprite.getU0(), sprite.getV0());
                RenderHelper.vt(builder, matrix, 1, 1, .73f, sprite.getU0(), sprite.getV1());
                RenderHelper.vt(builder, matrix, 1, 0, .73f, sprite.getU1(), sprite.getV1());
                RenderHelper.vt(builder, matrix, 0, 0, .73f, sprite.getU1(), sprite.getV0());
            }
            case COUNTERCLOCKWISE_90 -> {
                RenderHelper.vt(builder, matrix, 0, 1, .73f, sprite.getU1(), sprite.getV1());
                RenderHelper.vt(builder, matrix, 1, 1, .73f, sprite.getU1(), sprite.getV0());
                RenderHelper.vt(builder, matrix, 1, 0, .73f, sprite.getU0(), sprite.getV0());
                RenderHelper.vt(builder, matrix, 0, 0, .73f, sprite.getU0(), sprite.getV1());
            }
        }
    }

    public static void drawHorizontalLine(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int color) {
        graphics.fill(x1, y1, x2, y1 + 1, color);
    }

    public static void drawVerticalLine(GuiGraphicsExtractor graphics, int x1, int y1, int y2, int color) {
        graphics.fill(x1, y1, x1 + 1, y2, color);
    }

    // Draw a small triangle. x,y is the coordinate of the left point
    public static void drawLeftTriangle(GuiGraphicsExtractor graphics, int x, int y, int color) {
        drawVerticalLine(graphics, x, y, y, color);
        drawVerticalLine(graphics, x + 1, y - 1, y + 1, color);
        drawVerticalLine(graphics, x + 2, y - 2, y + 2, color);
    }

    // Draw a small triangle. x,y is the coordinate of the right point
    public static void drawRightTriangle(GuiGraphicsExtractor graphics, int x, int y, int color) {
        drawVerticalLine(graphics, x, y, y, color);
        drawVerticalLine(graphics, x - 1, y - 1, y + 1, color);
        drawVerticalLine(graphics, x - 2, y - 2, y + 2, color);
    }

    // Draw a small triangle. x,y is the coordinate of the top point
    public static void drawUpTriangle(GuiGraphicsExtractor graphics, int x, int y, int color) {
        drawHorizontalLine(graphics, x, y, x, color);
        drawHorizontalLine(graphics, x - 1, y + 1, x + 1, color);
        drawHorizontalLine(graphics, x - 2, y + 2, x + 2, color);
    }

    // Draw a small triangle. x,y is the coordinate of the bottom point
    public static void drawDownTriangle(GuiGraphicsExtractor graphics, int x, int y, int color) {
        drawHorizontalLine(graphics, x, y, x, color);
        drawHorizontalLine(graphics, x - 1, y - 1, x + 1, color);
        drawHorizontalLine(graphics, x - 2, y - 2, x + 2, color);
    }

    public static void drawThickButtonBox(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int bright, int average, int dark) {
        graphics.fill(x1 + 2, y1 + 2, x2 - 2, y2 - 2, average);
        drawHorizontalLine(graphics, x1 + 1, y1, x2 - 1, StyleConfig.colorButtonExternalBorder);
        drawHorizontalLine(graphics, x1 + 1, y2 - 1, x2 - 1, StyleConfig.colorButtonExternalBorder);
        drawVerticalLine(graphics, x1, y1 + 1, y2 - 1, StyleConfig.colorButtonExternalBorder);
        drawVerticalLine(graphics, x2 - 1, y1 + 1, y2 - 1, StyleConfig.colorButtonExternalBorder);

        drawHorizontalLine(graphics, x1 + 1, y1 + 1, x2 - 1, bright);
        drawHorizontalLine(graphics, x1 + 2, y1 + 2, x2 - 2, bright);
        drawVerticalLine(graphics, x1 + 1, y1 + 2, y2 - 2, bright);
        drawVerticalLine(graphics, x1 + 2, y1 + 3, y2 - 3, bright);

        drawHorizontalLine(graphics, x1 + 3, y2 - 3, x2 - 2, dark);
        drawHorizontalLine(graphics, x1 + 2, y2 - 2, x2 - 1, dark);
        drawVerticalLine(graphics, x2 - 2, y1 + 2, y2 - 2, dark);
        drawVerticalLine(graphics, x2 - 3, y1 + 3, y2 - 3, dark);
    }

    /**
     * Draw a button box. x2 and y2 are not included.
     */
    public static void drawThinButtonBox(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int bright, int average, int dark) {
        graphics.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, average);
        drawHorizontalLine(graphics, x1 + 1, y1, x2 - 1, StyleConfig.colorButtonExternalBorder);
        drawHorizontalLine(graphics, x1 + 1, y2 - 1, x2 - 1, StyleConfig.colorButtonExternalBorder);
        drawVerticalLine(graphics, x1, y1 + 1, y2 - 1, StyleConfig.colorButtonExternalBorder);
        drawVerticalLine(graphics, x2 - 1, y1 + 1, y2 - 1, StyleConfig.colorButtonExternalBorder);

        drawHorizontalLine(graphics, x1 + 1, y1 + 1, x2 - 2, bright);
        drawVerticalLine(graphics, x1 + 1, y1 + 2, y2 - 3, bright);

        drawHorizontalLine(graphics, x1 + 1, y2 - 2, x2 - 1, dark);
        drawVerticalLine(graphics, x2 - 2, y1 + 1, y2 - 2, dark);
    }

    /**
     * Draw a button box. x2 and y2 are not included.
     */
    public static void drawThinButtonBoxGradient(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int bright, int average1, int average2, int dark) {
        drawVerticalGradientRect(graphics, x1 + 1, y1 + 1, x2 - 1, y2 - 1, average2, average1);
        drawHorizontalLine(graphics, x1 + 1, y1, x2 - 1, StyleConfig.colorButtonExternalBorder);
        drawHorizontalLine(graphics, x1 + 1, y2 - 1, x2 - 1, StyleConfig.colorButtonExternalBorder);
        drawVerticalLine(graphics, x1, y1 + 1, y2 - 1, StyleConfig.colorButtonExternalBorder);
        drawVerticalLine(graphics, x2 - 1, y1 + 1, y2 - 1, StyleConfig.colorButtonExternalBorder);

        drawHorizontalLine(graphics, x1 + 1, y1 + 1, x2 - 2, bright);
        drawVerticalLine(graphics, x1 + 1, y1 + 2, y2 - 3, bright);

        drawHorizontalLine(graphics, x1 + 1, y2 - 2, x2 - 1, dark);
        drawVerticalLine(graphics, x2 - 2, y1 + 1, y2 - 2, dark);
    }

    /**
     * Draw a button box. x2 and y2 are not included.
     */
    public static void drawFlatButtonBox(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int bright, int average, int dark) {
        drawBeveledBox(graphics, x1, y1, x2, y2, bright, dark, average);
    }

    /**
     * Draw a button box. x2 and y2 are not included.
     */
    public static void drawFlatButtonBoxGradient(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int bright, int average1, int average2, int dark) {
        drawVerticalGradientRect(graphics, x1 + 1, y1 + 1, x2 - 1, y2 - 1, average2, average1);
        drawHorizontalLine(graphics, x1, y1, x2 - 1, bright);
        drawVerticalLine(graphics, x1, y1, y2 - 1, bright);
        drawVerticalLine(graphics, x2 - 1, y1, y2 - 1, dark);
        drawHorizontalLine(graphics, x1, y2 - 1, x2, dark);
    }

    /**
     * Draw a beveled box. x2 and y2 are not included.
     */
    public static void drawBeveledBox(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int topleftcolor, int botrightcolor, int fillcolor) {
        if (fillcolor != -1) {
            graphics.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, fillcolor);
        }
        drawHorizontalLine(graphics, x1, y1, x2 - 1, topleftcolor);
        drawVerticalLine(graphics, x1, y1, y2 - 1, topleftcolor);
        drawVerticalLine(graphics, x2 - 1, y1, y2 - 1, botrightcolor);
        drawHorizontalLine(graphics, x1, y2 - 1, x2, botrightcolor);
    }

    /**
     * Draw a beveled box. x2 and y2 are not included. Use this version for GUI's
     */
    public static void drawThickBeveledBox(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int thickness, int topleftcolor, int botrightcolor, int fillcolor) {
        if (fillcolor != -1) {
            graphics.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, fillcolor);
        }
        graphics.fill(x1, y1, x2 - 1, y1 + thickness, topleftcolor);
        graphics.fill(x1, y1, x1 + thickness, y2 - 1, topleftcolor);
        graphics.fill(x2 - thickness, y1, x2, y2 - 1, botrightcolor);
        graphics.fill(x1, y2 - thickness, x2, y2, botrightcolor);
    }

    /**
     * Draw a beveled box. x2 and y2 are not included.
     */
    public static void drawFlatBox(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int border, int fill) {
        if (fill != -1) {
            graphics.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, fill);
        }
        drawHorizontalLine(graphics, x1, y1, x2 - 1, border);
        drawVerticalLine(graphics, x1, y1, y2 - 1, border);
        drawVerticalLine(graphics, x2 - 1, y1, y2 - 1, border);
        drawHorizontalLine(graphics, x1, y2 - 1, x2, border);
    }

    public static void drawTexturedModalRect(PoseStack poseStack, VertexConsumer builder, int x, int y, int textureX, int textureY, int width, int height, int totw, int toth, float parentU, float parentV) {
        Matrix4f matrix = poseStack.last().pose();
        float f = 1.0f / totw;
        float f1 = 1.0f / toth;
        float zLevel = 50;
        builder.addVertex(matrix, (x + 0), (y + height), zLevel).setUv(parentU + ((textureX + 0) * f), parentV + ((textureY + height) * f1));
        builder.addVertex(matrix, (x + width), (y + height), zLevel).setUv(parentU + ((textureX + width) * f), parentV + ((textureY + height) * f1));
        builder.addVertex(matrix, (x + width), (y + 0), zLevel).setUv(parentU + ((textureX + width) * f), parentV + ((textureY + 0) * f1));
        builder.addVertex(matrix, (x + 0), (y + 0), zLevel).setUv(parentU + ((textureX + 0) * f), parentV + ((textureY + 0) * f1));
    }


    /**
     * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
     */
    public static void drawBeam(PoseStack matrix, VertexConsumer builder, TextureAtlasSprite sprite, Vec3 S, Vec3 E, Vec3 P, float width) {
        Vec3 PS = S.subtract(P);
        Vec3 SE = E.subtract(S);

        Vec3 normal = PS.cross(SE).normalize();

        Vec3 half = normal.multiply(width, width, width);
        Vec3 p1 = S.add(half);
        Vec3 p2 = S.subtract(half);
        Vec3 p3 = E.add(half);
        Vec3 p4 = E.subtract(half);

        drawQuad(matrix.last().pose(), builder, sprite, p1, p3, p4, p2, DEFAULT_SETTINGS);
    }

    /**
     * Draw a beam with some thickness.
     */
    public static void drawBeam(PoseStack poseStack, VertexConsumer buffer, TextureAtlasSprite sprite, Vec3 S, Vec3 E, Vec3 P, RenderSettings settings) {
        Vec3 PS = S.subtract(P);
        Vec3 SE = E.subtract(S);

        Vec3 normal = PS.cross(SE).normalize();

        Vec3 half = normal.multiply(settings.width(), settings.width(), settings.width());
        Vec3 p1 = S.add(half);
        Vec3 p2 = S.subtract(half);
        Vec3 p3 = E.add(half);
        Vec3 p4 = E.subtract(half);

        drawQuad(poseStack.last().pose(), buffer, sprite, p1, p3, p4, p2, settings);
    }

    public static void renderQuadGui(PoseStack matrixStack, TextureAtlasSprite sprite, int packedLight, VertexConsumer builder, float zfront, float size) {
        float u0 = sprite.getU0();
        float v0 = sprite.getV0();
        float u1 = sprite.getU1();
        float v1 = sprite.getV1();

        Matrix4f matrix = matrixStack.last().pose();
        vt(builder, matrix, -size, size, zfront, u0, v0, packedLight);
        vt(builder, matrix, size, size, zfront, u1, v0, packedLight);
        vt(builder, matrix, size, -size, zfront, u1, v1, packedLight);
        vt(builder, matrix, -size, -size, zfront, u0, v1, packedLight);
    }

    public static void drawQuadGui(PoseStack poseStack, VertexConsumer builder,
                                   float x1, float x2, float y1, float y2, float z,
                                   int color, int packedLightIn) {
        Matrix4f matrix = poseStack.last().pose();
        float a = (color >> 24 & 255) / 255.0F;
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;

        builder.addVertex(matrix, x1, y2, z).setColor(r, g, b, a).setLight(packedLightIn);
        builder.addVertex(matrix, x2, y2, z).setColor(r, g, b, a).setLight(packedLightIn);
        builder.addVertex(matrix, x2, y1, z).setColor(r, g, b, a).setLight(packedLightIn);
        builder.addVertex(matrix, x1, y1, z).setColor(r, g, b, a).setLight(packedLightIn);
    }

    private static void drawQuad(Matrix4f matrix, VertexConsumer buffer, TextureAtlasSprite sprite, Vec3 p1, Vec3 p2, Vec3 p3, Vec3 p4,
                                 RenderSettings settings) {
        int b1 = settings.brightness() >> 16 & 65535;
        int b2 = settings.brightness() & 65535;

        vt(buffer, matrix, (float) p1.x(), (float) p1.y(), (float) p1.z(), sprite.getU0(), sprite.getV0(), b1, b2, settings.r(), settings.g(), settings.b(), settings.a());
        vt(buffer, matrix, (float) p2.x(), (float) p2.y(), (float) p2.z(), sprite.getU1(), sprite.getV0(), b1, b2, settings.r(), settings.g(), settings.b(), settings.a());
        vt(buffer, matrix, (float) p3.x(), (float) p3.y(), (float) p3.z(), sprite.getU1(), sprite.getV1(), b1, b2, settings.r(), settings.g(), settings.b(), settings.a());
        vt(buffer, matrix, (float) p4.x(), (float) p4.y(), (float) p4.z(), sprite.getU0(), sprite.getV1(), b1, b2, settings.r(), settings.g(), settings.b(), settings.a());
    }

    private static void drawQuadUnit(Matrix4f matrix, VertexConsumer buffer, TextureAtlasSprite sprite, Vec3 p1, Vec3 p2, Vec3 p3, Vec3 p4,
                                     double u0Par, double u1Par,
                                     double v0Par, double v1Par,
                                     RenderSettings settings) {
        int b1 = settings.brightness() >> 16 & 65535;
        int b2 = settings.brightness() & 65535;
        u0Par = u0Par < 0 ? (1.0 + (u0Par % 1.0)) : u0Par % 1.0;
        u1Par = u1Par < 0 ? (-(u1Par % 1.0)) : (1.0 - (u1Par % 1.0));
        v0Par = v0Par < 0 ? (1.0 + (v0Par % 1.0)) : v0Par % 1.0;
        v1Par = v1Par < 0 ? (-(v1Par % 1.0)) : (1.0 - (v1Par % 1.0));
        float du = sprite.getU1() - sprite.getU0();
        float dv = sprite.getV1() - sprite.getV0();

        vt(buffer, matrix, (float) p1.x(), (float) p1.y(), (float) p1.z(), sprite.getU0() + (float) (du * u0Par), sprite.getV0() + (float) (dv * v0Par), b1, b2, settings.r(), settings.g(), settings.b(), settings.a());
        vt(buffer, matrix, (float) p2.x(), (float) p2.y(), (float) p2.z(), sprite.getU1() - (float) (du * u1Par), sprite.getV0() + (float) (dv * v0Par), b1, b2, settings.r(), settings.g(), settings.b(), settings.a());
        vt(buffer, matrix, (float) p3.x(), (float) p3.y(), (float) p3.z(), sprite.getU1() - (float) (du * u1Par), sprite.getV1() - (float) (dv * v1Par), b1, b2, settings.r(), settings.g(), settings.b(), settings.a());
        vt(buffer, matrix, (float) p4.x(), (float) p4.y(), (float) p4.z(), sprite.getU0() + (float) (du * u0Par), sprite.getV1() - (float) (dv * v1Par), b1, b2, settings.r(), settings.g(), settings.b(), settings.a());
    }

    private static void drawQuad(Matrix4f matrix, VertexConsumer buffer, TextureAtlasSprite sprite, Vec3 p1, Vec3 p2, Vec3 p3, Vec3 p4,
                                 boolean opposite,
                                 RenderSettings settings) {
        int b1 = settings.brightness() >> 16 & 65535;
        int b2 = settings.brightness() & 65535;

        if (opposite) {
            vt(buffer, matrix, (float) p1.x(), (float) p1.y(), (float) p1.z(), sprite.getU0(), sprite.getV0(), b1, b2, settings.r(), settings.g(), settings.b(), settings.a());
            vt(buffer, matrix, (float) p2.x(), (float) p2.y(), (float) p2.z(), sprite.getU1(), sprite.getV0(), b1, b2, settings.r(), settings.g(), settings.b(), settings.a());
            vt(buffer, matrix, (float) p3.x(), (float) p3.y(), (float) p3.z(), sprite.getU1(), sprite.getV1(), b1, b2, settings.r(), settings.g(), settings.b(), settings.a());
            vt(buffer, matrix, (float) p4.x(), (float) p4.y(), (float) p4.z(), sprite.getU0(), sprite.getV1(), b1, b2, settings.r(), settings.g(), settings.b(), settings.a());
        } else {
            vt(buffer, matrix, (float) p4.x(), (float) p4.y(), (float) p4.z(), sprite.getU0(), sprite.getV1(), b1, b2, settings.r(), settings.g(), settings.b(), settings.a());
            vt(buffer, matrix, (float) p3.x(), (float) p3.y(), (float) p3.z(), sprite.getU1(), sprite.getV1(), b1, b2, settings.r(), settings.g(), settings.b(), settings.a());
            vt(buffer, matrix, (float) p2.x(), (float) p2.y(), (float) p2.z(), sprite.getU1(), sprite.getV0(), b1, b2, settings.r(), settings.g(), settings.b(), settings.a());
            vt(buffer, matrix, (float) p1.x(), (float) p1.y(), (float) p1.z(), sprite.getU0(), sprite.getV0(), b1, b2, settings.r(), settings.g(), settings.b(), settings.a());
        }
    }

    /**
     * Render a block outline
     */
    public static void renderRect(PoseStack poseStack, VertexConsumer buffer, Rect rect, BlockPos p, float r, float g, float b, float a) {
        Vec3 offset = Vec3.atLowerCornerOf(p);
        Vec3[] vertices = {rect.v1.add(offset), rect.v2.add(offset), rect.v3.add(offset), rect.v4.add(offset)};
        for (int i = 0; i < vertices.length; i++) {
            drawLine3d(buffer, poseStack, vertices[i], vertices[(i + 1) % vertices.length], r, g, b, a);
        }
    }

    /**
     * Draw a box. No UV mapping
     */
    public static void drawBox(PoseStack matrixStack, VertexConsumer builder,
                               float x1, float x2, float y1, float y2, float z1, float z2,
                               float r, float g, float b,
                               int packedLightIn) {
        drawBox(matrixStack, builder, x1, x2, y1, y2, z1, z2, r, g, b, 1.0f, packedLightIn);
    }

    /**
     * Draw a box with alpha. No UV mapping
     */
    public static void drawBox(PoseStack matrixStack, VertexConsumer builder,
                               float x1, float x2, float y1, float y2, float z1, float z2,
                               float r, float g, float b, float a,
                               int packedLightIn) {
        Matrix4f matrix = matrixStack.last().pose();
        // BACK
        builder.addVertex(matrix, x1, y1, z2).setColor(r, g, b, a).setLight(packedLightIn);
        builder.addVertex(matrix, x2, y1, z2).setColor(r, g, b, a).setLight(packedLightIn);
        builder.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a).setLight(packedLightIn);
        builder.addVertex(matrix, x1, y2, z2).setColor(r, g, b, a).setLight(packedLightIn);

        // FRONT
        builder.addVertex(matrix, x1, y2, z1).setColor(r, g, b, a).setLight(packedLightIn);
        builder.addVertex(matrix, x2, y2, z1).setColor(r, g, b, a).setLight(packedLightIn);
        builder.addVertex(matrix, x2, y1, z1).setColor(r, g, b, a).setLight(packedLightIn);
        builder.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a).setLight(packedLightIn);

        // DOWN
        builder.addVertex(matrix, x1, y2, z2).setColor(r, g, b, a).setLight(packedLightIn);
        builder.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a).setLight(packedLightIn);
        builder.addVertex(matrix, x2, y2, z1).setColor(r, g, b, a).setLight(packedLightIn);
        builder.addVertex(matrix, x1, y2, z1).setColor(r, g, b, a).setLight(packedLightIn);

        // UP
        builder.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a).setLight(packedLightIn);
        builder.addVertex(matrix, x2, y1, z1).setColor(r, g, b, a).setLight(packedLightIn);
        builder.addVertex(matrix, x2, y1, z2).setColor(r, g, b, a).setLight(packedLightIn);
        builder.addVertex(matrix, x1, y1, z2).setColor(r, g, b, a).setLight(packedLightIn);

        // LEFT
        builder.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a).setLight(packedLightIn);
        builder.addVertex(matrix, x1, y1, z2).setColor(r, g, b, a).setLight(packedLightIn);
        builder.addVertex(matrix, x1, y2, z2).setColor(r, g, b, a).setLight(packedLightIn);
        builder.addVertex(matrix, x1, y2, z1).setColor(r, g, b, a).setLight(packedLightIn);

        // RIGHT
        builder.addVertex(matrix, x2, y2, z1).setColor(r, g, b, a).setLight(packedLightIn);
        builder.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a).setLight(packedLightIn);
        builder.addVertex(matrix, x2, y1, z2).setColor(r, g, b, a).setLight(packedLightIn);
        builder.addVertex(matrix, x2, y1, z1).setColor(r, g, b, a).setLight(packedLightIn);
    }

    /**
     * Draw a box. The texture is mapped from 0,0 -> 1,1 on each face regardless of the size of that face
     */
    public static void drawBox(PoseStack matrixStack, VertexConsumer builder, TextureAtlasSprite sprite,
                               boolean down, boolean up, boolean north, boolean south, boolean west, boolean east,
                               float x1, float x2, float y1, float y2, float z1, float z2, RenderSettings settings) {
        Matrix4f matrix = matrixStack.last().pose();
        Vec3 c111 = new Vec3(x1, y1, z1);
        Vec3 c112 = new Vec3(x1, y1, z2);
        Vec3 c121 = new Vec3(x1, y2, z1);
        Vec3 c122 = new Vec3(x1, y2, z2);
        Vec3 c211 = new Vec3(x2, y1, z1);
        Vec3 c212 = new Vec3(x2, y1, z2);
        Vec3 c221 = new Vec3(x2, y2, z1);
        Vec3 c222 = new Vec3(x2, y2, z2);
        if (down) {
            drawQuad(matrix, builder, sprite, c211, c212, c112, c111, settings);
        }
        if (up) {
            drawQuad(matrix, builder, sprite, c121, c122, c222, c221, settings);
        }
        if (north) {
            drawQuad(matrix, builder, sprite, c121, c221, c211, c111, settings);
        }
        if (south) {
            drawQuad(matrix, builder, sprite, c112, c212, c222, c122, settings);
        }
        if (west) {
            drawQuad(matrix, builder, sprite, c112, c122, c121, c111, settings);
        }
        if (east) {
            drawQuad(matrix, builder, sprite, c211, c221, c222, c212, settings);
        }
    }

    /**
     * Draw a box. The texture is mapped from 0,0 -> 1,1 on each face regardless of the size of that face
     */
    public static void drawBox(PoseStack matrixStack, VertexConsumer builder, TextureAtlasSprite sprite,
                               float x1, float x2, float y1, float y2, float z1, float z2, RenderSettings settings) {
        Matrix4f matrix = matrixStack.last().pose();
        Vec3 c111 = new Vec3(x1, y1, z1);
        Vec3 c112 = new Vec3(x1, y1, z2);
        Vec3 c121 = new Vec3(x1, y2, z1);
        Vec3 c122 = new Vec3(x1, y2, z2);
        Vec3 c211 = new Vec3(x2, y1, z1);
        Vec3 c212 = new Vec3(x2, y1, z2);
        Vec3 c221 = new Vec3(x2, y2, z1);
        Vec3 c222 = new Vec3(x2, y2, z2);
        drawQuad(matrix, builder, sprite, c211, c212, c112, c111, settings);
        drawQuad(matrix, builder, sprite, c121, c122, c222, c221, settings);
        drawQuad(matrix, builder, sprite, c121, c221, c211, c111, settings);
        drawQuad(matrix, builder, sprite, c112, c212, c222, c122, settings);
        drawQuad(matrix, builder, sprite, c112, c122, c121, c111, settings);
        drawQuad(matrix, builder, sprite, c211, c221, c222, c212, settings);
    }

    /**
     * Draw a box. The texture is mapped from 0,0 -> 1,1 on each face regardless of the size of that face
     */
    public static void drawBoxInside(PoseStack matrixStack, VertexConsumer builder, TextureAtlasSprite sprite,
                               float x1, float x2, float y1, float y2, float z1, float z2, RenderSettings settings) {
        Matrix4f matrix = matrixStack.last().pose();
        Vec3 c111 = new Vec3(x1, y1, z1);
        Vec3 c112 = new Vec3(x1, y1, z2);
        Vec3 c121 = new Vec3(x1, y2, z1);
        Vec3 c122 = new Vec3(x1, y2, z2);
        Vec3 c211 = new Vec3(x2, y1, z1);
        Vec3 c212 = new Vec3(x2, y1, z2);
        Vec3 c221 = new Vec3(x2, y2, z1);
        Vec3 c222 = new Vec3(x2, y2, z2);
        drawQuad(matrix, builder, sprite, c111, c112, c212, c211, settings);
        drawQuad(matrix, builder, sprite, c221, c222, c122, c121, settings);
        drawQuad(matrix, builder, sprite, c111, c211, c221, c121, settings);
        drawQuad(matrix, builder, sprite, c122, c222, c212, c112, settings);
        drawQuad(matrix, builder, sprite, c111, c121, c122, c112, settings);
        drawQuad(matrix, builder, sprite, c212, c222, c221, c211, settings);
    }

    /**
     * Draw a box. The texture is mapped according to unit size and will scale with the size of every face
     */
    public static void drawBoxUnit(PoseStack matrixStack, VertexConsumer builder, TextureAtlasSprite sprite,
                                   boolean down, boolean up, boolean north, boolean south, boolean west, boolean east,
                                   float x1, float x2, float y1, float y2, float z1, float z2, RenderSettings settings) {
        Matrix4f matrix = matrixStack.last().pose();
        Vec3 c111 = new Vec3(x1, y1, z1);
        Vec3 c112 = new Vec3(x1, y1, z2);
        Vec3 c121 = new Vec3(x1, y2, z1);
        Vec3 c122 = new Vec3(x1, y2, z2);
        Vec3 c211 = new Vec3(x2, y1, z1);
        Vec3 c212 = new Vec3(x2, y1, z2);
        Vec3 c221 = new Vec3(x2, y2, z1);
        Vec3 c222 = new Vec3(x2, y2, z2);
        if (down) {
            drawQuadUnit(matrix, builder, sprite, c211, c212, c112, c111, x1, x2, z1, z2, settings);
        }
        if (up) {
            drawQuadUnit(matrix, builder, sprite, c121, c122, c222, c221, x1, x2, z1, z2, settings);
        }
        if (north) {
            drawQuadUnit(matrix, builder, sprite, c121, c221, c211, c111, x1, x2, y1, y2, settings);
        }
        if (south) {
            drawQuadUnit(matrix, builder, sprite, c112, c212, c222, c122, x1, x2, y1, y2, settings);
        }
        if (west) {
            drawQuadUnit(matrix, builder, sprite, c112, c122, c121, c111, y1, y2, z1, z2, settings);
        }
        if (east) {
            drawQuadUnit(matrix, builder, sprite, c211, c221, c222, c212, y1, y2, z1, z2, settings);
        }
    }

    public static void drawQuad(PoseStack poseStack, VertexConsumer buffer, TextureAtlasSprite sprite, Direction side, boolean opposite, float offset, RenderSettings settings) {
        Matrix4f matrix = poseStack.last().pose();
        switch (side) {
            case DOWN -> drawQuad(matrix, buffer, sprite,
                    new Vec3(0, offset, 1),
                    new Vec3(1, offset, 1),
                    new Vec3(1, offset, 0),
                    new Vec3(0, offset, 0), opposite, settings);
            case UP -> drawQuad(matrix, buffer, sprite,
                    new Vec3(1, 1 - offset, 1),
                    new Vec3(0, 1 - offset, 1),
                    new Vec3(0, 1 - offset, 0),
                    new Vec3(1, 1 - offset, 0), opposite, settings);
            case NORTH -> drawQuad(matrix, buffer, sprite,
                    new Vec3(0, 0, offset),
                    new Vec3(1, 0, offset),
                    new Vec3(1, 1, offset),
                    new Vec3(0, 1, offset), opposite, settings);
            case SOUTH -> drawQuad(matrix, buffer, sprite,
                    new Vec3(0, 1, 1 - offset),
                    new Vec3(1, 1, 1 - offset),
                    new Vec3(1, 0, 1 - offset),
                    new Vec3(0, 0, 1 - offset), opposite, settings);
            case WEST -> drawQuad(matrix, buffer, sprite,
                    new Vec3(offset, 0, 0),
                    new Vec3(offset, 1, 0),
                    new Vec3(offset, 1, 1),
                    new Vec3(offset, 0, 1), opposite, settings);
            case EAST -> drawQuad(matrix, buffer, sprite,
                    new Vec3(1 - offset, 0, 1),
                    new Vec3(1 - offset, 1, 1),
                    new Vec3(1 - offset, 1, 0),
                    new Vec3(1 - offset, 0, 0), opposite, settings);
        }
    }

    public static void vt(VertexConsumer renderer, PoseStack stack, float x, float y, float z, float r, float g, float b,
                          int packedLight) {
        renderer.addVertex(stack.last().pose(), x, y, z).setColor(r, g, b, 1f).setLight(packedLight).setNormal(1.0F, 0.0F, 0.0F);
    }

    public static void vt(VertexConsumer renderer, PoseStack stack, float x, float y, float z, float u, float v,
                          int packedLight) {
        renderer.addVertex(stack.last().pose(), x, y, z).setColor(1f, 1f, 1f, 1f).setUv(u, v).setLight(packedLight).setNormal(1.0F, 0.0F, 0.0F);
    }

    private static void vt(VertexConsumer renderer, Matrix4f matrix, float x, float y, float z, float u, float v, int packedLight) {
        renderer.addVertex(matrix, x, y, z).setColor(1f, 1f, 1f, 1f).setUv(u, v).setLight(packedLight).setNormal(1.0F, 0.0F, 0.0F);
    }

    public static void vt(VertexConsumer renderer, PoseStack matrix, float x, float y, float z, float u, float v, int lu, int lv, int r, int g, int b, int a) {
        renderer
                .addVertex(matrix.last().pose(), x, y, z)
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setUv2(lu, lv)
                .setNormal(1, 0, 0)
                ;
    }


    private static void vt(VertexConsumer renderer, Matrix4f matrix, float x, float y, float z, float u, float v) {
        renderer
                .addVertex(matrix, x, y, z)
                .setColor(1.0f, 1.0f, 1.0f, 1.0f)
                .setUv(u, v)
                .setLight(MAX_BRIGHTNESS)
                .setNormal(1, 0, 0)
                ;
    }

    private static void vt(VertexConsumer renderer, Matrix4f matrix, float x, float y, float z, float u, float v, int lu, int lv, int r, int g, int b, int a) {
        renderer
                .addVertex(matrix, x, y, z)
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setUv2(lu, lv)
                .setNormal(1, 0, 0)
                ;
    }

    public static void putVertex(VertexConsumer builder, Position normal,
                                 double x, double y, double z, float u, float v,
                                 TextureAtlasSprite sprite, float r, float g, float b, float a) {
        float iu = sprite.getU(u);
        float iv = sprite.getV(v);
        builder.addVertex((float) x, (float) y, (float) z)
                .setUv(iu, iv)
                .setUv2(0, 0)
                .setColor(r, g, b, a)
                .setNormal((float) normal.x(), (float) normal.y(), (float) normal.z())
                ;
    }

    public static void renderHighLightedBlocksOutline(PoseStack poseStack, VertexConsumer buffer, float mx, float my, float mz, float r, float g, float b, float a) {
        for (int axis = 0; axis < 3; axis++) {
            for (int first = 0; first < 2; first++) {
                for (int second = 0; second < 2; second++) {
                    double[] start = {mx, my, mz};
                    start[(axis + 1) % 3] += first;
                    start[(axis + 2) % 3] += second;
                    double[] end = start.clone();
                    end[axis]++;
                    drawLine3d(buffer, poseStack, new Vec3(start[0], start[1], start[2]),
                            new Vec3(end[0], end[1], end[2]), r, g, b, a);
                }
            }
        }
    }

    private static void drawLine3d(VertexConsumer buffer, PoseStack pose, Vec3 start, Vec3 end, float r, float g, float b, float a) {
        Vec3 direction = end.subtract(start).normalize();
        var normal = new Vector3f((float)direction.x, (float)direction.y, (float)direction.z);
        pose.last().normal().transform(normal);
        buffer.addVertex(pose.last().pose(), (float)start.x, (float)start.y, (float)start.z)
                .setColor(r, g, b, a).setNormal(normal.x, normal.y, normal.z).setLineWidth(2);
        buffer.addVertex(pose.last().pose(), (float)end.x, (float)end.y, (float)end.z)
                .setColor(r, g, b, a).setNormal(normal.x, normal.y, normal.z).setLineWidth(2);
    }

    public static void rotateXP(PoseStack stack, float degrees) {
        stack.rotate(Axis.XP.rotationDegrees(degrees));
    }

    public static void rotateYP(PoseStack stack, float degrees) {
        stack.rotate(Axis.YP.rotationDegrees(degrees));
    }

    public static void rotateZP(PoseStack stack, float degrees) {
        stack.rotate(Axis.ZP.rotationDegrees(degrees));
    }

    public record Rect(Vec3 v1, Vec3 v2,
                       Vec3 v3, Vec3 v4) {
    }
}
