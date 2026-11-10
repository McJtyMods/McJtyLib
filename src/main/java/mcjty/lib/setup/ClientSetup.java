package mcjty.lib.setup;

import mcjty.lib.ClientEventHandler;
import mcjty.lib.keys.KeyBindings;
import mcjty.lib.keys.KeyInputHandler;
import mcjty.lib.network.ClientNetworking;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.tooltips.ClientTooltipIcon;
import mcjty.lib.tooltips.TooltipBundle;
import mcjty.lib.tooltips.TooltipRender;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;

public final class ClientSetup implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register((tile, level) -> {
            if (tile instanceof GenericTileEntity generic) generic.onLoad();
        });
        ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((tile, level) -> {
            if (tile instanceof GenericTileEntity generic) generic.onChunkUnloaded();
        });

        ClientNetworking.registerReceivers();

        ClientEventHandler.register();
        TooltipRender.register();
        KeyBindings.init();

        ClientTickEvents.START_CLIENT_TICK.register(client -> KeyInputHandler.onKeyInput());

        TooltipComponentCallback.EVENT.register(component -> component instanceof ClientTooltipIcon icon ? icon : component instanceof TooltipBundle bundle ? bundle.client() : null);

    }

}
