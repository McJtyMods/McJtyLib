package mcjty.lib.network;

import mcjty.lib.varia.SafeClientTools;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

// For compatibility with NeoForge
public class PlayPayloadContext {

    private final Supplier<NetworkEvent.Context> supplier;

    public PlayPayloadContext(Supplier<NetworkEvent.Context> supplier) {
        this.supplier = supplier;
    }

    public WorkHandler workHandler() {
        return new WorkHandler(supplier.get());
    }

    public Optional<Player> player() {
        Player sender = supplier.get().getSender();
        if (sender == null) {
            sender = SafeClientTools.getClientPlayer();
        }
        return Optional.ofNullable(sender);
    }

    public static <T extends CustomPacketPayload> BiConsumer<T, Supplier<NetworkEvent.Context>> wrap(BiConsumer<T, PlayPayloadContext> handler) {
        return (packet, supplier) -> {
            NetworkEvent.Context context = supplier.get();
            handler.accept(packet, new PlayPayloadContext(() -> context));
            context.setPacketHandled(true);
        };
    }

    public static class WorkHandler {
        private final NetworkEvent.Context ctx;

        public WorkHandler(NetworkEvent.Context ctx) {
            this.ctx = ctx;
        }

        public void submitAsync(Runnable runnable) {
            ctx.enqueueWork(runnable);
        }
    }
}
