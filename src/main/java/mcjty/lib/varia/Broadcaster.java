package mcjty.lib.varia;

import com.google.common.collect.Maps;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.Map;

public class Broadcaster {

    private static Map<String,Long> messages = Maps.newHashMap();

    public static void broadcast(World worldObj, int x, int y, int z, String message, float radius) {
        long time = System.currentTimeMillis();
        if (messages.containsKey(message)) {
            long t = messages.get(message);
            if ((time - t) > 2000) {
                messages.remove(message);
            } else {
                return;
            }
        }
        messages.put(message, time);
        for (Object p : worldObj.players()) {
            PlayerEntity player = (PlayerEntity) p;
            double sqdist = player.distanceToSqr(x + .5, y + .5, z + .5);
            if (sqdist < radius) {
                Logging.warn(player, message);
            }
        }
    }
}
