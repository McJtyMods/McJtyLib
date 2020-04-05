package mcjty.lib;

import mcjty.lib.gui.IKeyReceiver;
import mcjty.lib.gui.widgets.Widget;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler {

    public ClientEventHandler(){
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
