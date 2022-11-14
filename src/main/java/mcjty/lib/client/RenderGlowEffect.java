package mcjty.lib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Matrix4f;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.client.renderer.LightTexture.FULL_BLOCK;
import static net.minecraft.client.renderer.LightTexture.FULL_SKY;

public class RenderGlowEffect {

    /**
     * Render a glow effect at the given position. The texture to use
     * for glowing should be bound before calling this.
     */
    public static void renderGlow(PoseStack matrixStack, MultiBufferSource buffer, ResourceLocation texture) {
//        VertexConsumer builder = buffer.getBuffer(CustomRenderTypes.TRANSLUCENT_ADD_TEST);
//        VertexConsumer builder = buffer.getBuffer(CustomRenderTypes.TRANSLUCENT_ADD_NOLIGHTMAPS);
        VertexConsumer builder = buffer.getBuffer(RenderType.translucent());

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);

        Matrix4f matrix = matrixStack.last().pose();
        RenderGlowEffect.addSideFullTexture(matrix, builder, sprite, Direction.UP.ordinal(), 1.1f, -0.05f);
        RenderGlowEffect.addSideFullTexture(matrix, builder, sprite, Direction.DOWN.ordinal(), 1.1f, -0.05f);
        RenderGlowEffect.addSideFullTexture(matrix, builder, sprite, Direction.NORTH.ordinal(), 1.1f, -0.05f);
        RenderGlowEffect.addSideFullTexture(matrix, builder, sprite, Direction.SOUTH.ordinal(), 1.1f, -0.05f);
        RenderGlowEffect.addSideFullTexture(matrix, builder, sprite, Direction.WEST.ordinal(), 1.1f, -0.05f);
        RenderGlowEffect.addSideFullTexture(matrix, builder, sprite, Direction.EAST.ordinal(), 1.1f, -0.05f);
    }


    private static final Quad[] quads = new Quad[] {
            new Quad(new Vt(0, 0, 0), new Vt(1, 0, 0), new Vt(1, 0, 1), new Vt(0, 0, 1)),       // DOWN
            new Quad(new Vt(0, 1, 1), new Vt(1, 1, 1), new Vt(1, 1, 0), new Vt(0, 1, 0)),       // UP
            new Quad(new Vt(1, 1, 0), new Vt(1, 0, 0), new Vt(0, 0, 0), new Vt(0, 1, 0)),       // NORTH
            new Quad(new Vt(1, 0, 1), new Vt(1, 1, 1), new Vt(0, 1, 1), new Vt(0, 0, 1)),       // SOUTH
            new Quad(new Vt(0, 0, 1), new Vt(0, 1, 1), new Vt(0, 1, 0), new Vt(0, 0, 0)),       // WEST
            new Quad(new Vt(1, 0, 0), new Vt(1, 1, 0), new Vt(1, 1, 1), new Vt(1, 0, 1)),       // EAST
    };

    public static void addSideFullTexture(BufferBuilder buffer, int side, float mult, float offset, Vec3 offs) {
        int b1 = FULL_SKY;
        int b2 = FULL_BLOCK;
        Quad quad = quads[side];
        buffer.vertex(offs.x + quad.v1.x * mult + offset, offs.y + quad.v1.y * mult + offset, offs.z + quad.v1.z * mult + offset).uv(0, 0).uv2(b1, b2).color(255, 255, 255, 128).endVertex();
        buffer.vertex(offs.x + quad.v2.x * mult + offset, offs.y + quad.v2.y * mult + offset, offs.z + quad.v2.z * mult + offset).uv(0, 1).uv2(b1, b2).color(255, 255, 255, 128).endVertex();
        buffer.vertex(offs.x + quad.v3.x * mult + offset, offs.y + quad.v3.y * mult + offset, offs.z + quad.v3.z * mult + offset).uv(1, 1).uv2(b1, b2).color(255, 255, 255, 128).endVertex();
        buffer.vertex(offs.x + quad.v4.x * mult + offset, offs.y + quad.v4.y * mult + offset, offs.z + quad.v4.z * mult + offset).uv(1, 0).uv2(b1, b2).color(255, 255, 255, 128).endVertex();
    }

    public static void addSideFullTexture(Matrix4f positionMatrix, VertexConsumer buffer, TextureAtlasSprite sprite, int side, float mult, float offset) {
        int b1 = FULL_SKY;
        int b2 = FULL_BLOCK;
        Quad quad = quads[side];
        buffer.vertex(positionMatrix, quad.v1.x * mult + offset, quad.v1.y * mult + offset, quad.v1.z * mult + offset).color(255, 255, 255, 128).uv(sprite.getU0(), sprite.getV0()).uv2(b1, b2).normal(1,0,0).endVertex();
        buffer.vertex(positionMatrix, quad.v2.x * mult + offset, quad.v2.y * mult + offset, quad.v2.z * mult + offset).color(255, 255, 255, 128).uv(sprite.getU0(), sprite.getV1()).uv2(b1, b2).normal(1,0,0).endVertex();
        buffer.vertex(positionMatrix, quad.v3.x * mult + offset, quad.v3.y * mult + offset, quad.v3.z * mult + offset).color(255, 255, 255, 128).uv(sprite.getU1(), sprite.getV1()).uv2(b1, b2).normal(1,0,0).endVertex();
        buffer.vertex(positionMatrix, quad.v4.x * mult + offset, quad.v4.y * mult + offset, quad.v4.z * mult + offset).color(255, 255, 255, 128).uv(sprite.getU1(), sprite.getV0()).uv2(b1, b2).normal(1,0,0).endVertex();
    }

    private record Vt(float x, float y, float z) {
    }

    private record Quad(Vt v1, Vt v2,
                        Vt v3, Vt v4) {

        public Quad rotate(Direction direction) {
            return switch (direction) {
                case NORTH -> new Quad(v4, v1, v2, v3);
                case EAST -> new Quad(v3, v4, v1, v2);
                case SOUTH -> new Quad(v2, v3, v4, v1);
                case WEST -> this;
                default -> this;
            };
        }
    }
}
