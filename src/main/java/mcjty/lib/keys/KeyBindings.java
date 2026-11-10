package mcjty.lib.keys;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

    public static KeyMapping openManual;

    public static void init() {
        openManual = new KeyMapping("key.openManual", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F1, "key.categories.mcjtylib");
        KeyBindingHelper.registerKeyBinding(openManual);
    }
}
