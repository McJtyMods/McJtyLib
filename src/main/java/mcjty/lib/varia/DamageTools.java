package mcjty.lib.varia;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class DamageTools {

    public static DamageSource getGenericDamageSource(Entity entity) {
        return DamageSource.GENERIC;
    }

    public static DamageSource getPlayerAttackDamageSource(Entity entity, Player killer) {
        return DamageSource.playerAttack(killer);
    }
}
