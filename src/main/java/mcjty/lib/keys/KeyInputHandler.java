package mcjty.lib.keys;

import mcjty.lib.client.ClientManualHelper;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.eventbus.api.SubscribeEvent;

public class KeyInputHandler {

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        if (KeyBindings.openManual.consumeClick()) {
            ClientManualHelper.openManualFromGui();
        }
    }
}
