package mcjty.lib;

import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.widgets.Widget;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class ClientEventHandler {

    public ClientEventHandler(){
    }

    @SubscribeEvent
    public void onKeyboardInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (event.getGui() instanceof GenericGuiContainer) {
            GenericGuiContainer<?> container = (GenericGuiContainer<?>) event.getGui();
            Widget<?> focus = container.getWindow().getTextFocus();
            if (focus != null) {
                event.setCanceled(true);

                char c0 = Keyboard.getEventCharacter();

                if (Keyboard.getEventKey() == 0 && c0 >= 32 || Keyboard.getEventKeyState()) {
                    container.keyTypedFromEvent(c0, Keyboard.getEventKey());
                    Minecraft.getMinecraft().dispatchKeypresses();
                }
            }
        }
    }

}
