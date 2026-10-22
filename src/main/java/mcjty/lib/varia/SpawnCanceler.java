package mcjty.lib.varia;
import net.minecraft.world.entity.Entity;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
/** Invoked for finalized mob spawns before they are added to the server level. */
public final class SpawnCanceler {
 private static final List<Predicate<Entity>> CANCELERS=new CopyOnWriteArrayList<>();
 public static void registerSpawnCanceler(Predicate<Entity> canceler){CANCELERS.add(canceler);}
 public static boolean shouldCancel(Entity entity){return CANCELERS.stream().anyMatch(predicate->predicate.test(entity));}
 public interface SpawnState { boolean mcjtylib$isSpawnCanceled(); }
}
