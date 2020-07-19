package mcjty.lib.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class RenderGlowEffect {

    /**
     * Render a glow effect at the given position. The texture to use
     * for glowing should be bound before calling this.
     */
    // @todo 1.15: needs porting
    public static void renderGlow(MatrixStack matrixStack, IRenderTypeBuffer buffer, double x, double y, double z, ResourceLocation texture) {
        IVertexBuilder builder = buffer.getBuffer(CustomRenderTypes.TRANSLUCENT_ADD_NOLIGHTMAPS);

        matrixStack.push();
        matrixStack.translate(x, y, z);

        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(texture);

        RenderGlowEffect.addSideFullTexture(builder, sprite, Direction.UP.ordinal(), 1.1f, -0.05f);
        RenderGlowEffect.addSideFullTexture(builder, sprite, Direction.DOWN.ordinal(), 1.1f, -0.05f);
        RenderGlowEffect.addSideFullTexture(builder, sprite, Direction.NORTH.ordinal(), 1.1f, -0.05f);
        RenderGlowEffect.addSideFullTexture(builder, sprite, Direction.SOUTH.ordinal(), 1.1f, -0.05f);
        RenderGlowEffect.addSideFullTexture(builder, sprite, Direction.WEST.ordinal(), 1.1f, -0.05f);
        RenderGlowEffect.addSideFullTexture(builder, sprite, Direction.EAST.ordinal(), 1.1f, -0.05f);

        matrixStack.pop();
    }


    private static final Quad[] quads = new Quad[] {
            new Quad(new Vt(0, 0, 0), new Vt(1, 0, 0), new Vt(1, 0, 1), new Vt(0, 0, 1)),       // DOWN
            new Quad(new Vt(0, 1, 1), new Vt(1, 1, 1), new Vt(1, 1, 0), new Vt(0, 1, 0)),       // UP
            new Quad(new Vt(1, 1, 0), new Vt(1, 0, 0), new Vt(0, 0, 0), new Vt(0, 1, 0)),       // NORTH
            new Quad(new Vt(1, 0, 1), new Vt(1, 1, 1), new Vt(0, 1, 1), new Vt(0, 0, 1)),       // SOUTH
            new Quad(new Vt(0, 0, 1), new Vt(0, 1, 1), new Vt(0, 1, 0), new Vt(0, 0, 0)),       // WEST
            new Quad(new Vt(1, 0, 0), new Vt(1, 1, 0), new Vt(1, 1, 1), new Vt(1, 0, 1)),       // EAST
    };

    public static void addSideFullTexture(BufferBuilder buffer, int side, float mult, float offset, Vec3d offs) {
        int brightness = 240;
        int b1 = brightness >> 16 & 65535;
        int b2 = brightness & 65535;
        Quad quad = quads[side];
        buffer.pos(offs.x + quad.v1.x * mult + offset, offs.y + quad.v1.y * mult + offset, offs.z + quad.v1.z * mult + offset).tex(0, 0).lightmap(b1, b2).color(255, 255, 255, 128).endVertex();
        buffer.pos(offs.x + quad.v2.x * mult + offset, offs.y + quad.v2.y * mult + offset, offs.z + quad.v2.z * mult + offset).tex(0, 1).lightmap(b1, b2).color(255, 255, 255, 128).endVertex();
        buffer.pos(offs.x + quad.v3.x * mult + offset, offs.y + quad.v3.y * mult + offset, offs.z + quad.v3.z * mult + offset).tex(1, 1).lightmap(b1, b2).color(255, 255, 255, 128).endVertex();
        buffer.pos(offs.x + quad.v4.x * mult + offset, offs.y + quad.v4.y * mult + offset, offs.z + quad.v4.z * mult + offset).tex(1, 0).lightmap(b1, b2).color(255, 255, 255, 128).endVertex();
    }

    public static void addSideFullTexture(IVertexBuilder buffer, TextureAtlasSprite sprite, int side, float mult, float offset) {
        int brightness = 240;
        int b1 = brightness >> 16 & 65535;
        int b2 = brightness & 65535;
        Quad quad = quads[side];
        buffer.pos(quad.v1.x * mult + offset, quad.v1.y * mult + offset, quad.v1.z * mult + offset).color(255, 255, 255, 128).tex(sprite.getMinU(), sprite.getMinV()).lightmap(b1, b2).normal(1,0,0).endVertex();
        buffer.pos(quad.v2.x * mult + offset, quad.v2.y * mult + offset, quad.v2.z * mult + offset).color(255, 255, 255, 128).tex(sprite.getMinU(), sprite.getMaxV()).lightmap(b1, b2).normal(1,0,0).endVertex();
        buffer.pos(quad.v3.x * mult + offset, quad.v3.y * mult + offset, quad.v3.z * mult + offset).color(255, 255, 255, 128).tex(sprite.getMaxU(), sprite.getMaxV()).lightmap(b1, b2).normal(1,0,0).endVertex();
        buffer.pos(quad.v4.x * mult + offset, quad.v4.y * mult + offset, quad.v4.z * mult + offset).color(255, 255, 255, 128).tex(sprite.getMaxU(), sprite.getMinV()).lightmap(b1, b2).normal(1,0,0).endVertex();
    }

    private static class Vt {
        public final float x;
        public final float y;
        public final float z;

        public Vt(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private static class Quad {
        public final Vt v1;
        public final Vt v2;
        public final Vt v3;
        public final Vt v4;

        public Quad(Vt v1, Vt v2, Vt v3, Vt v4) {
            this.v1 = v1;
            this.v2 = v2;
            this.v3 = v3;
            this.v4 = v4;
        }

        public Quad rotate(Direction direction) {
            switch (direction) {
                case NORTH: return new Quad(v4, v1, v2, v3);
                case EAST: return new Quad(v3, v4, v1, v2);
                case SOUTH: return new Quad(v2, v3, v4, v1);
                case WEST: return this;
                default: return this;
            }
        }
    }
}
