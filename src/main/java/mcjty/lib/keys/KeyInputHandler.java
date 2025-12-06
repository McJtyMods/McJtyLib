package mcjty.lib.keys;

import com.mojang.blaze3d.platform.InputConstants;
import mcjty.lib.client.ClientManualHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {

    @SubscribeEvent
    public void onKeyInput(ClientTickEvent.Pre event) {
        KeyMapping kb = KeyBindings.openManual;
        boolean doStuff = switch (kb.getKey().getType()) {
            case KEYSYM -> {
                if (kb.isUnbound()) {
                    yield false;
                }
                yield InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), kb.getKey().getValue());
            }
            case MOUSE -> GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), kb.getKey().getValue()) == GLFW.GLFW_PRESS;
            default -> kb.isDown();
        };
        if (doStuff) {
            ClientManualHelper.openManualFromGui();
        }
    }
}
