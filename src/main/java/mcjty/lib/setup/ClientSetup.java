package mcjty.lib.setup;

import mcjty.lib.ClientEventHandler;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.keys.KeyBindings;
import mcjty.lib.keys.KeyInputHandler;
import mcjty.lib.tooltips.ClientTooltipIcon;
import mcjty.lib.tooltips.ClientTooltipLine;
import mcjty.lib.tooltips.TooltipRender;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class ClientSetup implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        mcjty.lib.network.ClientNetworking.registerReceivers();

        ClientEventHandler.register();
        TooltipRender.register();
        KeyBindings.init();

        ClientTickEvents.START_CLIENT_TICK.register(client -> KeyInputHandler.onKeyInput());

        CustomRenderTypes.registerPipelines();

        ClientTooltipComponentCallback.EVENT.register(component -> component instanceof ClientTooltipLine line ? line : component instanceof ClientTooltipIcon icon ? icon : component instanceof mcjty.lib.tooltips.TooltipBundle bundle ? bundle.client() : null);

    }

}
