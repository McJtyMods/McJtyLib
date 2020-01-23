package mcjty.lib.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.Set;

public class BlockOutlineRenderer {

    /**
     * This method translates GL state relative to player position
     */
    public static void renderHilightedBlock(BlockPos c, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();

        ClientPlayerEntity p = mc.player;
        double doubleX = p.lastTickPosX + (p.getPosX() - p.lastTickPosX) * partialTicks;
        double doubleY = p.lastTickPosY + (p.getPosY() - p.lastTickPosY) * partialTicks;
        double doubleZ = p.lastTickPosZ + (p.getPosZ() - p.lastTickPosZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.color4f(1.0f, 0, 0, 1.0f);
        GlStateManager.lineWidth(3);
        GlStateManager.translated(-doubleX, -doubleY, -doubleZ);

        GlStateManager.disableDepthTest();
        GlStateManager.disableTexture();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float mx = c.getX();
        float my = c.getY();
        float mz = c.getZ();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        RenderHelper.renderHighLightedBlocksOutline(buffer, mx, my, mz, 1.0f, 0.0f, 0.0f, 1.0f);

        tessellator.draw();

        GlStateManager.enableTexture();
        GlStateManager.popMatrix();
    }

    /**
     * This method translates GL state relative to player position
     */
    public static void renderOutlines(ClientPlayerEntity p, Set<BlockPos> coordinates, int r, int g, int b, float partialTicks) {
        double doubleX = p.lastTickPosX + (p.getPosX() - p.lastTickPosX) * partialTicks;
        double doubleY = p.lastTickPosY + (p.getPosY() - p.lastTickPosY) * partialTicks;
        double doubleZ = p.lastTickPosZ + (p.getPosZ() - p.lastTickPosZ) * partialTicks;

        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        Minecraft.getInstance().gameRenderer.getLightTexture().disableLightmap();
        GlStateManager.disableDepthTest();
        GlStateManager.disableTexture();
        GlStateManager.disableLighting();
        GlStateManager.disableAlphaTest();
        GlStateManager.depthMask(false);

        GlStateManager.pushMatrix();
        GlStateManager.translated(-doubleX, -doubleY, -doubleZ);

        renderOutlines(coordinates, r, g, b, 4);

        GlStateManager.popMatrix();

        Minecraft.getInstance().gameRenderer.getLightTexture().enableLightmap();
        GlStateManager.enableTexture();
    }


    /**
     * This method expects the GL state matrix to be translated to relative player position already
     * (player.lastTickPos + (player.pos - player.lastTickPos)* partialTicks)
     */
    public static void renderOutlines(Set<BlockPos> coordinates, int r, int g, int b, int thickness) {
        Tessellator tessellator = Tessellator.getInstance();

        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

//        GlStateManager.color(r / 255.0f, g / 255.0f, b / 255.0f);
        GlStateManager.lineWidth(thickness);

        for (BlockPos coordinate : coordinates) {
            float x = coordinate.getX();
            float y = coordinate.getY();
            float z = coordinate.getZ();

            renderHighLightedBlocksOutline(buffer, x, y, z, r / 255.0f, g / 255.0f, b / 255.0f, 1.0f); // .02f
        }
        tessellator.draw();
    }

    public static void renderHighLightedBlocksOutline(BufferBuilder buffer, float mx, float my, float mz, float r, float g, float b, float a) {
        buffer.pos(mx, my, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx+1, my, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx, my, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx, my+1, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx, my, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx, my, mz+1).color(r, g, b, a).endVertex();
        buffer.pos(mx+1, my+1, mz+1).color(r, g, b, a).endVertex();
        buffer.pos(mx, my+1, mz+1).color(r, g, b, a).endVertex();
        buffer.pos(mx+1, my+1, mz+1).color(r, g, b, a).endVertex();
        buffer.pos(mx+1, my, mz+1).color(r, g, b, a).endVertex();
        buffer.pos(mx+1, my+1, mz+1).color(r, g, b, a).endVertex();
        buffer.pos(mx+1, my+1, mz).color(r, g, b, a).endVertex();

        buffer.pos(mx, my+1, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx, my+1, mz+1).color(r, g, b, a).endVertex();
        buffer.pos(mx, my+1, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx+1, my+1, mz).color(r, g, b, a).endVertex();

        buffer.pos(mx+1, my, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx+1, my, mz+1).color(r, g, b, a).endVertex();
        buffer.pos(mx+1, my, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx+1, my+1, mz).color(r, g, b, a).endVertex();

        buffer.pos(mx, my, mz+1).color(r, g, b, a).endVertex();
        buffer.pos(mx+1, my, mz+1).color(r, g, b, a).endVertex();
        buffer.pos(mx, my, mz+1).color(r, g, b, a).endVertex();
        buffer.pos(mx, my+1, mz+1).color(r, g, b, a).endVertex();
    }



    /**
     * This method expects the GL state matrix to be translated to relative player position already
     * (player.lastTickPos + (player.pos - player.lastTickPos)* partialTicks)
     */
    public static void renderBoxOutline(BlockPos pos) {
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        Minecraft.getInstance().gameRenderer.getLightTexture().disableLightmap();
        GlStateManager.disableTexture();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableAlphaTest();
        GlStateManager.lineWidth(2);
        GlStateManager.color4f(1, 1, 1, 1);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float mx = pos.getX();
        float my = pos.getY();
        float mz = pos.getZ();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        RenderHelper.renderHighLightedBlocksOutline(buffer, mx, my, mz, .9f, .7f, 0, 1);

        tessellator.draw();

        Minecraft.getInstance().gameRenderer.getLightTexture().enableLightmap();
        GlStateManager.enableTexture();
    }

    /**
     * This method translates GL state relative to player position
     */
    public static void renderHighlightedBlocks(PlayerEntity p, BlockPos base, Set<BlockPos> coordinates, ResourceLocation texture, float partialTicks) {
        double doubleX = p.lastTickPosX + (p.getPosX() - p.lastTickPosX) * partialTicks;
        double doubleY = p.lastTickPosY + (p.getPosY() - p.lastTickPosY) * partialTicks;
        double doubleZ = p.lastTickPosZ + (p.getPosZ() - p.lastTickPosZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.translated(-doubleX, -doubleY, -doubleZ);

        GlStateManager.disableDepthTest();
        GlStateManager.enableTexture();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        Minecraft.getInstance().getTextureManager().bindTexture(texture);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
//        tessellator.setColorRGBA(255, 255, 255, 64);
//        tessellator.setBrightness(240);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        for (BlockPos coordinate : coordinates) {
            float x = base.getX() + coordinate.getX();
            float y = base.getY() + coordinate.getY();
            float z = base.getZ() + coordinate.getZ();
            Vec3d offs = new Vec3d(x, y, z);
            RenderGlowEffect.addSideFullTexture(buffer, Direction.UP.ordinal(), 1.1f, -0.05f, offs);
            RenderGlowEffect.addSideFullTexture(buffer, Direction.DOWN.ordinal(), 1.1f, -0.05f, offs);
            RenderGlowEffect.addSideFullTexture(buffer, Direction.NORTH.ordinal(), 1.1f, -0.05f, offs);
            RenderGlowEffect.addSideFullTexture(buffer, Direction.SOUTH.ordinal(), 1.1f, -0.05f, offs);
            RenderGlowEffect.addSideFullTexture(buffer, Direction.WEST.ordinal(), 1.1f, -0.05f, offs);
            RenderGlowEffect.addSideFullTexture(buffer, Direction.EAST.ordinal(), 1.1f, -0.05f, offs);
        }
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.disableTexture();
        GlStateManager.color4f(.5f, .3f, 0, 1);
        GlStateManager.lineWidth(2);

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        for (BlockPos coordinate : coordinates) {
            RenderHelper.renderHighLightedBlocksOutline(buffer,
                    base.getX() + coordinate.getX(), base.getY() + coordinate.getY(), base.getZ() + coordinate.getZ(),
                    .5f, .3f, 0f, 1.0f);
        }
        tessellator.draw();

        GlStateManager.enableTexture();
        GlStateManager.popMatrix();
    }



}
