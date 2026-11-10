package mcjty.lib.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;

/** Creates GL-backed resources after Minecraft has a window and texture manager. */
public final class ClientStartup {

    private static final List<Consumer<Minecraft>> LISTENERS = new ArrayList<>();

    public static void register(Consumer<Minecraft> listener) {
        LISTENERS.add(listener);
    }

    public static void initialize(Minecraft client) {
        List.copyOf(LISTENERS).forEach(listener -> listener.accept(client));
        LISTENERS.clear();
    }
}
