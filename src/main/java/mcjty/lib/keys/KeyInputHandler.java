package mcjty.lib.keys;

import mcjty.lib.client.ClientManualHelper;
import net.minecraft.client.KeyMapping;

public class KeyInputHandler {

    public static void onKeyInput() {
        KeyMapping kb = KeyBindings.openManual;
        boolean doStuff = !kb.isUnbound() && kb.isDown();
        if (doStuff) {
            ClientManualHelper.openManualFromGui();
        }
    }
}
