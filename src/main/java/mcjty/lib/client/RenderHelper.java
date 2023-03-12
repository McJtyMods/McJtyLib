package mcjty.lib.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import mcjty.lib.base.StyleConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

import static net.minecraft.client.renderer.LightTexture.FULL_BLOCK;
import static net.minecraft.client.renderer.LightTexture.FULL_SKY;

public class RenderHelper {

    public static float rot = 0.0f;

    public static final int MAX_BRIGHTNESS = 0xf000f0;

    public static final RenderSettings DEFAULT_SETTINGS = RenderSettings.builder()
            .color(255, 255, 255)
            .alpha(128)
            .build();

    public static final RenderSettings FULLBRIGHT_SETTINGS = RenderSettings.builder()
            .color(255, 255, 255)
            .alpha(MAX_BRIGHTNESS)
            .build();


    public static void renderText(Font font, String text, int x, int y, int color, PoseStack poseStack, MultiBufferSource buffer, int lightmapValue) {
        font.drawInBatch(text, x, y, color, false, poseStack.last().pose(), buffer, false, 0, lightmapValue);
    }

    public static void renderModel(BlockRenderDispatcher renderer, PoseStack stack, VertexConsumer buffer, BlockState state, BakedModel model,
                                   float r, float g, float b, int combinedLight, int combinedOverlay, ModelData modelData, RenderType renderType) {
        renderer.getModelRenderer().renderModel(stack.last(), buffer, state, model, r, g, b, combinedLight, combinedOverlay, modelData, renderType);
    }

    public static void line(VertexConsumer builder, PoseStack matrixStack, float x1, float y1, float z1, float x2, float y2, float z2,
                            float red, float green, float blue, float alpha) {
        builder.vertex(matrixStack.last().pose(), x1, y1, 0).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrixStack.last().pose(), x2, y2, 0).color(red, green, blue, alpha).endVertex();
    }

    // Adjust the given matrix to the specified direction
    public static void adjustTransformToDirection(PoseStack matrixStack, Direction facing) {
        matrixStack.translate(0.5F, 0.5F, 0.5F);

        switch (facing) {
            case DOWN -> {
                matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            }
            case UP -> {
                matrixStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
            }
            case NORTH -> {
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(-180.0F));
            }
            case SOUTH -> {
            }
            case WEST -> {
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
            }
            case EAST -> {
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
            }
        }

        matrixStack.translate(-0.5F, -0.5F, -0.5F);
    }

    public static void renderNorthSouthQuad(PoseStack poseStack, VertexConsumer builder, TextureAtlasSprite sprite, ModelBuilder.FaceRotation rotation, float offset) {
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

    private static void renderEntity(PoseStack matrixStack, Entity entity, int xPos, int yPos, float scale) {
        matrixStack.pushPose();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        // @todo 1.17 GlStateManager._enableRescaleNormal();
        // @todo 1.17 GlStateManager._enableColorMaterial();
        matrixStack.pushPose();
        matrixStack.translate(xPos + 8, yPos + 16, 50F);
        matrixStack.scale(-scale, scale, scale);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180F));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(135F));
        // @todo 1.17 com.mojang.blaze3d.platform.Lighting.turnBackOn();
        matrixStack.mulPose(Vector3f.YN.rotationDegrees(135F));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(rot));
//        GlStateManager.rotatef(0.0F, 1.0F, 0.0F, 0.0F);
//        entity.renderYawOffset = entity.rotationYaw = entity.prevRotationYaw = entity.prevRotationYawHead = entity.rotationYawHead = 0;//this.rotateTurret;
        entity.setXRot(0.0F);
        matrixStack.translate(0.0F, (float) entity.getMyRidingOffset(), 0.0F);
        // @todo 1.15
//        Minecraft.getInstance().getRenderManager().playerViewY = 180F;
//        Minecraft.getInstance().getRenderManager().renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        matrixStack.popPose();
        // @todo 1.17 com.mojang.blaze3d.platform.Lighting.turnOff();

        // @todo 1.17 GlStateManager._disableRescaleNormal();
        matrixStack.translate(0F, 0F, 0.0F);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        // @todo 1.17 GlStateManager._enableRescaleNormal();
        int i1 = FULL_BLOCK;
        int k1 = FULL_BLOCK;

        // @todo 1.14 check if right?
        // @todo 1.15
