package mcjty.lib.multipart;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.Vec3d;

public interface IGeometryCollector {
    void addQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, TextureAtlasSprite sprite, float hilight);
    void addCube(Vec3d min, Vec3d max, TextureAtlasSprite sprite, float hilight);
}
