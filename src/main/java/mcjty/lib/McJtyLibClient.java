package mcjty.lib;

import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.widgets.Widget;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class McJtyLibClient {

    private static boolean init;

    public static void preInit(FMLPreInitializationEvent event){
        if (init) {
            return;
        }
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        init = true;
    }

    public static class ClientEventHandler {

        private ClientEventHandler(){
        }

        @SubscribeEvent
        public void onKeyboardInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
            if (event.getGui() instanceof GenericGuiContainer) {
                GenericGuiContainer container = (GenericGuiContainer) event.getGui();
                Widget focus = container.getWindow().getTextFocus();
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
}
