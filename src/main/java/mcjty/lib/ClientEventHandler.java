package mcjty.lib;

import mcjty.lib.gui.IKeyReceiver;
import mcjty.lib.gui.WindowManager;
import mcjty.lib.gui.widgets.Widget;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler {

    public ClientEventHandler(){
    }

    @SubscribeEvent
    public void onMouseMoved(GuiScreenEvent.MouseDragEvent event) {
        if (event.getGui() instanceof IKeyReceiver) {
            IKeyReceiver container = (IKeyReceiver) event.getGui();
            WindowManager manager = container.getWindow().getWindowManager();
            if (manager != null) {
                if (manager.getModalWindows().findFirst().isPresent()) {
                    // There is a modal window. Eat this event and send it directly to the window
                    manager.handleMouseInput(event.getMouseButton());
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onMouseScolled(GuiScreenEvent.MouseScrollEvent.Pre event) {
        if (event.getGui() instanceof IKeyReceiver) {
            IKeyReceiver container = (IKeyReceiver) event.getGui();
            WindowManager manager = container.getWindow().getWindowManager();
            if (manager != null) {
                if (manager.getModalWindows().findFirst().isPresent()) {
                    // There is a modal window. Eat this event and send it directly to the window
//                    manager.(event.getMouseButton());
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onMouseClicked(GuiScreenEvent.MouseClickedEvent event) {
        if (event.getGui() instanceof IKeyReceiver) {
            IKeyReceiver container = (IKeyReceiver) event.getGui();
            WindowManager manager = container.getWindow().getWindowManager();
            if (manager != null) {
                if (manager.getModalWindows().findFirst().isPresent()) {
                    // There is a modal window. Eat this event and send it directly to the window
                    manager.mouseClicked((int)event.getMouseX(), (int)event.getMouseY(), event.getButton());
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onMouseReleased(GuiScreenEvent.MouseReleasedEvent event) {
        if (event.getGui() instanceof IKeyReceiver) {
            IKeyReceiver container = (IKeyReceiver) event.getGui();
            WindowManager manager = container.getWindow().getWindowManager();
            if (manager != null) {
                if (manager.getModalWindows().findFirst().isPresent()) {
                    // There is a modal window. Eat this event and send it directly to the window
                    manager.mouseReleased((int)event.getMouseX(), (int)event.getMouseY(), event.getButton());
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onGuiInput(GuiScreenEvent.KeyboardCharTypedEvent event) {
        if (event.getGui() instanceof IKeyReceiver) {
            IKeyReceiver container = (IKeyReceiver) event.getGui();
            Widget<?> focus;
            if (container.getWindow().getWindowManager() == null) {
                focus = container.getWindow().getTextFocus();
            } else {
                focus = container.getWindow().getWindowManager().getTextFocus();
            }
            if (focus != null) {
                event.setCanceled(true);
                container.charTypedFromEvent(event.getCodePoint());
                // @todo 1.14 check
//                int c0 = event.getKeyCode();
//                if (Keyboard.getEventKey() == 0 && c0 >= 32 || Keyboard.getEventKeyState()) {
//                    container.keyTypedFromEvent(c0, Keyboard.getEventKey());
//                    Minecraft.getInstance().dispatchKeypresses();
//                }
            }
        }

    }

    @SubscribeEvent
    public void onKeyboardInput(GuiScreenEvent.KeyboardKeyPressedEvent event) {
        if (event.getGui() instanceof IKeyReceiver) {
            IKeyReceiver container = (IKeyReceiver) event.getGui();
            Widget<?> focus;
            if (container.getWindow().getWindowManager() == null) {
                focus = container.getWindow().getTextFocus();
            } else {
                focus = container.getWindow().getWindowManager().getTextFocus();
            }
            if (focus != null) {
                event.setCanceled(true);
                container.keyTypedFromEvent(event.getKeyCode(), event.getScanCode());
                // @todo 1.14 check
//                int c0 = event.getKeyCode();
//                if (Keyboard.getEventKey() == 0 && c0 >= 32 || Keyboard.getEventKeyState()) {
//                    container.keyTypedFromEvent(c0, Keyboard.getEventKey());
//                    Minecraft.getInstance().dispatchKeypresses();
//                }
            }
        }
    }

}
