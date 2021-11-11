package mcjty.lib.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.varia.MathTools;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import javax.annotation.Nullable;

public class RenderHelper {
    public static float rot = 0.0f;

    public static final int MAX_BRIGHTNESS = 0xf000f0;

    public static final RenderSettings DEFAULT_SETTINGS = RenderSettings.builder()
            .color(255, 255, 255)
            .alpha(128)
            .build();


    public static void renderEntity(MatrixStack matrixStack, Entity entity, int xPos, int yPos) {
        float f1 = 10F;
        renderEntity(matrixStack, entity, xPos, yPos, f1);
    }

    // Adjust the given matrix to the specified direction
    public static void adjustTransformToDirection(MatrixStack matrixStack, Direction facing) {
        matrixStack.translate(0.5F, 0.5F, 0.5F);

        switch (facing) {
            case DOWN:
                matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
                break;
            case UP:
                matrixStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
                break;
            case NORTH:
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(-180.0F));
                break;
            case SOUTH:
                break;
            case WEST:
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
                break;
            case EAST:
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
                break;
        }

        matrixStack.translate(-0.5F, -0.5F, -0.5F);
    }

    public static void renderNorthSouthQuad(IVertexBuilder builder, Matrix4f matrix, TextureAtlasSprite sprite, ModelBuilder.FaceRotation rotation, float offset) {
        switch (rotation) {
            case ZERO:
                RenderHelper.vt(builder, matrix, 0, 1, .73f, sprite.getU0(), sprite.getV1());
                RenderHelper.vt(builder, matrix, 1, 1, .73f, sprite.getU1(), sprite.getV1());
                RenderHelper.vt(builder, matrix, 1, 0, .73f, sprite.getU1(), sprite.getV0());
                RenderHelper.vt(builder, matrix, 0, 0, .73f, sprite.getU0(), sprite.getV0());
                break;
            case CLOCKWISE_90:
                RenderHelper.vt(builder, matrix, 0, 1, .73f, sprite.getU1(), sprite.getV0());
                RenderHelper.vt(builder, matrix, 1, 1, .73f, sprite.getU0(), sprite.getV0());
                RenderHelper.vt(builder, matrix, 1, 0, .73f, sprite.getU0(), sprite.getV1());
                RenderHelper.vt(builder, matrix, 0, 0, .73f, sprite.getU1(), sprite.getV1());
                break;
            case UPSIDE_DOWN:
                RenderHelper.vt(builder, matrix, 0, 1, .73f, sprite.getU0(), sprite.getV0());
                RenderHelper.vt(builder, matrix, 1, 1, .73f, sprite.getU0(), sprite.getV1());
                RenderHelper.vt(builder, matrix, 1, 0, .73f, sprite.getU1(), sprite.getV1());
                RenderHelper.vt(builder, matrix, 0, 0, .73f, sprite.getU1(), sprite.getV0());
                break;
            case COUNTERCLOCKWISE_90:
                RenderHelper.vt(builder, matrix, 0, 1, .73f, sprite.getU1(), sprite.getV1());
                RenderHelper.vt(builder, matrix, 1, 1, .73f, sprite.getU1(), sprite.getV0());
                RenderHelper.vt(builder, matrix, 1, 0, .73f, sprite.getU0(), sprite.getV0());
                RenderHelper.vt(builder, matrix, 0, 0, .73f, sprite.getU0(), sprite.getV1());
                break;
        }
    }

    public static void renderEntity(MatrixStack matrixStack, Entity entity, int xPos, int yPos, float scale) {
        matrixStack.pushPose();
        GlStateManager._color4f(1f, 1f, 1f, 1f);
        GlStateManager._enableRescaleNormal();
        GlStateManager._enableColorMaterial();
        matrixStack.pushPose();
        matrixStack.translate(xPos + 8, yPos + 16, 50F);
        matrixStack.scale(-scale, scale, scale);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180F));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(135F));
        net.minecraft.client.renderer.RenderHelper.turnBackOn();
        matrixStack.mulPose(Vector3f.YN.rotationDegrees(135F));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(rot));
//        GlStateManager.rotatef(0.0F, 1.0F, 0.0F, 0.0F);
//        entity.renderYawOffset = entity.rotationYaw = entity.prevRotationYaw = entity.prevRotationYawHead = entity.rotationYawHead = 0;//this.rotateTurret;
        entity.xRot = 0.0F;
        matrixStack.translate(0.0F, (float) entity.getMyRidingOffset(), 0.0F);
        // @todo 1.15
//        Minecraft.getInstance().getRenderManager().playerViewY = 180F;
//        Minecraft.getInstance().getRenderManager().renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        matrixStack.popPose();
        net.minecraft.client.renderer.RenderHelper.turnOff();

        GlStateManager._disableRescaleNormal();
        matrixStack.translate(0F, 0F, 0.0F);
        GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager._enableRescaleNormal();
        int i1 = 240;
        int k1 = 240;

        // @todo 1.14 check if right?
        // @todo 1.15
