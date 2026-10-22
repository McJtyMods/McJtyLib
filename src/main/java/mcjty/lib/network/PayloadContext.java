package mcjty.lib.network;

import net.minecraft.world.entity.player.Player;
import java.util.concurrent.Executor;

public record PayloadContext(Player player, Executor executor) {

    public void enqueueWork(Runnable task) {
        executor.execute(task);
    }

}
