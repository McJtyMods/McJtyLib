package mcjty.lib.network;

import java.util.concurrent.Executor;
import net.minecraft.world.entity.player.Player;

public record PayloadContext(Player player, Executor executor) {

    public void enqueueWork(Runnable task) {
        executor.execute(task);
    }
}
