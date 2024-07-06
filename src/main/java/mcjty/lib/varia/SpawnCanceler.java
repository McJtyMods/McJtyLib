package mcjty.lib.varia;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;

import java.util.function.Predicate;

public class SpawnCanceler {

    // If this predicate returns true the spawn will be canceled
    public static void registerSpawnCanceler(Predicate<Entity> entityConsumer) {
        NeoForge.EVENT_BUS.addListener((FinalizeSpawnEvent event) -> {
            LevelAccessor world = event.getLevel();
            if (world instanceof Level) {
                Entity entity = event.getEntity();
                if (entityConsumer.test(entity)) {
                    event.setSpawnCancelled(true);
                }
            }
        });
    }
}
