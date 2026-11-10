package mcjty.lib;

import mcjty.lib.client.DelayedRenderer;
import mcjty.lib.gui.IKeyReceiver;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;

public final class ClientEventHandler {

    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof IKeyReceiver receiver) {
                ScreenMouseEvents.allowMouseClick(screen).register((s, x, y, button) ->
                        receiver.getWindow() == null || receiver.getWindow().getWindowManager() == null
                                || receiver.getWindow().getWindowManager().getModalWindows().findFirst().isEmpty()
                                || !receiver.mouseClickedFromEvent(x, y, button));
                ScreenMouseEvents.allowMouseRelease(screen).register((s, x, y, button) ->
                        receiver.getWindow() == null || receiver.getWindow().getWindowManager() == null
                                || receiver.getWindow().getWindowManager().getModalWindows().findFirst().isEmpty()
                                || !receiver.mouseReleasedFromEvent(x, y, button));
                ScreenMouseEvents.allowMouseScroll(screen).register((s, x, y, dx, dy) ->
                        receiver.getWindow() == null || receiver.getWindow().getWindowManager() == null
                                || receiver.getWindow().getWindowManager().getModalWindows().findFirst().isEmpty()
                                || !receiver.mouseScrolledFromEvent(x, y, dx, dy));
            }
        });
        WorldRenderEvents.AFTER_ENTITIES.register(context -> DelayedRenderer.render(context.matrixStack()));
    }
}
