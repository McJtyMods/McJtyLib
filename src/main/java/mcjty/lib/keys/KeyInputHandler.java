package mcjty.lib.keys;

import mcjty.lib.client.ClientManualHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class KeyInputHandler {

    @SubscribeEvent
    public void onKeyInput(ClientTickEvent.Pre event) {
        KeyMapping kb = KeyBindings.openManual;
        boolean doStuff = !kb.isUnbound() && kb.isDown();
        if (doStuff) {
            ClientManualHelper.openManualFromGui();
        }
    }
}
