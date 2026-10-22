package mcjty.lib.varia;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

public class ExplosionTools {

    public static Explosion explodeFullDestroy(Level level, BlockPos location, float radius) {
        return new net.minecraft.world.level.ServerExplosion((net.minecraft.server.level.ServerLevel)level, null, null, null, net.minecraft.world.phys.Vec3.atCenterOf(location), radius, false, Explosion.BlockInteraction.DESTROY_WITH_DECAY);
    }
}
