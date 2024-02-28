package mcjty.lib;

import mcjty.lib.client.DelayedRenderer;
import mcjty.lib.gui.IKeyReceiver;
import mcjty.lib.gui.WindowManager;
import mcjty.lib.gui.widgets.Widget;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.eventbus.api.EventPriority;
import net.neoforged.neoforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler {

    public ClientEventHandler(){
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMouseDragged(ScreenEvent.MouseDragged.Pre event) {
        if (event.getScreen() instanceof IKeyReceiver container) {
            if (container.getWindow() != null) {
                WindowManager manager = container.getWindow().getWindowManager();
                if (manager != null) {
                    if (manager.getModalWindows().findFirst().isPresent()) {
                        // There is a modal window. Eat this event and send it directly to the window
                        manager.mouseDragged(event.getMouseX(), event.getMouseY(), event.getMouseButton());
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMouseScolled(ScreenEvent.MouseScrolled.Pre event) {
        if (event.getScreen() instanceof IKeyReceiver container) {
            if (container.getWindow() != null) {
                WindowManager manager = container.getWindow().getWindowManager();
                if (manager != null) {
                    if (manager.getModalWindows().findFirst().isPresent()) {
                        // There is a modal window. Eat this event and send it directly to the window
                        if (container.mouseScrolledFromEvent(event.getMouseX(), event.getMouseY(), event.getScrollDelta())) {
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMouseClicked(ScreenEvent.MouseButtonPressed.Pre event) {
        if (event.getScreen() instanceof IKeyReceiver container) {
            if (container.getWindow() != null) {
                WindowManager manager = container.getWindow().getWindowManager();
                if (manager != null) {
                    if (manager.getModalWindows().findFirst().isPresent()) {
                        // There is a modal window. Eat this event and send it directly to the window
                        if (container.mouseClickedFromEvent(event.getMouseX(), event.getMouseY(), event.getButton())) {
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMouseReleased(ScreenEvent.MouseButtonReleased.Pre event) {
        if (event.getScreen() instanceof IKeyReceiver container) {
            if (container.getWindow() != null) {
                WindowManager manager = container.getWindow().getWindowManager();
                if (manager != null) {
                    if (manager.getModalWindows().findFirst().isPresent()) {
                        // There is a modal window. Eat this event and send it directly to the window
                        if (container.mouseReleasedFromEvent(event.getMouseX(), event.getMouseY(), event.getButton())) {
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onGuiInput(ScreenEvent.CharacterTyped event) {
        if (event.getScreen() instanceof IKeyReceiver container) {
            if (container.getWindow() != null) {
                Widget<?> focus;
                if (container.getWindow().getWindowManager() == null) {
                    focus = container.getWindow().getTextFocus();
                } else {
                    focus = container.getWindow().getWindowManager().getTextFocus();
                }
                if (focus != null) {
                    event.setCanceled(true);
                    container.charTypedFromEvent(event.getCodePoint());
                }
            }
        }

    }

    @SubscribeEvent
    public void onKeyboardInput(ScreenEvent.KeyPressed event) {
        if (event.getScreen() instanceof IKeyReceiver container) {
            if (container.getWindow() != null) {
                Widget<?> focus;
                if (container.getWindow().getWindowManager() == null) {
                    focus = container.getWindow().getTextFocus();
                } else {
                    focus = container.getWindow().getWindowManager().getTextFocus();
                }
                if (focus != null) {
                    event.setCanceled(true);
                    container.keyTypedFromEvent(event.getKeyCode(), event.getScanCode());
                }
            }
        }
    }

    // @todo 1.20 correct event?
    @SubscribeEvent
    public void onGameRenderOverlay(RenderLevelStageEvent e) {
        if (e.getStage() == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
            DelayedRenderer.render(e.getPoseStack());
        }
    }
}
