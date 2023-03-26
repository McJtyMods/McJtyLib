package mcjty.lib.varia;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.function.Predicate;

public class SpawnCanceler {

    // If this predicate returns true the spawn will be canceled
    public static void registerSpawnCanceler(Predicate<Entity> entityConsumer) {
        MinecraftForge.EVENT_BUS.addListener((LivingSpawnEvent.CheckSpawn event) -> {
            LevelAccessor world = event.getLevel();
            if (world instanceof Level) {
                Entity entity = event.getEntity();
                if (entityConsumer.test(entity)) {
                    event.setResult(Event.Result.DENY);
                }
            }
        });
    }
}