//        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, i1 / 1.0F, k1 / 1.0F);
//        OpenGlHelper.setLightmapTextureCoords(GLX.GL_TEXTURE1, i1 / 1.0F, k1 / 1.0F);

        GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager._disableRescaleNormal();
        net.minecraft.client.renderer.RenderHelper.turnOff();
        GlStateManager._disableLighting();
        GlStateManager._disableDepthTest();
        matrixStack.popPose();
    }

    public static boolean renderObject(MatrixStack matrixStack, int x, int y, Object itm, boolean highlight) {
        if (itm instanceof Entity) {
            renderEntity(matrixStack, (Entity) itm, x, y);
            return true;
        }
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        return renderObject(matrixStack, itemRenderer, x, y, itm, highlight, 100);
    }

    public static boolean renderObject(MatrixStack matrixStack, ItemRenderer itemRender, int x, int y, Object itm, boolean highlight, float lvl) {
        itemRender.blitOffset = lvl;

        if (itm == null) {
            return renderItemStack(matrixStack, itemRender, ItemStack.EMPTY, x, y, "", highlight);
        }
        if (itm instanceof Item) {
            return renderItemStack(matrixStack, itemRender, new ItemStack((Item) itm, 1), x, y, "", highlight);
        }
        if (itm instanceof Block) {
            return renderItemStack(matrixStack, itemRender, new ItemStack((Block) itm, 1), x, y, "", highlight);
        }
        if (itm instanceof ItemStack) {
            return renderItemStackWithCount(matrixStack, itemRender, (ItemStack) itm, x, y, highlight);
        }
        if (itm instanceof FluidStack) {
            return renderFluidStack((FluidStack) itm, x, y, highlight);
        }
        if (itm instanceof TextureAtlasSprite) {
            return renderIcon(matrixStack, itemRender, (TextureAtlasSprite) itm, x, y, highlight);
        }
        return renderItemStack(matrixStack, itemRender, ItemStack.EMPTY, x, y, "", highlight);
    }

    public static boolean renderIcon(MatrixStack matrixStack, ItemRenderer itemRender, TextureAtlasSprite itm, int xo, int yo, boolean highlight) {
        //itemRender.draw(xo, yo, itm, 16, 16); //TODO: Make
        return true;
    }

    public static boolean renderFluidStack(FluidStack fluidStack, int x, int y, boolean highlight) {
        Fluid fluid = fluidStack.getFluid();
        if (fluid == null) {
            return false;
        }

        ResourceLocation fluidStill = fluid.getAttributes().getStillTexture();
        TextureAtlasSprite fluidStillSprite = null;
        if (fluidStill != null) {
            fluidStillSprite = Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(fluidStill);
        }
        if (fluidStillSprite == null) {
            return false;
        }

        int fluidColor = fluid.getAttributes().getColor(fluidStack);
        Minecraft.getInstance().getEntityRenderDispatcher().textureManager.bind(AtlasTexture.LOCATION_BLOCKS);
        setGLColorFromInt(fluidColor);
        drawFluidTexture(x, y, fluidStillSprite, 100);

        return true;
    }

    private static void drawFluidTexture(double xCoord, double yCoord, TextureAtlasSprite textureSprite, double zLevel) {
        float uMin = textureSprite.getU0();
        float uMax = textureSprite.getU1();
        float vMin = textureSprite.getV0();
        float vMax = textureSprite.getV1();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexBuffer = tessellator.getBuilder();
        vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexBuffer.vertex(xCoord, yCoord + 16, zLevel).uv(uMin, vMax).endVertex();
        vertexBuffer.vertex(xCoord + 16, yCoord + 16, zLevel).uv(uMax, vMax).endVertex();
        vertexBuffer.vertex(xCoord + 16, yCoord, zLevel).uv(uMax, vMin).endVertex();
        vertexBuffer.vertex(xCoord, yCoord, zLevel).uv(uMin, vMin).endVertex();
        tessellator.end();
    }


    private static void setGLColorFromInt(int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        GlStateManager._color4f(red, green, blue, 1.0F);
    }


    public static boolean renderItemStackWithCount(MatrixStack matrixStack, ItemRenderer itemRender, ItemStack itm, int xo, int yo, boolean highlight) {
        int size = itm.getCount();
        String amount;
        if (size <= 1) {
            amount = "";
        } else if (size < 100000) {
            amount = String.valueOf(size);
        } else if (size < 1000000) {
            amount = String.valueOf(size / 1000) + "k";
        } else if (size < 1000000000) {
            amount = String.valueOf(size / 1000000) + "m";
        } else {
            amount = String.valueOf(size / 1000000000) + "g";
        }

        return renderItemStack(matrixStack, itemRender, itm, xo, yo, amount, highlight);
//        if (itm.stackSize==1 || itm.stackSize==0) {
//            return renderItemStack(mc, itemRender, itm, xo, yo, "", highlight);
//        } else {
//            return renderItemStack(mc, itemRender, itm, xo, yo, "" + itm.stackSize, highlight);
//        }
    }

    public static void renderStackOnGround(MatrixStack matrixStack, ItemStack stack, double alpha) {
        if (!stack.isEmpty()) {
            IBakedModel ibakedmodel = Minecraft.getInstance().getItemRenderer().getModel(stack, null, null);
            if (!stack.isEmpty()) {
                Minecraft.getInstance().getTextureManager().bind(AtlasTexture.LOCATION_BLOCKS);
                Minecraft.getInstance().getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS).setFilter(false, false);
                GlStateManager._color4f(1.0F, 1.0F, 1.0F, (float) alpha);
                GlStateManager._enableRescaleNormal();
                GlStateManager._alphaFunc(GL11.GL_GREATER, 0.1F);
                GlStateManager._enableBlend();
                GlStateManager._blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
                matrixStack.pushPose();

                // @todo 1.15
//                ibakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GROUND, false);
//                Minecraft.getInstance().getItemRenderer().renderItem(stack, ibakedmodel);

                // @todo 1.15
//                GlStateManager.cullFace(GlStateManager.CullFace.BACK);
                matrixStack.popPose();
                GlStateManager._disableRescaleNormal();
                GlStateManager._disableBlend();
                Minecraft.getInstance().getTextureManager().bind(AtlasTexture.LOCATION_BLOCKS);
                Minecraft.getInstance().getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS).restoreLastBlurMipmap();
            }
        }

    }

    public static boolean renderItemStack(MatrixStack matrixStack, ItemRenderer itemRender, ItemStack itm, int x, int y, String txt, boolean highlight) {
        RenderSystem.color4f(1F, 1F, 1F, 1f);

        boolean rc = false;
        if (highlight) {
            RenderSystem.disableLighting();
            drawVerticalGradientRect(x, y, x + 16, y + 16, 0x80ffffff, 0xffffffff);
        }
        if (!itm.isEmpty() && itm.getItem() != null) {
            rc = true;
            matrixStack.pushPose();
            RenderSystem.color4f(1F, 1F, 1F, 1F);
            RenderSystem.enableRescaleNormal();
            RenderSystem.enableLighting();
            net.minecraft.client.renderer.RenderHelper.turnBackOn();
            // @todo 1.14 check if right?
            // @todo 1.15
            RenderSystem.glMultiTexCoord2f(GL13.GL_TEXTURE1, (float) 240, (float) 240);
//            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, short1 / 1.0F, short2 / 1.0F);

            itemRender.renderAndDecorateItem(itm, x, y);
            renderItemOverlayIntoGUI(matrixStack, Minecraft.getInstance().font, itm, x, y, txt, txt.length() - 2);
//            itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, itm, x, y, txt);
            matrixStack.popPose();
            RenderSystem.disableRescaleNormal();
            RenderSystem.disableLighting();
        }

        return rc;
    }

    private static void renderItemOverlayIntoGUI(MatrixStack matrixStack, FontRenderer fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text,
                                                    int scaled) {
        if (!stack.isEmpty()) {
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            if (stack.getCount() != 1 || text != null) {
                String s = text == null ? String.valueOf(stack.getCount()) : text;
                matrixStack.translate(0.0D, 0.0D, (itemRenderer.blitOffset + 200.0F));
                IRenderTypeBuffer.Impl buffer = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());

                if (scaled >= 2) {
                    matrixStack.pushPose();
                    matrixStack.scale(.5f, .5f, .5f);
                    fr.drawShadow(matrixStack, s, ((xPosition + 19 - 2) * 2 - 1 - fr.width(s)), yPosition * 2 + 24, 16777215);
                    matrixStack.popPose();
                } else if (scaled == 1) {
                    matrixStack.pushPose();
                    matrixStack.scale(.75f, .75f, .75f);
                    fr.drawShadow(matrixStack, s, ((xPosition - 2) * 1.34f + 24 - fr.width(s)), yPosition * 1.34f + 14, 16777215);
                    matrixStack.popPose();
                } else {
                    fr.drawShadow(matrixStack, s, (xPosition + 19 - 2 - fr.width(s)), (float)(yPosition + 6 + 3), 16777215);
                }

                buffer.endBatch();
            }

            if (stack.getItem().showDurabilityBar(stack)) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableAlphaTest();
                RenderSystem.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuilder();
                double health = stack.getItem().getDurabilityForDisplay(stack);
                int i = Math.round(13.0F - (float)health * 13.0F);
                int j = stack.getItem().getRGBDurabilityForDisplay(stack);
                draw(bufferbuilder, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
                draw(bufferbuilder, xPosition + 2, yPosition + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
                RenderSystem.enableBlend();
                RenderSystem.enableAlphaTest();
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }

            ClientPlayerEntity clientplayerentity = Minecraft.getInstance().player;
            float f3 = clientplayerentity == null ? 0.0F : clientplayerentity.getCooldowns().getCooldownPercent(stack.getItem(), Minecraft.getInstance().getFrameTime());
            if (f3 > 0.0F) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                Tessellator tessellator1 = Tessellator.getInstance();
                BufferBuilder bufferbuilder1 = tessellator1.getBuilder();
                draw(bufferbuilder1, xPosition, yPosition + MathHelper.floor(16.0F * (1.0F - f3)), 16, MathHelper.ceil(16.0F * f3), 255, 255, 255, 127);
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }

        }
    }

    /**
     * Renders the stack size and/or damage bar for the given ItemStack.
     */
    private static void renderItemOverlayIntoGUIOld(MatrixStack matrixStack, FontRenderer fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text,
                                                 int scaled) {
        if (!stack.isEmpty()) {
            int stackSize = stack.getCount();
            if (stackSize != 1 || text != null) {
                String s = text == null ? String.valueOf(stackSize) : text;
                if (text == null && stackSize < 1) {
                    s = TextFormatting.RED + String.valueOf(stackSize);
                }

                RenderSystem.disableLighting();
                RenderSystem.disableDepthTest();
                RenderSystem.disableBlend();
                if (scaled >= 2) {
                    matrixStack.pushPose();
                    matrixStack.scale(.5f, .5f, .5f);
                    fr.drawShadow(matrixStack, s, ((xPosition + 19 - 2) * 2 - 1 - fr.width(s)), yPosition * 2 + 24, 16777215);
                    matrixStack.popPose();
                } else if (scaled == 1) {
                    matrixStack.pushPose();
                    matrixStack.scale(.75f, .75f, .75f);
                    fr.drawShadow(matrixStack, s, ((xPosition - 2) * 1.34f + 24 - fr.width(s)), yPosition * 1.34f + 14, 16777215);
                    matrixStack.popPose();
                } else {
                    fr.drawShadow(matrixStack, s, (xPosition + 19 - 2 - fr.width(s)), (yPosition + 6 + 3), 16777215);
                }
                RenderSystem.enableLighting();
                RenderSystem.enableDepthTest();
                // Fixes opaque cooldown overlay a bit lower
                // TODO: check if enabled blending still screws things up down the line.
                RenderSystem.enableBlend();
            }

            if (stack.getItem().showDurabilityBar(stack)) {
                double health = stack.getItem().getDurabilityForDisplay(stack);
                int j = (int) Math.round(13.0D - health * 13.0D);
                int i = (int) Math.round(255.0D - health * 255.0D);
                RenderSystem.disableLighting();
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableAlphaTest();
                RenderSystem.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder vertexbuffer = tessellator.getBuilder();
                draw(vertexbuffer, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
                draw(vertexbuffer, xPosition + 2, yPosition + 13, 12, 1, (255 - i) / 4, 64, 0, 255);
                draw(vertexbuffer, xPosition + 2, yPosition + 13, j, 1, 255 - i, i, 0, 255);
                RenderSystem.enableBlend();
                RenderSystem.enableAlphaTest();
                RenderSystem.enableTexture();
                RenderSystem.enableLighting();
                RenderSystem.enableDepthTest();
            }

            ClientPlayerEntity PlayerEntitysp = Minecraft.getInstance().player;
            float f = PlayerEntitysp == null ? 0.0F : PlayerEntitysp.getCooldowns().getCooldownPercent(stack.getItem(), Minecraft.getInstance().getFrameTime());

            if (f > 0.0F) {
                RenderSystem.disableLighting();
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                Tessellator tessellator1 = Tessellator.getInstance();
                BufferBuilder vertexbuffer1 = tessellator1.getBuilder();
                draw(vertexbuffer1, xPosition, yPosition + MathTools.floor(16.0F * (1.0F - f)), 16, MathTools.ceiling(16.0F * f), 255, 255, 255, 127);
                RenderSystem.enableTexture();
                RenderSystem.enableLighting();
                RenderSystem.enableDepthTest();
            }
        }
    }

    /**
     * Draw with the WorldRenderer
     */
    private static void draw(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        renderer.vertex((x + 0), (y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.vertex((x + 0), (y + height), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.vertex((x + width), (y + height), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.vertex((x + width), (y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
        Tessellator.getInstance().end();
    }


    /**
     * Draws a rectangle with a vertical gradient between the specified colors.
     * x2 and y2 are not included.
     */
    public static void drawVerticalGradientRect(int x1, int y1, int x2, int y2, int color1, int color2) {
//        this.zLevel = 300.0F;
        float zLevel = 0.0f;

        float f = (color1 >> 24 & 255) / 255.0F;
        float f1 = (color1 >> 16 & 255) / 255.0F;
        float f2 = (color1 >> 8 & 255) / 255.0F;
        float f3 = (color1 & 255) / 255.0F;
        float f4 = (color2 >> 24 & 255) / 255.0F;
        float f5 = (color2 >> 16 & 255) / 255.0F;
        float f6 = (color2 >> 8 & 255) / 255.0F;
        float f7 = (color2 & 255) / 255.0F;
        GlStateManager._disableTexture();
        GlStateManager._enableBlend();
        GlStateManager._disableAlphaTest();

        // @todo 1.14 check
//        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GlStateManager._blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);

        GlStateManager._shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(x2, y1, zLevel).color(f1, f2, f3, f).endVertex();
        buffer.vertex(x1, y1, zLevel).color(f1, f2, f3, f).endVertex();
        buffer.vertex(x1, y2, zLevel).color(f5, f6, f7, f4).endVertex();
        buffer.vertex(x2, y2, zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.end();

        GlStateManager._shadeModel(GL11.GL_FLAT);
        GlStateManager._disableBlend();
        GlStateManager._enableAlphaTest();
        GlStateManager._enableTexture();
    }

    /**
     * Draws a rectangle with a horizontal gradient between the specified colors.
     * x2 and y2 are not included.
     */
    public static void drawHorizontalGradientRect(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int color1, int color2) {
//        this.zLevel = 300.0F;
        float zLevel = 0.0f;

        float f = (color1 >> 24 & 255) / 255.0F;
        float f1 = (color1 >> 16 & 255) / 255.0F;
        float f2 = (color1 >> 8 & 255) / 255.0F;
        float f3 = (color1 & 255) / 255.0F;
        float f4 = (color2 >> 24 & 255) / 255.0F;
        float f5 = (color2 >> 16 & 255) / 255.0F;
        float f6 = (color2 >> 8 & 255) / 255.0F;
        float f7 = (color2 & 255) / 255.0F;
        GlStateManager._disableTexture();
        GlStateManager._enableBlend();
        GlStateManager._disableAlphaTest();

        // @todo 1.14 check
//        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GlStateManager._blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);

        GlStateManager._shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(x1, y1, zLevel).color(f1, f2, f3, f).endVertex();
        buffer.vertex(x1, y2, zLevel).color(f1, f2, f3, f).endVertex();
        buffer.vertex(x2, y2, zLevel).color(f5, f6, f7, f4).endVertex();
        buffer.vertex(x2, y1, zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.end();
        GlStateManager._shadeModel(GL11.GL_FLAT);
        GlStateManager._disableBlend();
        GlStateManager._enableAlphaTest();
        GlStateManager._enableTexture();
    }

    /**
     * Draws a rectangle with a horizontal gradient between the specified colors.
     * x2 and y2 are not included.
     */
    public static void drawHorizontalGradientRect(MatrixStack matrixStack, IRenderTypeBuffer buffer, int x1, int y1, int x2, int y2, int color1, int color2, int lightmap) {
//        this.zLevel = 300.0F;
        float zLevel = 0.0f;

        float f = (color1 >> 24 & 255) / 255.0F;
        float f1 = (color1 >> 16 & 255) / 255.0F;
        float f2 = (color1 >> 8 & 255) / 255.0F;
        float f3 = (color1 & 255) / 255.0F;
        float f4 = (color2 >> 24 & 255) / 255.0F;
        float f5 = (color2 >> 16 & 255) / 255.0F;
        float f6 = (color2 >> 8 & 255) / 255.0F;
        float f7 = (color2 & 255) / 255.0F;
//        GlStateManager.disableTexture();
//        GlStateManager.enableBlend();
//        GlStateManager.disableAlphaTest();

        // @todo 1.14 check
//        OpenGlHelper.glBlendFunc(770, 771, 1, 0);


//        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);

//        GlStateManager.shadeModel(GL11.GL_SMOOTH);
//        Tessellator tessellator = Tessellator.getInstance();
//        BufferBuilder buffer = tessellator.getBuffer();
//        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        IVertexBuilder builder = buffer.getBuffer(CustomRenderTypes.QUADS_NOTEXTURE);

        Matrix4f positionMatrix = matrixStack.last().pose();
        builder.vertex(positionMatrix, x1, y1, zLevel).color(f1, f2, f3, f).uv2(lightmap).endVertex();
        builder.vertex(positionMatrix, x1, y2, zLevel).color(f1, f2, f3, f).uv2(lightmap).endVertex();
        builder.vertex(positionMatrix, x2, y2, zLevel).color(f5, f6, f7, f4).uv2(lightmap).endVertex();
        builder.vertex(positionMatrix, x2, y1, zLevel).color(f5, f6, f7, f4).uv2(lightmap).endVertex();
//        GlStateManager.shadeModel(GL11.GL_FLAT);
//        GlStateManager.disableBlend();
//        GlStateManager.enableAlphaTest();
//        GlStateManager.enableTexture();
    }

    public static void drawHorizontalLine(MatrixStack matrixStack, int x1, int y1, int x2, int color) {
        Screen.fill(matrixStack, x1, y1, x2, y1 + 1, color);
    }

    public static void drawVerticalLine(MatrixStack matrixStack, int x1, int y1, int y2, int color) {
        Screen.fill(matrixStack, x1, y1, x1 + 1, y2, color);
    }

    // Draw a small triangle. x,y is the coordinate of the left point
    public static void drawLeftTriangle(MatrixStack matrixStack, int x, int y, int color) {
        drawVerticalLine(matrixStack, x, y, y, color);
        drawVerticalLine(matrixStack, x + 1, y - 1, y + 1, color);
        drawVerticalLine(matrixStack, x + 2, y - 2, y + 2, color);
    }

    // Draw a small triangle. x,y is the coordinate of the right point
    public static void drawRightTriangle(MatrixStack matrixStack, int x, int y, int color) {
        drawVerticalLine(matrixStack, x, y, y, color);
        drawVerticalLine(matrixStack, x - 1, y - 1, y + 1, color);
        drawVerticalLine(matrixStack, x - 2, y - 2, y + 2, color);
    }

    // Draw a small triangle. x,y is the coordinate of the top point
    public static void drawUpTriangle(MatrixStack matrixStack, int x, int y, int color) {
        drawHorizontalLine(matrixStack, x, y, x, color);
        drawHorizontalLine(matrixStack, x - 1, y + 1, x + 1, color);
        drawHorizontalLine(matrixStack, x - 2, y + 2, x + 2, color);
    }

    // Draw a small triangle. x,y is the coordinate of the bottom point
    public static void drawDownTriangle(MatrixStack matrixStack, int x, int y, int color) {
        drawHorizontalLine(matrixStack, x, y, x, color);
        drawHorizontalLine(matrixStack, x - 1, y - 1, x + 1, color);
        drawHorizontalLine(matrixStack, x - 2, y - 2, x + 2, color);
    }

    public static void drawColorLogic(int x, int y, int width, int height, int red, int green, int blue, GlStateManager.LogicOp colorLogic) {
        GlStateManager._disableTexture();
        GlStateManager._enableColorLogicOp();
        GlStateManager._logicOp(colorLogic.value);

        draw(Tessellator.getInstance().getBuilder(), x, y, width, height, red, green, blue, 255);

        GlStateManager._disableColorLogicOp();
        GlStateManager._enableTexture();
    }

    /**
     * Draw a button box. x2 and y2 are not included.
     */
    public static void drawThickButtonBox(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int bright, int average, int dark) {
        Screen.fill(matrixStack, x1 + 2, y1 + 2, x2 - 2, y2 - 2, average);
        drawHorizontalLine(matrixStack, x1 + 1, y1, x2 - 1, StyleConfig.colorButtonExternalBorder);
        drawHorizontalLine(matrixStack, x1 + 1, y2 - 1, x2 - 1, StyleConfig.colorButtonExternalBorder);
        drawVerticalLine(matrixStack, x1, y1 + 1, y2 - 1, StyleConfig.colorButtonExternalBorder);
        drawVerticalLine(matrixStack, x2 - 1, y1 + 1, y2 - 1, StyleConfig.colorButtonExternalBorder);

        drawHorizontalLine(matrixStack, x1 + 1, y1 + 1, x2 - 1, bright);
        drawHorizontalLine(matrixStack, x1 + 2, y1 + 2, x2 - 2, bright);
        drawVerticalLine(matrixStack, x1 + 1, y1 + 2, y2 - 2, bright);
        drawVerticalLine(matrixStack, x1 + 2, y1 + 3, y2 - 3, bright);

        drawHorizontalLine(matrixStack, x1 + 3, y2 - 3, x2 - 2, dark);
        drawHorizontalLine(matrixStack, x1 + 2, y2 - 2, x2 - 1, dark);
        drawVerticalLine(matrixStack, x2 - 2, y1 + 2, y2 - 2, dark);
        drawVerticalLine(matrixStack, x2 - 3, y1 + 3, y2 - 3, dark);
    }

    /**
     * Draw a button box. x2 and y2 are not included.
     */
    public static void drawThinButtonBox(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int bright, int average, int dark) {
        Screen.fill(matrixStack, x1 + 1, y1 + 1, x2 - 1, y2 - 1, average);
        drawHorizontalLine(matrixStack, x1 + 1, y1, x2 - 1, StyleConfig.colorButtonExternalBorder);
        drawHorizontalLine(matrixStack, x1 + 1, y2 - 1, x2 - 1, StyleConfig.colorButtonExternalBorder);
        drawVerticalLine(matrixStack, x1, y1 + 1, y2 - 1, StyleConfig.colorButtonExternalBorder);
        drawVerticalLine(matrixStack, x2 - 1, y1 + 1, y2 - 1, StyleConfig.colorButtonExternalBorder);

        drawHorizontalLine(matrixStack, x1 + 1, y1 + 1, x2 - 2, bright);
        drawVerticalLine(matrixStack, x1 + 1, y1 + 2, y2 - 3, bright);

        drawHorizontalLine(matrixStack, x1 + 1, y2 - 2, x2 - 1, dark);
        drawVerticalLine(matrixStack, x2 - 2, y1 + 1, y2 - 2, dark);
    }

    /**
     * Draw a button box. x2 and y2 are not included.
     */
    public static void drawThinButtonBoxGradient(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int bright, int average1, int average2, int dark) {
        drawVerticalGradientRect(x1 + 1, y1 + 1, x2 - 1, y2 - 1, average2, average1);
        drawHorizontalLine(matrixStack, x1 + 1, y1, x2 - 1, StyleConfig.colorButtonExternalBorder);
        drawHorizontalLine(matrixStack, x1 + 1, y2 - 1, x2 - 1, StyleConfig.colorButtonExternalBorder);
        drawVerticalLine(matrixStack, x1, y1 + 1, y2 - 1, StyleConfig.colorButtonExternalBorder);
        drawVerticalLine(matrixStack, x2 - 1, y1 + 1, y2 - 1, StyleConfig.colorButtonExternalBorder);

        drawHorizontalLine(matrixStack, x1 + 1, y1 + 1, x2 - 2, bright);
        drawVerticalLine(matrixStack, x1 + 1, y1 + 2, y2 - 3, bright);

        drawHorizontalLine(matrixStack, x1 + 1, y2 - 2, x2 - 1, dark);
        drawVerticalLine(matrixStack, x2 - 2, y1 + 1, y2 - 2, dark);
    }

    /**
     * Draw a button box. x2 and y2 are not included.
     */
    public static void drawFlatButtonBox(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int bright, int average, int dark) {
        drawBeveledBox(matrixStack, x1, y1, x2, y2, bright, dark, average);
    }

    /**
     * Draw a button box. x2 and y2 are not included.
     */
    public static void drawFlatButtonBox(MatrixStack matrixStack, IRenderTypeBuffer buffer, int x1, int y1, int x2, int y2, int bright, int average, int dark, int lightmap) {
        drawBeveledBox(matrixStack, buffer, x1, y1, x2, y2, bright, dark, average, lightmap);
    }

    /**
     * Draw a button box. x2 and y2 are not included.
     */
    public static void drawFlatButtonBoxGradient(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int bright, int average1, int average2, int dark) {
        drawVerticalGradientRect(x1 + 1, y1 + 1, x2 - 1, y2 - 1, average2, average1);
        drawHorizontalLine(matrixStack, x1, y1, x2 - 1, bright);
        drawVerticalLine(matrixStack, x1, y1, y2 - 1, bright);
        drawVerticalLine(matrixStack, x2 - 1, y1, y2 - 1, dark);
        drawHorizontalLine(matrixStack, x1, y2 - 1, x2, dark);
    }

    /**
     * Draw a beveled box. x2 and y2 are not included.
     */
    public static void drawBeveledBox(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int topleftcolor, int botrightcolor, int fillcolor) {
        if (fillcolor != -1) {
            Screen.fill(matrixStack, x1 + 1, y1 + 1, x2 - 1, y2 - 1, fillcolor);
        }
        drawHorizontalLine(matrixStack, x1, y1, x2 - 1, topleftcolor);
        drawVerticalLine(matrixStack, x1, y1, y2 - 1, topleftcolor);
        drawVerticalLine(matrixStack, x2 - 1, y1, y2 - 1, botrightcolor);
        drawHorizontalLine(matrixStack, x1, y2 - 1, x2, botrightcolor);
    }

    /**
     * Draw a beveled box. x2 and y2 are not included. Use this version for GUI's
     */
    public static void drawBeveledBox(MatrixStack matrixStack, IRenderTypeBuffer buffer, int x1, int y1, int x2, int y2, int topleftcolor, int botrightcolor, int fillcolor, int lightmap) {
        if (fillcolor != -1) {
            fill(matrixStack, buffer, x1 + 1, y1 + 1, x2 - 1, y2 - 1, fillcolor, lightmap);
        }
        fill(matrixStack, buffer, x1, y1, x2 - 1, y1 + 1, topleftcolor, lightmap);
        fill(matrixStack, buffer, x1, y1, x1 + 1, y2 - 1, topleftcolor, lightmap);
        fill(matrixStack, buffer, x2 - 1, y1, x2 - 1 + 1, y2 - 1, botrightcolor, lightmap);
        fill(matrixStack, buffer, x1, y2 - 1, x2, y2 - 1 + 1, botrightcolor, lightmap);
    }

    /**
     * Draw a thick beveled box. x2 and y2 are not included. Use this version for batched rendering
     */
    public static void drawThickBeveledBox(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int thickness, int topleftcolor, int botrightcolor, int fillcolor) {
        if (fillcolor != -1) {
            Screen.fill(matrixStack, x1 + 1, y1 + 1, x2 - 1, y2 - 1, fillcolor);
        }
        Screen.fill(matrixStack, x1, y1, x2 - 1, y1 + thickness, topleftcolor);
        Screen.fill(matrixStack, x1, y1, x1 + thickness, y2 - 1, topleftcolor);
        Screen.fill(matrixStack, x2 - thickness, y1, x2, y2 - 1, botrightcolor);
        Screen.fill(matrixStack, x1, y2 - thickness, x2, y2, botrightcolor);
    }

    /**
     * Draw a beveled box. x2 and y2 are not included.
     */
    public static void drawFlatBox(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int border, int fill) {
        if (fill != -1) {
            Screen.fill(matrixStack, x1 + 1, y1 + 1, x2 - 1, y2 - 1, fill);
        }
        drawHorizontalLine(matrixStack, x1, y1, x2 - 1, border);
        drawVerticalLine(matrixStack, x1, y1, y2 - 1, border);
        drawVerticalLine(matrixStack, x2 - 1, y1, y2 - 1, border);
        drawHorizontalLine(matrixStack, x1, y2 - 1, x2, border);
    }

    /**
     * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
     */
    public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
        float zLevel = 0.01f;
        float f = (1 / 256.0f);
        float f1 = (1 / 256.0f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.vertex((x + 0), (y + height), zLevel).uv(((u + 0) * f), ((v + height) * f1)).endVertex();
        buffer.vertex((x + width), (y + height), zLevel).uv(((u + width) * f), ((v + height) * f1)).endVertex();
        buffer.vertex((x + width), (y + 0), zLevel).uv(((u + width) * f), ((v + 0) * f1)).endVertex();
        buffer.vertex((x + 0), (y + 0), zLevel).uv(((u + 0) * f), ((v + 0) * f1)).endVertex();
        tessellator.end();
    }

    public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height, int totw, int toth) {
        float f = 1.0f / totw;
        float f1 = 1.0f / toth;
        double zLevel = 50;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuilder();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.vertex((x + 0), (y + height), zLevel).uv(((textureX + 0) * f), ((textureY + height) * f1)).endVertex();
        vertexbuffer.vertex((x + width), (y + height), zLevel).uv(((textureX + width) * f), ((textureY + height) * f1)).endVertex();
        vertexbuffer.vertex((x + width), (y + 0), zLevel).uv(((textureX + width) * f), ((textureY + 0) * f1)).endVertex();
        vertexbuffer.vertex((x + 0), (y + 0), zLevel).uv(((textureX + 0) * f), ((textureY + 0) * f1)).endVertex();
        tessellator.end();
    }

    public static void drawTexturedModalRect(Matrix4f positionMatrix, IVertexBuilder builder, int x, int y, int textureX, int textureY, int width, int height, int totw, int toth, float parentU, float parentV) {
        float f = 1.0f / totw;
        float f1 = 1.0f / toth;
        float zLevel = 50;
        builder.vertex(positionMatrix, (x + 0), (y + height), zLevel).uv(parentU + ((textureX + 0) * f), parentV + ((textureY + height) * f1)).endVertex();
        builder.vertex(positionMatrix, (x + width), (y + height), zLevel).uv(parentU + ((textureX + width) * f), parentV + ((textureY + height) * f1)).endVertex();
        builder.vertex(positionMatrix, (x + width), (y + 0), zLevel).uv(parentU + ((textureX + width) * f), parentV + ((textureY + 0) * f1)).endVertex();
        builder.vertex(positionMatrix, (x + 0), (y + 0), zLevel).uv(parentU + ((textureX + 0) * f), parentV + ((textureY + 0) * f1)).endVertex();
    }


    /**
     * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
     */
    public static void drawTexturedModalRect(Matrix4f matrix, int x, int y, int u, int v, int width, int height) {
        float zLevel = 0.01f;
        float f = (1 / 256.0f);
        float f1 = (1 / 256.0f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.vertex(matrix, (x + 0), (y + height), zLevel).uv(((u + 0) * f), ((v + height) * f1)).endVertex();
        buffer.vertex(matrix, (x + width), (y + height), zLevel).uv(((u + width) * f), ((v + height) * f1)).endVertex();
        buffer.vertex(matrix, (x + width), (y + 0), zLevel).uv(((u + width) * f), ((v + 0) * f1)).endVertex();
        buffer.vertex(matrix, (x + 0), (y + 0), zLevel).uv(((u + 0) * f), ((v + 0) * f1)).endVertex();
        tessellator.end();
    }

    public static void renderBillboardQuadBright(MatrixStack matrixStack, IRenderTypeBuffer buffer, float scale, ResourceLocation texture) {
        renderBillboardQuadBright(matrixStack, buffer, scale, texture, DEFAULT_SETTINGS);
    }

    public static void renderBillboardQuadBright(MatrixStack matrixStack, IRenderTypeBuffer buffer, float scale, ResourceLocation texture, RenderSettings settings) {
        int b1 = settings.getBrightness() >> 16 & 65535;
        int b2 = settings.getBrightness() & 65535;
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(texture);
        matrixStack.pushPose();
        matrixStack.translate(0.5, 0.5, 0.5);
        RenderHelper.rotateToPlayer(matrixStack);
        IVertexBuilder builder = buffer.getBuffer(settings.getRenderType());
        Matrix4f matrix = matrixStack.last().pose();
        builder.vertex(matrix, -scale, -scale, 0.0f).color(settings.getR(), settings.getG(), settings.getB(), settings.getA()).uv(sprite.getU0(), sprite.getV0()).uv2(b1, b2).normal(1,0,0).endVertex();
        builder.vertex(matrix, -scale, scale, 0.0f).color(settings.getR(), settings.getG(), settings.getB(), settings.getA()).uv(sprite.getU0(), sprite.getV1()).uv2(b1, b2).normal(1,0,0).endVertex();
        builder.vertex(matrix, scale, scale, 0.0f).color(settings.getR(), settings.getG(), settings.getB(), settings.getA()).uv(sprite.getU1(), sprite.getV1()).uv2(b1, b2).normal(1,0,0).endVertex();
        builder.vertex(matrix, scale, -scale, 0.0f).color(settings.getR(), settings.getG(), settings.getB(), settings.getA()).uv(sprite.getU1(), sprite.getV0()).uv2(b1, b2).normal(1,0,0).endVertex();
        matrixStack.popPose();
    }

    public static void rotateToPlayer(MatrixStack matrixStack) {
        Quaternion rotation = Minecraft.getInstance().gameRenderer.getMainCamera().rotation();
        matrixStack.mulPose(rotation);
    }

    public static int renderText(MatrixStack matrixStack, int x, int y, String txt) {
        GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1f);

        matrixStack.pushPose();
        matrixStack.translate(0.0F, 0.0F, 32.0F);
        GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager._enableRescaleNormal();
        GlStateManager._enableLighting();
        net.minecraft.client.renderer.RenderHelper.turnBackOn();

        GlStateManager._disableLighting();
        GlStateManager._disableDepthTest();
        GlStateManager._disableBlend();
        Minecraft mc = Minecraft.getInstance();
        int width = mc.font.width(txt);
        mc.font.drawShadow(matrixStack, txt, x, y, 16777215);
        GlStateManager._enableLighting();
        GlStateManager._enableDepthTest();
        // Fixes opaque cooldown overlay a bit lower
        // TODO: check if enabled blending still screws things up down the line.
        GlStateManager._enableBlend();


        matrixStack.popPose();
        GlStateManager._disableRescaleNormal();
        GlStateManager._disableLighting();

        return width;
    }

    public static int renderText(MatrixStack matrixStack, int x, int y, String txt, int color) {
        GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0f);

        matrixStack.pushPose();
        matrixStack.translate(0.0F, 0.0F, 32.0F);
        GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager._enableRescaleNormal();
        GlStateManager._enableLighting();
        net.minecraft.client.renderer.RenderHelper.turnBackOn();

        GlStateManager._disableLighting();
        GlStateManager._disableDepthTest();
        GlStateManager._disableBlend();
        Minecraft mc = Minecraft.getInstance();
        int width = mc.font.width(txt);
        mc.font.draw(matrixStack, txt, x, y, color);
        GlStateManager._enableLighting();
        GlStateManager._enableDepthTest();
        // Fixes opaque cooldown overlay a bit lower
        // TODO: check if enabled blending still screws things up down the line.
        GlStateManager._enableBlend();


        matrixStack.popPose();
        GlStateManager._disableRescaleNormal();
        GlStateManager._disableLighting();

        return width;
    }

    /**
     * Draw a beam with some thickness.
     */
    public static void drawBeam(Matrix4f matrix, IVertexBuilder builder, TextureAtlasSprite sprite, Vector3f S, Vector3f E, Vector3f P, float width) {
        Vector3f PS = Sub(S, P);
        Vector3f SE = Sub(E, S);

        Vector3f normal = Cross(PS, SE);
        normal.normalize();

        Vector3f half = Mul(normal, width);
        Vector3f p1 = Add(S, half);
        Vector3f p2 = Sub(S, half);
        Vector3f p3 = Add(E, half);
        Vector3f p4 = Sub(E, half);

        drawQuad(matrix, builder, sprite, p1, p3, p4, p2, DEFAULT_SETTINGS);
    }

    /**
     * Draw a beam with some thickness.
     */
    public static void drawBeam(Matrix4f matrix, IVertexBuilder buffer, TextureAtlasSprite sprite, Vector3f S, Vector3f E, Vector3f P, RenderSettings settings) {
        Vector3f PS = Sub(S, P);
        Vector3f SE = Sub(E, S);

        Vector3f normal = Cross(PS, SE);
        normal.normalize();

        Vector3f half = Mul(normal, settings.getWidth());
        Vector3f p1 = Add(S, half);
        Vector3f p2 = Sub(S, half);
        Vector3f p3 = Add(E, half);
        Vector3f p4 = Sub(E, half);

        drawQuad(matrix, buffer, sprite, p1, p3, p4, p2, settings);
    }

    public static void drawQuad(Matrix4f matrix, IVertexBuilder buffer, TextureAtlasSprite sprite, Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4,
                                RenderSettings settings) {
        int b1 = settings.getBrightness() >> 16 & 65535;
        int b2 = settings.getBrightness() & 65535;

        vt(buffer, matrix, p1.x(), p1.y(), p1.z(), sprite.getU0(), sprite.getV0(), b1, b2, settings.getR(), settings.getG(), settings.getB(), settings.getA());
        vt(buffer, matrix, p2.x(), p2.y(), p2.z(), sprite.getU1(), sprite.getV0(), b1, b2, settings.getR(), settings.getG(), settings.getB(), settings.getA());
        vt(buffer, matrix, p3.x(), p3.y(), p3.z(), sprite.getU1(), sprite.getV1(), b1, b2, settings.getR(), settings.getG(), settings.getB(), settings.getA());
        vt(buffer, matrix, p4.x(), p4.y(), p4.z(), sprite.getU0(), sprite.getV1(), b1, b2, settings.getR(), settings.getG(), settings.getB(), settings.getA());
    }

    public static void drawQuad(Matrix4f matrix, IVertexBuilder buffer, TextureAtlasSprite sprite, Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4,
                                boolean opposite,
                                RenderSettings settings) {
        int b1 = settings.getBrightness() >> 16 & 65535;
        int b2 = settings.getBrightness() & 65535;

        if (opposite) {
            vt(buffer, matrix, p1.x(), p1.y(), p1.z(), sprite.getU0(), sprite.getV0(), b1, b2, settings.getR(), settings.getG(), settings.getB(), settings.getA());
            vt(buffer, matrix, p2.x(), p2.y(), p2.z(), sprite.getU1(), sprite.getV0(), b1, b2, settings.getR(), settings.getG(), settings.getB(), settings.getA());
            vt(buffer, matrix, p3.x(), p3.y(), p3.z(), sprite.getU1(), sprite.getV1(), b1, b2, settings.getR(), settings.getG(), settings.getB(), settings.getA());
            vt(buffer, matrix, p4.x(), p4.y(), p4.z(), sprite.getU0(), sprite.getV1(), b1, b2, settings.getR(), settings.getG(), settings.getB(), settings.getA());
        } else {
            vt(buffer, matrix, p4.x(), p4.y(), p4.z(), sprite.getU0(), sprite.getV1(), b1, b2, settings.getR(), settings.getG(), settings.getB(), settings.getA());
            vt(buffer, matrix, p3.x(), p3.y(), p3.z(), sprite.getU1(), sprite.getV1(), b1, b2, settings.getR(), settings.getG(), settings.getB(), settings.getA());
            vt(buffer, matrix, p2.x(), p2.y(), p2.z(), sprite.getU1(), sprite.getV0(), b1, b2, settings.getR(), settings.getG(), settings.getB(), settings.getA());
            vt(buffer, matrix, p1.x(), p1.y(), p1.z(), sprite.getU0(), sprite.getV0(), b1, b2, settings.getR(), settings.getG(), settings.getB(), settings.getA());
        }
    }

    public static void drawQuad(Matrix4f matrix, IVertexBuilder buffer, TextureAtlasSprite sprite, Direction side, boolean opposite, float offset, RenderSettings settings) {
        switch (side) {
            case DOWN:
                drawQuad(matrix, buffer, sprite, new Vector3f(0, offset, 1), new Vector3f(1, offset, 1), new Vector3f(1, offset, 0), new Vector3f(0, offset, 0), opposite, settings);
                break;
            case UP:
                drawQuad(matrix, buffer, sprite, new Vector3f(1, 1-offset, 1), new Vector3f(0, 1-offset, 1), new Vector3f(0, 1-offset, 0), new Vector3f(1, 1-offset, 0), opposite, settings);
                break;
            case NORTH:
                drawQuad(matrix, buffer, sprite, new Vector3f(0, 0, offset), new Vector3f(1, 0, offset), new Vector3f(1, 1, offset), new Vector3f(0, 1, offset), opposite, settings);
                break;
            case SOUTH:
                drawQuad(matrix, buffer, sprite, new Vector3f(0, 1, 1-offset), new Vector3f(1, 1, 1-offset), new Vector3f(1, 0, 1-offset), new Vector3f(0, 0, 1-offset), opposite, settings);
                break;
            case WEST:
                drawQuad(matrix, buffer, sprite, new Vector3f(offset, 0, 0), new Vector3f(offset, 1, 0), new Vector3f(offset, 1, 1), new Vector3f(offset, 0, 1), opposite, settings);
                break;
            case EAST:
                drawQuad(matrix, buffer, sprite, new Vector3f(1-offset, 0, 1), new Vector3f(1-offset, 1, 1), new Vector3f(1-offset, 1, 0), new Vector3f(1-offset, 0, 0), opposite, settings);
                break;
        }
    }

    public static void vt(IVertexBuilder renderer, Matrix4f matrix, float x, float y, float z, float u, float v) {
        renderer
                .vertex(matrix, x, y, z)
                .color(1.0f, 1.0f, 1.0f, 1.0f)
                .uv(u, v)
                .uv2(MAX_BRIGHTNESS)
                .normal(1, 0, 0)
                .endVertex();
    }

    public static void vt(IVertexBuilder renderer, Matrix4f matrix, float x, float y, float z, float u, float v, int lu, int lv, int r, int g, int b, int a) {
        renderer
                .vertex(matrix, x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .uv2(lu, lv)
                .normal(1, 0, 0)
                .endVertex();
    }

    public static void vt(IVertexBuilder renderer, Matrix4f matrix, float x, float y, float z, float u, float v, int lu, int lv, float r, float g, float b, float a) {
        renderer
                .vertex(matrix, x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .uv2(lu, lv)
                .normal(1, 0, 0)
                .endVertex();
    }

    public static void putVertex(Matrix4f matrix, IVertexConsumer builder, IPosition normal,
                                 double x, double y, double z, float u, float v, TextureAtlasSprite sprite, float r, float g, float b, float a) {
        Vector4f vector4f = new Vector4f((float)x, (float)y, (float)z, 1.0F);
        vector4f.transform(matrix);
        putVertex(builder, normal, vector4f.x(), vector4f.y(), vector4f.z(), u, v, sprite, r, g, b, a);
    }

    public static void putVertex(IVertexConsumer builder, IPosition normal,
                                 double x, double y, double z, float u, float v, TextureAtlasSprite sprite, float r, float g, float b, float a) {
        ImmutableList<VertexFormatElement> elements = builder.getVertexFormat().getElements().asList();
        for (int e = 0; e < elements.size(); e++) {
            switch (elements.get(e).getUsage()) {
                case POSITION:
                    builder.put(e, (float)x, (float)y, (float)z);
                    break;
                case COLOR:
                    builder.put(e, r, g, b, a);
                    break;
                case UV:
                    switch (elements.get(e).getIndex()) {
                        case 0:
                            float iu = sprite.getU(u);
                            float iv = sprite.getV(v);
                            builder.put(e, iu, iv);
                            break;
                        case 2:
                            builder.put(e, (short) 0, (short) 0);
//                            builder.put(e, 0f, 1f);
                            break;
                        default:
                            builder.put(e);
                            break;
                    }
                    break;
                case NORMAL:
                    builder.put(e, (float) normal.x(), (float) normal.y(), (float) normal.z());
                    break;
                default:
                    builder.put(e);
                    break;
            }
        }
    }

    private static Vector3f Cross(Vector3f a, Vector3f b) {
        float x = a.y() * b.z() - a.z() * b.y();
        float y = a.z() * b.x() - a.x() * b.z();
        float z = a.x() * b.y() - a.y() * b.x();
        return new Vector3f(x, y, z);
    }

    private static Vector3f Sub(Vector3f a, Vector3f b) {
        return new Vector3f(a.x() - b.x(), a.y() - b.y(), a.z() - b.z());
    }

    private static Vector3f Add(Vector3f a, Vector3f b) {
        return new Vector3f(a.x() + b.x(), a.y() + b.y(), a.z() + b.z());
    }

    private static Vector3f Mul(Vector3f a, float f) {
        return new Vector3f(a.x() * f, a.y() * f, a.z() * f);
    }

    public static void renderHighLightedBlocksOutline(IVertexBuilder buffer, Matrix4f positionMatrix, float mx, float my, float mz, float r, float g, float b, float a) {
        buffer.vertex(positionMatrix, mx, my, mz).color(r, g, b, a).endVertex();
        buffer.vertex(positionMatrix, mx + 1, my, mz).color(r, g, b, a).endVertex();
        buffer.vertex(positionMatrix, mx, my, mz).color(r, g, b, a).endVertex();
        buffer.vertex(positionMatrix, mx, my + 1, mz).color(r, g, b, a).endVertex();
        buffer.vertex(positionMatrix, mx, my, mz).color(r, g, b, a).endVertex();
        buffer.vertex(positionMatrix, mx, my, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(positionMatrix, mx + 1, my + 1, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(positionMatrix, mx, my + 1, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(positionMatrix, mx + 1, my + 1, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(positionMatrix, mx + 1, my, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(positionMatrix, mx + 1, my + 1, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(positionMatrix, mx + 1, my + 1, mz).color(r, g, b, a).endVertex();

        buffer.vertex(positionMatrix, mx, my + 1, mz).color(r, g, b, a).endVertex();
        buffer.vertex(positionMatrix, mx, my + 1, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(positionMatrix, mx, my + 1, mz).color(r, g, b, a).endVertex();
        buffer.vertex(positionMatrix, mx + 1, my + 1, mz).color(r, g, b, a).endVertex();

        buffer.vertex(positionMatrix, mx + 1, my, mz).color(r, g, b, a).endVertex();
        buffer.vertex(positionMatrix, mx + 1, my, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(positionMatrix, mx + 1, my, mz).color(r, g, b, a).endVertex();
        buffer.vertex(positionMatrix, mx + 1, my + 1, mz).color(r, g, b, a).endVertex();

        buffer.vertex(positionMatrix, mx, my, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(positionMatrix, mx + 1, my, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(positionMatrix, mx, my, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(positionMatrix, mx, my + 1, mz + 1).color(r, g, b, a).endVertex();
    }

    public static void fill(MatrixStack matrixStack, IRenderTypeBuffer buffer, int x1, int y1, int x2, int y2, int color,
                            int lightmap) {
        Matrix4f positionMatrix = matrixStack.last().pose();
        int swapper;
        if (x1 < x2) {
            swapper = x1;
            x1 = x2;
            x2 = swapper;
        }

        if (y1 < y2) {
            swapper = y1;
            y1 = y2;
            y2 = swapper;
        }

        float a = (color >> 24 & 255) / 255.0F;
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        IVertexBuilder builder = buffer.getBuffer(CustomRenderTypes.QUADS_NOTEXTURE);
//        RenderSystem.enableBlend();
//        RenderSystem.disableTexture();
//        RenderSystem.defaultBlendFunc();
//        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        builder.vertex(positionMatrix, x1, y2, -1F).color(r, g, b, a).uv2(lightmap).endVertex();
        builder.vertex(positionMatrix, x2, y2, -1F).color(r, g, b, a).uv2(lightmap).endVertex();
        builder.vertex(positionMatrix, x2, y1, -1F).color(r, g, b, a).uv2(lightmap).endVertex();
        builder.vertex(positionMatrix, x1, y1, -1F).color(r, g, b, a).uv2(lightmap).endVertex();
//        builder.finishDrawing();
//        RenderSystem.enableTexture();
//        RenderSystem.disableBlend();
    }

}
