package mcjty.lib.keys;

import mcjty.lib.client.ClientManualHelper;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class KeyInputHandler {

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KeyBindings.openManual.isPressed()) {
            ClientManualHelper.openManualFromGui();
        }
    }
}
