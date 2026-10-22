package mcjty.lib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import static net.minecraft.util.LightCoordsUtil.FULL_BRIGHT;
import static net.minecraft.util.LightCoordsUtil.FULL_SKY;

public class RenderGlowEffect {

    /**
     * Render a glow effect at the given position. The texture to use
     * for glowing should be bound before calling this.
     */
    public static void renderGlow(PoseStack matrixStack, SubmitNodeCollector collector, Identifier texture) {
        TextureAtlasSprite sprite = AbstractDynamicBakedModel.getTexture(texture);
        collector.submitCustomGeometry(matrixStack, CustomRenderTypes.TRANSLUCENT_ADD_NOLIGHTMAPS, (pose, buffer) -> {
            for (Direction side : Direction.values()) addSideFullTexture(pose.pose(), buffer, sprite, side.ordinal(), 1.1f, -0.05f);
        });
    }

    private static final Quad[] QUADS = new Quad[] {
            new Quad(new Vt(0, 0, 0), new Vt(1, 0, 0), new Vt(1, 0, 1), new Vt(0, 0, 1)),       // DOWN
            new Quad(new Vt(0, 1, 1), new Vt(1, 1, 1), new Vt(1, 1, 0), new Vt(0, 1, 0)),       // UP
            new Quad(new Vt(1, 1, 0), new Vt(1, 0, 0), new Vt(0, 0, 0), new Vt(0, 1, 0)),       // NORTH
            new Quad(new Vt(1, 0, 1), new Vt(1, 1, 1), new Vt(0, 1, 1), new Vt(0, 0, 1)),       // SOUTH
            new Quad(new Vt(0, 0, 1), new Vt(0, 1, 1), new Vt(0, 1, 0), new Vt(0, 0, 0)),       // WEST
            new Quad(new Vt(1, 0, 0), new Vt(1, 1, 0), new Vt(1, 1, 1), new Vt(1, 0, 1)),       // EAST
    };

    public static void addSideFullTexture(BufferBuilder buffer, int side, float mult, float offset, Vec3 offs) {
        int b1 = 240;
        int b2 = 240;
        Quad quad = QUADS[side];
        buffer.addVertex((float) (offs.x + quad.v1.x * mult + offset), (float) (offs.y + quad.v1.y * mult + offset), (float) (offs.z + quad.v1.z * mult + offset)).setUv(0, 0).setUv2(b1, b2).setColor(255, 255, 255, 128);
        buffer.addVertex((float) (offs.x + quad.v2.x * mult + offset), (float) (offs.y + quad.v2.y * mult + offset), (float) (offs.z + quad.v2.z * mult + offset)).setUv(0, 1).setUv2(b1, b2).setColor(255, 255, 255, 128);
        buffer.addVertex((float) (offs.x + quad.v3.x * mult + offset), (float) (offs.y + quad.v3.y * mult + offset), (float) (offs.z + quad.v3.z * mult + offset)).setUv(1, 1).setUv2(b1, b2).setColor(255, 255, 255, 128);
        buffer.addVertex((float) (offs.x + quad.v4.x * mult + offset), (float) (offs.y + quad.v4.y * mult + offset), (float) (offs.z + quad.v4.z * mult + offset)).setUv(1, 0).setUv2(b1, b2).setColor(255, 255, 255, 128);
    }

    public static void addSideFullTexture(Matrix4f positionMatrix, VertexConsumer buffer, TextureAtlasSprite sprite, int side, float mult, float offset) {
        int b1 = 240;
        int b2 = 240;
        Quad quad = QUADS[side];
        buffer.addVertex(positionMatrix, quad.v1.x * mult + offset, quad.v1.y * mult + offset, quad.v1.z * mult + offset).setColor(255, 255, 255, 128).setUv(sprite.getU0(), sprite.getV0()).setUv2(b1, b2).setNormal(1,0,0);
        buffer.addVertex(positionMatrix, quad.v2.x * mult + offset, quad.v2.y * mult + offset, quad.v2.z * mult + offset).setColor(255, 255, 255, 128).setUv(sprite.getU0(), sprite.getV1()).setUv2(b1, b2).setNormal(1,0,0);
        buffer.addVertex(positionMatrix, quad.v3.x * mult + offset, quad.v3.y * mult + offset, quad.v3.z * mult + offset).setColor(255, 255, 255, 128).setUv(sprite.getU1(), sprite.getV1()).setUv2(b1, b2).setNormal(1,0,0);
        buffer.addVertex(positionMatrix, quad.v4.x * mult + offset, quad.v4.y * mult + offset, quad.v4.z * mult + offset).setColor(255, 255, 255, 128).setUv(sprite.getU1(), sprite.getV0()).setUv2(b1, b2).setNormal(1,0,0);
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
