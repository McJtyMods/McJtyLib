package mcjty.lib;

import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.widgets.Widget;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler {

    public ClientEventHandler(){
    }

    @SubscribeEvent
    public void onKeyboardInput(GuiScreenEvent.KeyboardKeyEvent event) {
        if (event.getGui() instanceof GenericGuiContainer) {
            GenericGuiContainer<?> container = (GenericGuiContainer<?>) event.getGui();
            Widget<?> focus = container.getWindow().getTextFocus();
            if (focus != null) {
                event.setCanceled(true);

                // @todo 1.14
                char c0 = Keyboard.getEventCharacter();

                if (Keyboard.getEventKey() == 0 && c0 >= 32 || Keyboard.getEventKeyState()) {
                    container.keyTypedFromEvent(c0, Keyboard.getEventKey());
                    Minecraft.getInstance().dispatchKeypresses();
                }
            }
        }
    }

}
