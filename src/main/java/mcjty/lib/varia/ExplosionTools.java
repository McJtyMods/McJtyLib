package mcjty.lib.varia;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

public class ExplosionTools {

    public static Explosion explodeFullDestroy(Level level, BlockPos location, float radius) {
        return new Explosion(level, null, location.getX(), location.getY(), location.getZ(), radius, false, Explosion.BlockInteraction.DESTROY_WITH_DECAY);
    }
}