//        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, i1 / 1.0F, k1 / 1.0F);
//        OpenGlHelper.setLightmapTextureCoords(GLX.GL_TEXTURE1, i1 / 1.0F, k1 / 1.0F);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        // @todo 1.17 GlStateManager._disableRescaleNormal();
        // @todo 1.17 com.mojang.blaze3d.platform.Lighting.turnOff();
        // @todo 1.17 GlStateManager._disableLighting();
        GlStateManager._disableDepthTest();
        matrixStack.popPose();
    }

    public static boolean renderObject(PoseStack matrixStack, int x, int y, Object itm, boolean highlight) {
        if (itm instanceof Entity) {
            renderEntity(matrixStack, (Entity) itm, x, y, 10F);
            return true;
        }
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        return renderObject(matrixStack, itemRenderer, x, y, itm, highlight, 100);
    }

    public static boolean renderObject(PoseStack matrixStack, ItemRenderer itemRender, int x, int y, Object itm, boolean highlight, float lvl) {
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

    private static boolean renderIcon(PoseStack matrixStack, ItemRenderer itemRender, TextureAtlasSprite itm, int xo, int yo, boolean highlight) {
        //itemRender.draw(xo, yo, itm, 16, 16); //TODO: Make
        return true;
    }

    public static boolean renderFluidStack(FluidStack fluidStack, int x, int y, boolean highlight) {
        Fluid fluid = fluidStack.getFluid();
        if (fluid == null) {
            return false;
        }

        IClientFluidTypeExtensions attributes = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        ResourceLocation fluidStill = attributes.getStillTexture(fluidStack);
        TextureAtlasSprite fluidStillSprite = null;
        if (fluidStill != null) {
            fluidStillSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);
        }
        if (fluidStillSprite == null) {
            return false;
        }

        int fluidColor = attributes.getTintColor(fluidStack);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
//        Minecraft.getInstance().getEntityRenderDispatcher().textureManager.bindForSetup(InventoryMenu.BLOCK_ATLAS);
        setGLColorFromInt(fluidColor);
        drawFluidTexture(x, y, fluidStillSprite, 100);

        return true;
    }

    private static void drawFluidTexture(double xCoord, double yCoord, TextureAtlasSprite textureSprite, double zLevel) {
        float uMin = textureSprite.getU0();
        float uMax = textureSprite.getU1();
        float vMin = textureSprite.getV0();
        float vMax = textureSprite.getV1();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder vertexBuffer = tessellator.getBuilder();
        vertexBuffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
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

        RenderSystem.setShaderColor(red, green, blue, 1.0F);
    }


    public static boolean renderItemStackWithCount(PoseStack matrixStack, ItemRenderer itemRender, ItemStack itm, int xo, int yo, boolean highlight) {
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
    }

    public static boolean renderItemStack(PoseStack matrixStack, ItemRenderer itemRender, ItemStack itm, int x, int y, String txt, boolean highlight) {
        RenderSystem.setShaderColor(1F, 1F, 1F, 1f);

        boolean rc = false;
        if (highlight) {
            // @todo 1.17 RenderSystem.disableLighting();
            drawVerticalGradientRect(x, y, x + 16, y + 16, 0x80ffffff, 0xffffffff);
        }
        if (!itm.isEmpty() && itm.getItem() != null) {
            rc = true;
            matrixStack.pushPose();
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            // @todo 1.17 enderSystem.enableRescaleNormal();
            // @todo 1.17 enderSystem.enableLighting();
            // @todo 1.17 om.mojang.blaze3d.platform.Lighting.turnBackOn();
            // @todo 1.14 check if right?
            // @todo 1.15
            // @todo 1.17 RenderSystem.glMultiTexCoord2f(GL13.GL_TEXTURE1, 240, 240);
//            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, short1 / 1.0F, short2 / 1.0F);

            itemRender.renderAndDecorateItem(itm, x, y);
//            itemRender.renderGuiItemDecorations();
            renderGuiItemDecorations(itemRender, Minecraft.getInstance().font, itm, x, y, txt, txt.length() - 2);
            matrixStack.popPose();
            // @todo 1.17 RenderSystem.disableRescaleNormal();
            // @todo 1.17 RenderSystem.disableLighting();
        }

        return rc;
    }

    private static void renderGuiItemDecorations(ItemRenderer itemRender, Font font, ItemStack stack, int x, int y, @Nullable String text, int scaled) {
        if (!stack.isEmpty()) {
            PoseStack posestack = new PoseStack();
            if (stack.getCount() != 1 || text != null) {
                String s = text == null ? String.valueOf(stack.getCount()) : text;
                posestack.translate(0.0D, 0.0D, itemRender.blitOffset + 200.0F);
                MultiBufferSource.BufferSource source = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

                if (scaled >= 2) {
                    posestack.pushPose();
                    posestack.scale(.5f, .5f, .5f);
                    font.drawInBatch(s, ((x + 19 - 2) * 2 - 1 - font.width(s)), y * 2 + 24, 16777215, true, posestack.last().pose(), source, false, 0, 15728880);
                    posestack.popPose();
                } else if (scaled == 1) {
                    posestack.pushPose();
                    posestack.scale(.75f, .75f, .75f);
                    font.drawInBatch(s, ((x - 2) * 1.34f + 24 - font.width(s)), y * 1.34f + 14, 16777215, true, posestack.last().pose(), source, false, 0, 15728880);
                    posestack.popPose();
                } else {
                    font.drawInBatch(s, (x + 19 - 2 - font.width(s)), (y + 6 + 3), 16777215, true, posestack.last().pose(), source, false, 0, 15728880);
                }

                source.endBatch();
            }

            if (stack.isBarVisible()) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableBlend();
                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder bufferbuilder = tesselator.getBuilder();
                int i = stack.getBarWidth();
                int j = stack.getBarColor();
                fillRect(bufferbuilder, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
                fillRect(bufferbuilder, x + 2, y + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
                RenderSystem.enableBlend();
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }

            LocalPlayer localplayer = Minecraft.getInstance().player;
            float f = localplayer == null ? 0.0F : localplayer.getCooldowns().getCooldownPercent(stack.getItem(), Minecraft.getInstance().getFrameTime());
            if (f > 0.0F) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                Tesselator tesselator1 = Tesselator.getInstance();
                BufferBuilder bufferbuilder1 = tesselator1.getBuilder();
                fillRect(bufferbuilder1, x, y + Mth.floor(16.0F * (1.0F - f)), 16, Mth.ceil(16.0F * f), 255, 255, 255, 127);
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }
        }
    }

    private static void fillRect(BufferBuilder pRenderer, int pX, int pY, int pWidth, int pHeight, int pRed, int pGreen, int pBlue, int pAlpha) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        pRenderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        pRenderer.vertex(pX + 0, pY + 0, 0.0D).color(pRed, pGreen, pBlue, pAlpha).endVertex();
        pRenderer.vertex(pX + 0, pY + pHeight, 0.0D).color(pRed, pGreen, pBlue, pAlpha).endVertex();
        pRenderer.vertex(pX + pWidth, pY + pHeight, 0.0D).color(pRed, pGreen, pBlue, pAlpha).endVertex();
        pRenderer.vertex(pX + pWidth, pY + 0, 0.0D).color(pRed, pGreen, pBlue, pAlpha).endVertex();
        BufferUploader.drawWithShader(pRenderer.end());
    }


    /**
     * Draw with the WorldRenderer
     */
    private static void draw(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        renderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        renderer.vertex((x + 0), (y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.vertex((x + 0), (y + height), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.vertex((x + width), (y + height), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.vertex((x + width), (y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
        Tesselator.getInstance().end();
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
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        // @todo 1.17 GlStateManager._disableAlphaTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);

        // @todo 1.17 GlStateManager._shadeModel(GL11.GL_SMOOTH);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(x2, y1, zLevel).color(f1, f2, f3, f).endVertex();
        buffer.vertex(x1, y1, zLevel).color(f1, f2, f3, f).endVertex();
        buffer.vertex(x1, y2, zLevel).color(f5, f6, f7, f4).endVertex();
        buffer.vertex(x2, y2, zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.end();

        // @todo 1.17 GlStateManager._shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        // @todo 1.17 GlStateManager._enableAlphaTest();
        RenderSystem.enableTexture();
    }

    /**
     * Draws a rectangle with a horizontal gradient between the specified colors.
     * x2 and y2 are not included.
     */
    public static void drawHorizontalGradientRect(PoseStack matrixStack, int x1, int y1, int x2, int y2, int color1, int color2) {
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
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        // @todo 1.17 GlStateManager._shadeModel(GL11.GL_SMOOTH);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(x1, y1, zLevel).color(f1, f2, f3, f).endVertex();
        buffer.vertex(x1, y2, zLevel).color(f1, f2, f3, f).endVertex();
        buffer.vertex(x2, y2, zLevel).color(f5, f6, f7, f4).endVertex();
        buffer.vertex(x2, y1, zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.end();
        // @todo 1.17 GlStateManager._shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        // @todo 1.17 GlStateManager._enableAlphaTest();
        RenderSystem.enableTexture();
    }

    /**
     * Draws a rectangle with a horizontal gradient between the specified colors.
     * x2 and y2 are not included.
     */
    public static void drawHorizontalGradientRect(PoseStack matrixStack, MultiBufferSource buffer, int x1, int y1, int x2, int y2, int color1, int color2, int lightmap) {
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

        VertexConsumer builder = buffer.getBuffer(CustomRenderTypes.QUADS_NOTEXTURE);

        Matrix4f positionMatrix = matrixStack.last().pose();
        builder.vertex(positionMatrix, x1, y1, zLevel).color(f1, f2, f3, f).uv2(lightmap).endVertex();
        builder.vertex(positionMatrix, x1, y2, zLevel).color(f1, f2, f3, f).uv2(lightmap).endVertex();
        builder.vertex(positionMatrix, x2, y2, zLevel).color(f5, f6, f7, f4).uv2(lightmap).endVertex();
        builder.vertex(positionMatrix, x2, y1, zLevel).color(f5, f6, f7, f4).uv2(lightmap).endVertex();
    }

    public static void drawHorizontalLine(PoseStack matrixStack, int x1, int y1, int x2, int color) {
        GuiComponent.fill(matrixStack, x1, y1, x2, y1 + 1, color);
    }

    public static void drawVerticalLine(PoseStack matrixStack, int x1, int y1, int y2, int color) {
        GuiComponent.fill(matrixStack, x1, y1, x1 + 1, y2, color);
    }

    // Draw a small triangle. x,y is the coordinate of the left point
    public static void drawLeftTriangle(PoseStack matrixStack, int x, int y, int color) {
        drawVerticalLine(matrixStack, x, y, y, color);
        drawVerticalLine(matrixStack, x + 1, y - 1, y + 1, color);
        drawVerticalLine(matrixStack, x + 2, y - 2, y + 2, color);
    }

    // Draw a small triangle. x,y is the coordinate of the right point
    public static void drawRightTriangle(PoseStack matrixStack, int x, int y, int color) {
        drawVerticalLine(matrixStack, x, y, y, color);
        drawVerticalLine(matrixStack, x - 1, y - 1, y + 1, color);
        drawVerticalLine(matrixStack, x - 2, y - 2, y + 2, color);
    }

    // Draw a small triangle. x,y is the coordinate of the top point
    public static void drawUpTriangle(PoseStack matrixStack, int x, int y, int color) {
        drawHorizontalLine(matrixStack, x, y, x, color);
        drawHorizontalLine(matrixStack, x - 1, y + 1, x + 1, color);
        drawHorizontalLine(matrixStack, x - 2, y + 2, x + 2, color);
    }

    // Draw a small triangle. x,y is the coordinate of the bottom point
    public static void drawDownTriangle(PoseStack matrixStack, int x, int y, int color) {
        drawHorizontalLine(matrixStack, x, y, x, color);
        drawHorizontalLine(matrixStack, x - 1, y - 1, x + 1, color);
        drawHorizontalLine(matrixStack, x - 2, y - 2, x + 2, color);
    }

    public static void drawColorLogic(int x, int y, int width, int height, int red, int green, int blue, GlStateManager.LogicOp colorLogic) {
        GlStateManager._disableTexture();
        GlStateManager._enableColorLogicOp();
        GlStateManager._logicOp(colorLogic.value);

        draw(Tesselator.getInstance().getBuilder(), x, y, width, height, red, green, blue, 255);

        GlStateManager._disableColorLogicOp();
        GlStateManager._enableTexture();
    }

    /**
     * Draw a button box. x2 and y2 are not included.
     */
    public static void drawThickButtonBox(PoseStack matrixStack, int x1, int y1, int x2, int y2, int bright, int average, int dark) {
        GuiComponent.fill(matrixStack, x1 + 2, y1 + 2, x2 - 2, y2 - 2, average);
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
    public static void drawThinButtonBox(PoseStack matrixStack, int x1, int y1, int x2, int y2, int bright, int average, int dark) {
        GuiComponent.fill(matrixStack, x1 + 1, y1 + 1, x2 - 1, y2 - 1, average);
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
    public static void drawThinButtonBoxGradient(PoseStack matrixStack, int x1, int y1, int x2, int y2, int bright, int average1, int average2, int dark) {
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
    public static void drawFlatButtonBox(PoseStack matrixStack, int x1, int y1, int x2, int y2, int bright, int average, int dark) {
        drawBeveledBox(matrixStack, x1, y1, x2, y2, bright, dark, average);
    }

    /**
     * Draw a button box. x2 and y2 are not included.
     */
    public static void drawFlatButtonBox(PoseStack matrixStack, MultiBufferSource buffer, int x1, int y1, int x2, int y2, int bright, int average, int dark, int lightmap) {
        drawBeveledBox(matrixStack, buffer, x1, y1, x2, y2, bright, dark, average, lightmap);
    }

    /**
     * Draw a button box. x2 and y2 are not included.
     */
    public static void drawFlatButtonBoxGradient(PoseStack matrixStack, int x1, int y1, int x2, int y2, int bright, int average1, int average2, int dark) {
        drawVerticalGradientRect(x1 + 1, y1 + 1, x2 - 1, y2 - 1, average2, average1);
        drawHorizontalLine(matrixStack, x1, y1, x2 - 1, bright);
        drawVerticalLine(matrixStack, x1, y1, y2 - 1, bright);
        drawVerticalLine(matrixStack, x2 - 1, y1, y2 - 1, dark);
        drawHorizontalLine(matrixStack, x1, y2 - 1, x2, dark);
    }

    /**
     * Draw a beveled box. x2 and y2 are not included.
     */
    public static void drawBeveledBox(PoseStack matrixStack, int x1, int y1, int x2, int y2, int topleftcolor, int botrightcolor, int fillcolor) {
        if (fillcolor != -1) {
            GuiComponent.fill(matrixStack, x1 + 1, y1 + 1, x2 - 1, y2 - 1, fillcolor);
        }
        drawHorizontalLine(matrixStack, x1, y1, x2 - 1, topleftcolor);
        drawVerticalLine(matrixStack, x1, y1, y2 - 1, topleftcolor);
        drawVerticalLine(matrixStack, x2 - 1, y1, y2 - 1, botrightcolor);
        drawHorizontalLine(matrixStack, x1, y2 - 1, x2, botrightcolor);
    }

    /**
     * Draw a beveled box. x2 and y2 are not included. Use this version for GUI's
     */
    public static void drawBeveledBox(PoseStack matrixStack, MultiBufferSource buffer, int x1, int y1, int x2, int y2, int topleftcolor, int botrightcolor, int fillcolor, int lightmap) {
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
    public static void drawThickBeveledBox(PoseStack matrixStack, int x1, int y1, int x2, int y2, int thickness, int topleftcolor, int botrightcolor, int fillcolor) {
        if (fillcolor != -1) {
            GuiComponent.fill(matrixStack, x1 + 1, y1 + 1, x2 - 1, y2 - 1, fillcolor);
        }
        GuiComponent.fill(matrixStack, x1, y1, x2 - 1, y1 + thickness, topleftcolor);
        GuiComponent.fill(matrixStack, x1, y1, x1 + thickness, y2 - 1, topleftcolor);
        GuiComponent.fill(matrixStack, x2 - thickness, y1, x2, y2 - 1, botrightcolor);
        GuiComponent.fill(matrixStack, x1, y2 - thickness, x2, y2, botrightcolor);
    }

    /**
     * Draw a beveled box. x2 and y2 are not included.
     */
    public static void drawFlatBox(PoseStack matrixStack, int x1, int y1, int x2, int y2, int border, int fill) {
        if (fill != -1) {
            GuiComponent.fill(matrixStack, x1 + 1, y1 + 1, x2 - 1, y2 - 1, fill);
        }
        drawHorizontalLine(matrixStack, x1, y1, x2 - 1, border);
        drawVerticalLine(matrixStack, x1, y1, y2 - 1, border);
        drawVerticalLine(matrixStack, x2 - 1, y1, y2 - 1, border);
        drawHorizontalLine(matrixStack, x1, y2 - 1, x2, border);
    }

    public static void drawTexturedModalRect(PoseStack poseStack, VertexConsumer builder, int x, int y, int textureX, int textureY, int width, int height, int totw, int toth, float parentU, float parentV) {
        Matrix4f matrix = poseStack.last().pose();
        float f = 1.0f / totw;
        float f1 = 1.0f / toth;
        float zLevel = 50;
        builder.vertex(matrix, (x + 0), (y + height), zLevel).uv(parentU + ((textureX + 0) * f), parentV + ((textureY + height) * f1)).endVertex();
        builder.vertex(matrix, (x + width), (y + height), zLevel).uv(parentU + ((textureX + width) * f), parentV + ((textureY + height) * f1)).endVertex();
        builder.vertex(matrix, (x + width), (y + 0), zLevel).uv(parentU + ((textureX + width) * f), parentV + ((textureY + 0) * f1)).endVertex();
        builder.vertex(matrix, (x + 0), (y + 0), zLevel).uv(parentU + ((textureX + 0) * f), parentV + ((textureY + 0) * f1)).endVertex();
    }


    /**
     * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
     */
    public static void drawTexturedModalRect(PoseStack poseStack, int x, int y, int u, int v, int width, int height) {
        Matrix4f matrix = poseStack.last().pose();
        float zLevel = 0.01f;
        float f = (1 / 256.0f);
        float f1 = (1 / 256.0f);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, (x + 0), (y + height), zLevel).uv(((u + 0) * f), ((v + height) * f1)).endVertex();
        buffer.vertex(matrix, (x + width), (y + height), zLevel).uv(((u + width) * f), ((v + height) * f1)).endVertex();
        buffer.vertex(matrix, (x + width), (y + 0), zLevel).uv(((u + width) * f), ((v + 0) * f1)).endVertex();
        buffer.vertex(matrix, (x + 0), (y + 0), zLevel).uv(((u + 0) * f), ((v + 0) * f1)).endVertex();
        tessellator.end();
    }

    /**
     * Render a billboard in four sections for better depth sorting
     */
    public static void renderSplitBillboard(PoseStack matrixStack, VertexConsumer buffer, float scale, Vec3 offset, ResourceLocation texture) {
        int b1 = FULL_SKY;
        int b2 = FULL_BLOCK;
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
        matrixStack.pushPose();
        matrixStack.translate(0.5, 0.5 + offset.y, 0.5);
        RenderHelper.rotateToPlayer(matrixStack);
        Matrix4f matrix = matrixStack.last().pose();

        float u0 = sprite.getU0();
        float v0 = sprite.getV0();
        float u1 = sprite.getU1();
        float v1 = sprite.getV1();
        float um = (u0 + u1) / 2f;
        float vm = (v0 + v1) / 2f;

        buffer.vertex(matrix, -scale, -scale, 0.0f).color(255, 255, 255, 255).uv(u0, v0).uv2(b1, b2).normal(1, 0, 0).endVertex();
        buffer.vertex(matrix, -scale, 0.0f, 0.0f).color(255, 255, 255, 255).uv(u0, vm).uv2(b1, b2).normal(1, 0, 0).endVertex();
        buffer.vertex(matrix, 0.0f, 0.0f, 0.0f).color(255, 255, 255, 255).uv(um, vm).uv2(b1, b2).normal(1, 0, 0).endVertex();
        buffer.vertex(matrix, 0.0f, -scale, 0.0f).color(255, 255, 255, 255).uv(um, v0).uv2(b1, b2).normal(1, 0, 0).endVertex();

        buffer.vertex(matrix, 0.0f, 0.0f, 0.0f).color(255, 255, 255, 255).uv(um, vm).uv2(b1, b2).normal(1, 0, 0).endVertex();
        buffer.vertex(matrix, 0.0f, scale, 0.0f).color(255, 255, 255, 255).uv(um, v1).uv2(b1, b2).normal(1, 0, 0).endVertex();
        buffer.vertex(matrix, scale, scale, 0.0f).color(255, 255, 255, 255).uv(u1, v1).uv2(b1, b2).normal(1, 0, 0).endVertex();
        buffer.vertex(matrix, scale, 0.0f, 0.0f).color(255, 255, 255, 255).uv(u1, vm).uv2(b1, b2).normal(1, 0, 0).endVertex();

        buffer.vertex(matrix, 0.0f, -scale, 0.0f).color(255, 255, 255, 255).uv(um, v0).uv2(b1, b2).normal(1, 0, 0).endVertex();
        buffer.vertex(matrix, 0.0f, 0.0f, 0.0f).color(255, 255, 255, 255).uv(um, vm).uv2(b1, b2).normal(1, 0, 0).endVertex();
        buffer.vertex(matrix, scale, 0.0f, 0.0f).color(255, 255, 255, 255).uv(u1, vm).uv2(b1, b2).normal(1, 0, 0).endVertex();
        buffer.vertex(matrix, scale, -scale, 0.0f).color(255, 255, 255, 255).uv(u1, v0).uv2(b1, b2).normal(1, 0, 0).endVertex();

        buffer.vertex(matrix, -scale, 0.0f, 0.0f).color(255, 255, 255, 255).uv(u0, vm).uv2(b1, b2).normal(1, 0, 0).endVertex();
        buffer.vertex(matrix, -scale, scale, 0.0f).color(255, 255, 255, 255).uv(u0, v1).uv2(b1, b2).normal(1, 0, 0).endVertex();
        buffer.vertex(matrix, 0.0f, scale, 0.0f).color(255, 255, 255, 255).uv(um, v1).uv2(b1, b2).normal(1, 0, 0).endVertex();
        buffer.vertex(matrix, 0.0f, 0.0f, 0.0f).color(255, 255, 255, 255).uv(um, vm).uv2(b1, b2).normal(1, 0, 0).endVertex();
        matrixStack.popPose();
    }

    public static void renderBillboardQuadBright(PoseStack matrixStack, MultiBufferSource buffer, float scale, ResourceLocation texture) {
        renderBillboardQuadBright(matrixStack, buffer, scale, texture, DEFAULT_SETTINGS);
    }

    public static void renderBillboardQuadBright(PoseStack matrixStack, VertexConsumer builder, float scale, ResourceLocation texture, RenderSettings settings) {
        int b1 = settings.brightness() >> 16 & 65535;
        int b2 = settings.brightness() & 65535;
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
        matrixStack.pushPose();
        matrixStack.translate(0.5, 0.5, 0.5);
        RenderHelper.rotateToPlayer(matrixStack);
        Matrix4f matrix = matrixStack.last().pose();
        builder.vertex(matrix, -scale, -scale, 0.0f).color(settings.r(), settings.g(), settings.b(), settings.a()).uv(sprite.getU0(), sprite.getV0()).uv2(b1, b2).normal(1, 0, 0).endVertex();
        builder.vertex(matrix, -scale, scale, 0.0f).color(settings.r(), settings.g(), settings.b(), settings.a()).uv(sprite.getU0(), sprite.getV1()).uv2(b1, b2).normal(1, 0, 0).endVertex();
        builder.vertex(matrix, scale, scale, 0.0f).color(settings.r(), settings.g(), settings.b(), settings.a()).uv(sprite.getU1(), sprite.getV1()).uv2(b1, b2).normal(1, 0, 0).endVertex();
        builder.vertex(matrix, scale, -scale, 0.0f).color(settings.r(), settings.g(), settings.b(), settings.a()).uv(sprite.getU1(), sprite.getV0()).uv2(b1, b2).normal(1, 0, 0).endVertex();
        matrixStack.popPose();
    }

    public static void renderBillboardQuadBright(PoseStack matrixStack, MultiBufferSource buffer, float scale, ResourceLocation texture, RenderSettings settings) {
        renderBillboardQuadBright(matrixStack, buffer.getBuffer(settings.renderType()), scale, texture, settings);
    }

    public static void rotateToPlayer(PoseStack matrixStack) {
        Quaternion rotation = Minecraft.getInstance().gameRenderer.getMainCamera().rotation();
        matrixStack.mulPose(rotation);
    }

    public static int renderText(PoseStack matrixStack, int x, int y, String txt) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1f);

        matrixStack.pushPose();
        matrixStack.translate(0.0F, 0.0F, 32.0F);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        // @todo 1.17 GlStateManager._enableRescaleNormal();
        // @todo 1.17 GlStateManager._enableLighting();
        // @todo 1.17 com.mojang.blaze3d.platform.Lighting.turnBackOn();

        // @todo 1.17 GlStateManager._disableLighting();
        GlStateManager._disableDepthTest();
        GlStateManager._disableBlend();
        Minecraft mc = Minecraft.getInstance();
        int width = mc.font.width(txt);
        mc.font.drawShadow(matrixStack, txt, x, y, 16777215);
        // @todo 1.17 GlStateManager._enableLighting();
        GlStateManager._enableDepthTest();
        // Fixes opaque cooldown overlay a bit lower
        // TODO: check if enabled blending still screws things up down the line.
        GlStateManager._enableBlend();


        matrixStack.popPose();
        // @todo 1.17 GlStateManager._disableRescaleNormal();
        // @todo 1.17 GlStateManager._disableLighting();

        return width;
    }

    public static int renderText(PoseStack matrixStack, int x, int y, String txt, int color) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0f);

        matrixStack.pushPose();
        matrixStack.translate(0.0F, 0.0F, 32.0F);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        // @todo 1.17 GlStateManager._enableRescaleNormal();
        // @todo 1.17 GlStateManager._enableLighting();
        // @todo 1.17 com.mojang.blaze3d.platform.Lighting.turnBackOn();

        // @todo 1.17 GlStateManager._disableLighting();
        GlStateManager._disableDepthTest();
        GlStateManager._disableBlend();
        Minecraft mc = Minecraft.getInstance();
        int width = mc.font.width(txt);
        mc.font.draw(matrixStack, txt, x, y, color);
        // @todo 1.17 GlStateManager._enableLighting();
        GlStateManager._enableDepthTest();
        // Fixes opaque cooldown overlay a bit lower
        // TODO: check if enabled blending still screws things up down the line.
        GlStateManager._enableBlend();


        matrixStack.popPose();
        // @todo 1.17 GlStateManager._disableRescaleNormal();
        // @todo 1.17 GlStateManager._disableLighting();

        return width;
    }

    /**
     * Draw a beam with some thickness.
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

        builder.vertex(matrix, x1, y2, z).color(r, g, b, a).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, x2, y2, z).color(r, g, b, a).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, x2, y1, z).color(r, g, b, a).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, x1, y1, z).color(r, g, b, a).uv2(packedLightIn).endVertex();
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
        Matrix4f matrix = poseStack.last().pose();
        buffer.vertex(matrix, (float) (p.getX() + rect.v1.x), (float) (p.getY() + rect.v1.y), (float) (p.getZ() + rect.v1.z)).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) (p.getX() + rect.v2.x), (float) (p.getY() + rect.v2.y), (float) (p.getZ() + rect.v2.z)).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) (p.getX() + rect.v2.x), (float) (p.getY() + rect.v2.y), (float) (p.getZ() + rect.v2.z)).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) (p.getX() + rect.v3.x), (float) (p.getY() + rect.v3.y), (float) (p.getZ() + rect.v3.z)).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) (p.getX() + rect.v3.x), (float) (p.getY() + rect.v3.y), (float) (p.getZ() + rect.v3.z)).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) (p.getX() + rect.v4.x), (float) (p.getY() + rect.v4.y), (float) (p.getZ() + rect.v4.z)).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) (p.getX() + rect.v4.x), (float) (p.getY() + rect.v4.y), (float) (p.getZ() + rect.v4.z)).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) (p.getX() + rect.v1.x), (float) (p.getY() + rect.v1.y), (float) (p.getZ() + rect.v1.z)).color(r, g, b, a).endVertex();
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
        builder.vertex(matrix, x1, y1, z2).color(r, g, b, a).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, x2, y1, z2).color(r, g, b, a).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, x2, y2, z2).color(r, g, b, a).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, x1, y2, z2).color(r, g, b, a).uv2(packedLightIn).endVertex();

        // FRONT
        builder.vertex(matrix, x1, y2, z1).color(r, g, b, a).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, x2, y2, z1).color(r, g, b, a).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, x2, y1, z1).color(r, g, b, a).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, x1, y1, z1).color(r, g, b, a).uv2(packedLightIn).endVertex();

        // DOWN
        builder.vertex(matrix, x1, y2, z2).color(r, g, b, a).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, x2, y2, z2).color(r, g, b, a).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, x2, y2, z1).color(r, g, b, a).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, x1, y2, z1).color(r, g, b, a).uv2(packedLightIn).endVertex();

        // UP
        builder.vertex(matrix, x1, y1, z1).color(r, g, b, a).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, x2, y1, z1).color(r, g, b, a).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, x2, y1, z2).color(r, g, b, a).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, x1, y1, z2).color(r, g, b, a).uv2(packedLightIn).endVertex();

        // LEFT
        builder.vertex(matrix, x1, y1, z1).color(r, g, b, a).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, x1, y1, z2).color(r, g, b, a).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, x1, y2, z2).color(r, g, b, a).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, x1, y2, z1).color(r, g, b, a).uv2(packedLightIn).endVertex();

        // RIGHT
        builder.vertex(matrix, x2, y2, z1).color(r, g, b, a).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, x2, y2, z2).color(r, g, b, a).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, x2, y1, z2).color(r, g, b, a).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, x2, y1, z1).color(r, g, b, a).uv2(packedLightIn).endVertex();
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
        renderer.vertex(stack.last().pose(), x, y, z).color(r, g, b, 1f).uv2(packedLight).normal(1.0F, 0.0F, 0.0F).endVertex();
    }

    public static void vt(VertexConsumer renderer, PoseStack stack, float x, float y, float z, float u, float v,
                          int packedLight) {
        renderer.vertex(stack.last().pose(), x, y, z).color(1f, 1f, 1f, 1f).uv(u, v).uv2(packedLight).normal(1.0F, 0.0F, 0.0F).endVertex();
    }

    public static void vt(VertexConsumer renderer, PoseStack matrix, float x, float y, float z, float u, float v, int lu, int lv, int r, int g, int b, int a) {
        renderer
                .vertex(matrix.last().pose(), x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .uv2(lu, lv)
                .normal(1, 0, 0)
                .endVertex();
    }

    private static void vt(VertexConsumer renderer, Matrix4f matrix, float x, float y, float z, float u, float v, int packedLight) {
        renderer.vertex(matrix, x, y, z).color(1f, 1f, 1f, 1f).uv(u, v).uv2(packedLight).normal(1.0F, 0.0F, 0.0F).endVertex();
    }

    private static void vt(VertexConsumer renderer, Matrix4f matrix, float x, float y, float z, float u, float v) {
        renderer
                .vertex(matrix, x, y, z)
                .color(1.0f, 1.0f, 1.0f, 1.0f)
                .uv(u, v)
                .uv2(MAX_BRIGHTNESS)
                .normal(1, 0, 0)
                .endVertex();
    }

    public static void vt(VertexConsumer renderer, Matrix4f matrix, float x, float y, float z, float u, float v, int lu, int lv, int r, int g, int b, int a) {
        renderer
                .vertex(matrix, x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .uv2(lu, lv)
                .normal(1, 0, 0)
                .endVertex();
    }

    public static void putVertex(VertexConsumer builder, Position normal,
                                 double x, double y, double z, float u, float v,
                                 TextureAtlasSprite sprite, float r, float g, float b, float a) {
        float iu = sprite.getU(u);
        float iv = sprite.getV(v);
        builder.vertex(x, y, z)
                .uv(iu, iv)
                .uv2(0, 0)
                .color(r, g, b, a)
                .normal((float) normal.x(), (float) normal.y(), (float) normal.z())
                .endVertex();
    }

    public static void renderHighLightedBlocksOutline(PoseStack poseStack, VertexConsumer buffer, float mx, float my, float mz, float r, float g, float b, float a) {
        Matrix4f matrix = poseStack.last().pose();
        buffer.vertex(matrix, mx, my, mz).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx + 1, my, mz).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx, my, mz).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx, my + 1, mz).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx, my, mz).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx, my, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx + 1, my + 1, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx, my + 1, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx + 1, my + 1, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx + 1, my, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx + 1, my + 1, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx + 1, my + 1, mz).color(r, g, b, a).endVertex();

        buffer.vertex(matrix, mx, my + 1, mz).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx, my + 1, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx, my + 1, mz).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx + 1, my + 1, mz).color(r, g, b, a).endVertex();

        buffer.vertex(matrix, mx + 1, my, mz).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx + 1, my, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx + 1, my, mz).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx + 1, my + 1, mz).color(r, g, b, a).endVertex();

        buffer.vertex(matrix, mx, my, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx + 1, my, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx, my, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx, my + 1, mz + 1).color(r, g, b, a).endVertex();
    }

    public static void fill(PoseStack matrixStack, MultiBufferSource buffer, int x1, int y1, int x2, int y2, int color,
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
        VertexConsumer builder = buffer.getBuffer(CustomRenderTypes.QUADS_NOTEXTURE);
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

    public static void rotateXP(PoseStack stack, float degrees) {
        stack.mulPose(Vector3f.XP.rotationDegrees(degrees));
    }

    public static void rotateYP(PoseStack stack, float degrees) {
        stack.mulPose(Vector3f.YP.rotationDegrees(degrees));
    }

    public static void rotateZP(PoseStack stack, float degrees) {
        stack.mulPose(Vector3f.ZP.rotationDegrees(degrees));
    }

    public record Rect(Vec3 v1, Vec3 v2,
                       Vec3 v3, Vec3 v4) {
    }
}
