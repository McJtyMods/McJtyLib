package mcjty.lib.setup;

import mcjty.lib.McJtyLib;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;

public class ModSetup extends DefaultModSetup {

    public static boolean patchouli;

    @Override
    public void init() {
        super.init();
        ServerTickEvents.START_SERVER_TICK.register(server -> server.getPlayerList().getPlayers().forEach(player -> McJtyLib.getPreferencesProperties(player).tick(player)));
    }

    @Override
    protected void setupModCompat() {
        patchouli=FabricLoader.getInstance().isModLoaded("patchouli");
    }

}
