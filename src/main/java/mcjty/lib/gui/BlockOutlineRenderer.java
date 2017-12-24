package mcjty.lib.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.Set;

public class BlockOutlineRenderer {

    /**
     * This method translates GL state relative to player position
     */
    private static void renderHilightedBlock(BlockPos c, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();

        EntityPlayerSP p = mc.player;
        double doubleX = p.lastTickPosX + (p.posX - p.lastTickPosX) * partialTicks;
        double doubleY = p.lastTickPosY + (p.posY - p.lastTickPosY) * partialTicks;
        double doubleZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f, 0, 0);
        GlStateManager.glLineWidth(3);
        GlStateManager.translate(-doubleX, -doubleY, -doubleZ);

        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float mx = c.getX();
        float my = c.getY();
        float mz = c.getZ();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        mcjty.lib.gui.RenderHelper.renderHighLightedBlocksOutline(buffer, mx, my, mz, 1.0f, 0.0f, 0.0f, 1.0f);

        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }


    /**
     * This method expects the GL state matrix to be translated to relative player position already
     * (player.lastTickPos + (player.pos - player.lastTickPos)* partialTicks)
     * @param pos
     */
    private static void renderBoxOutline(BlockPos pos) {
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        Minecraft.getMinecraft().entityRenderer.disableLightmap();
        GlStateManager.disableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableAlpha();
        GlStateManager.glLineWidth(2);
        GlStateManager.color(1, 1, 1);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float mx = pos.getX();
        float my = pos.getY();
        float mz = pos.getZ();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        mcjty.lib.gui.RenderHelper.renderHighLightedBlocksOutline(buffer, mx, my, mz, .9f, .7f, 0, 1);

        tessellator.draw();

        Minecraft.getMinecraft().entityRenderer.enableLightmap();
        GlStateManager.enableTexture2D();
    }

    /**
     * This method translates GL state relative to player position
     */
    private static void renderHighlightedBlocks(EntityPlayerSP p, BlockPos base, Set<BlockPos> coordinates, ResourceLocation texture, float partialTicks) {
        double doubleX = p.lastTickPosX + (p.posX - p.lastTickPosX) * partialTicks;
        double doubleY = p.lastTickPosY + (p.posY - p.lastTickPosY) * partialTicks;
        double doubleZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.translate(-doubleX, -doubleY, -doubleZ);

        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
//        tessellator.setColorRGBA(255, 255, 255, 64);
//        tessellator.setBrightness(240);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        for (BlockPos coordinate : coordinates) {
            float x = base.getX() + coordinate.getX();
            float y = base.getY() + coordinate.getY();
            float z = base.getZ() + coordinate.getZ();
            Vec3d offs = new Vec3d(x, y, z);
            RenderGlowEffect.addSideFullTexture(buffer, EnumFacing.UP.ordinal(), 1.1f, -0.05f, offs);
            RenderGlowEffect.addSideFullTexture(buffer, EnumFacing.DOWN.ordinal(), 1.1f, -0.05f, offs);
            RenderGlowEffect.addSideFullTexture(buffer, EnumFacing.NORTH.ordinal(), 1.1f, -0.05f, offs);
            RenderGlowEffect.addSideFullTexture(buffer, EnumFacing.SOUTH.ordinal(), 1.1f, -0.05f, offs);
            RenderGlowEffect.addSideFullTexture(buffer, EnumFacing.WEST.ordinal(), 1.1f, -0.05f, offs);
            RenderGlowEffect.addSideFullTexture(buffer, EnumFacing.EAST.ordinal(), 1.1f, -0.05f, offs);
        }
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.color(.5f, .3f, 0);
        GlStateManager.glLineWidth(2);

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        for (BlockPos coordinate : coordinates) {
            mcjty.lib.gui.RenderHelper.renderHighLightedBlocksOutline(buffer,
                    base.getX() + coordinate.getX(), base.getY() + coordinate.getY(), base.getZ() + coordinate.getZ(),
                    .5f, .3f, 0f, 1.0f);
        }
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }



}
