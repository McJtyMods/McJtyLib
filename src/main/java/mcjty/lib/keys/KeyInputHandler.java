package mcjty.lib.keys;

import com.mojang.blaze3d.platform.InputConstants;
import mcjty.lib.client.ClientManualHelper;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {

    public static void onKeyInput() {
        KeyMapping kb = KeyBindings.openManual;
        boolean doStuff = switch (KeyBindingHelper.getBoundKeyOf(kb).getType()) {
            case KEYSYM -> {
                if (kb.isUnbound()) {
                    yield false;
                }
                yield InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), KeyBindingHelper.getBoundKeyOf(kb).getValue());
            }
            case MOUSE -> GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), KeyBindingHelper.getBoundKeyOf(kb).getValue()) == GLFW.GLFW_PRESS;
            default -> kb.isDown();
        };
        if (doStuff) {
            ClientManualHelper.openManualFromGui();
        }
    }
}
