package mcjty.lib.gui;

import mcjty.lib.gui.Window;
import mcjty.lib.gui.WindowManager;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Consumer;

import static mcjty.lib.gui.widgets.Widgets.*;

public class GuiPopupTools {

    public static void askSomething(Minecraft mc, Screen gui, WindowManager windowManager, int x, int y, String title, String initialValue, Consumer<String> callback) {
        Panel ask = vertical()
                .filledBackground(0xff666666, 0xffaaaaaa)
                .filledRectThickness(1);
        ask.bounds(x, y, 100, 60);
        Window askWindow = windowManager.createModalWindow(ask);
        ask.children(label(title));
        TextField input = new TextField().addTextEnterEvent(((newText) -> {
            windowManager.closeWindow(askWindow);
            callback.accept(newText);
        }));
        input.text(initialValue);
        ask.children(input);
        Panel buttons = horizontal().desiredWidth(100).desiredHeight(18);
        buttons.children(button("Ok").event((() -> {
            windowManager.closeWindow(askWindow);
            callback.accept(input.getText());
        })));
        buttons.children(button("Cancel").event((() -> {
            windowManager.closeWindow(askWindow);
        })));
        ask.children(buttons);
    }

    public static void showMessage(Minecraft mc, Screen gui, WindowManager windowManager, int x, int y, String title) {
        Panel ask = vertical()
                .filledBackground(0xff666666, 0xffaaaaaa)
                .filledRectThickness(1);
        ask.bounds(x, y, 200, 40);
        Window askWindow = windowManager.createModalWindow(ask);
        ask.children(label(title).desiredWidth(200));
        Panel buttons = horizontal().desiredWidth(100).desiredHeight(18);
        buttons.children(button("Cancel").event(() -> windowManager.closeWindow(askWindow)));
        ask.children(buttons);
    }

}
