package mcjty.lib.test;

import com.mojang.blaze3d.platform.InputConstants;
import mcjty.lib.gui.IKeyReceiver;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.WindowManager;
import mcjty.lib.gui.widgets.ChoiceLabel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.gui.widgets.Widgets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.AccessibilityOnboardingScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.network.chat.Component;

/** Runs in a separate client process, without opening or changing a world. */
public final class ClientRegression implements ClientModInitializer {
    private int ticks;
    private RegressionScreen screen;
    private boolean finished;

    @Override
    public void onInitializeClient() {
        if (!Boolean.getBoolean("mcjtylib.clientRegression")) return;
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (finished) return;
            try {
                if (++ticks > 1200) throw new AssertionError("Client startup timed out at " + client.gui.screen());
                if (screen == null && (client.gui.screen() instanceof TitleScreen
                        || client.gui.screen() instanceof AccessibilityOnboardingScreen)) {
                    screen = new RegressionScreen();
                    client.gui.setScreen(screen);
                } else if (screen != null && client.gui.screen() == screen) {
                    screen.checkInput();
                    finished = true;
                    System.out.println("McJtyLib Fabric client regression checks passed: pipelines, SDL input, text focus and choice widgets");
                    client.stop();
                }
            } catch (Throwable error) {
                error.printStackTrace();
                System.exit(1);
            }
        });
    }

    private static final class RegressionScreen extends Screen implements IKeyReceiver {
        private Window window;
        private WindowManager manager;
        private TextField text;
        private ChoiceLabel choice;

        private RegressionScreen() { super(Component.literal("McJtyLib regression")); }

        @Override
        protected void init() {
            text = new TextField().hint(10, 10, 120, 16).text("start");
            choice = new ChoiceLabel().hint(10, 35, 120, 16).choices("one", "two", "three").choice("one");
            var panel = Widgets.positional().children(text, choice);
            panel.bounds(0, 0, 160, 80);
            window = new Window(this, panel);
            manager = new WindowManager(this).addWindow(window);
            panel.getChildren().forEach(widget -> widget.setWindow(window));
            window.getWidgetAtPosition(15, 15); // Lay out the actual widgets before sending input.
        }

        private void checkInput() {
            manager.mouseClicked(15, 15, InputConstants.MOUSE_BUTTON_LEFT);
            require(window.getTextFocus() == text, "Left click focuses text instead of clearing it");
            require(text.getText().equals("start"), "Left click preserves text");
            require(!ScreenKeyboardEvents.allowCharType(this).invoker().allowCharType(this, new CharacterEvent('é')),
                    "Fabric routes text input to McJtyLib");
            require(text.getText().equals("starté"), "Unicode text input");
            manager.mouseClicked(15, 40, InputConstants.MOUSE_BUTTON_LEFT);
            require(choice.getCurrentChoice().equals("two"), "SDL left click advances choices");
            manager.mouseClicked(15, 40, InputConstants.MOUSE_BUTTON_RIGHT);
            require(choice.getCurrentChoice().equals("one"), "SDL right click reverses choices");
            manager.mouseClicked(15, 15, InputConstants.MOUSE_BUTTON_RIGHT);
            require(text.getText().isEmpty(), "SDL right click clears text");
            manager.closeWindow(window);
            require(window.getTextFocus() == null, "Closing the window releases text input focus");
        }

        @Override public Window getWindow() { return window; }
        @Override public void charTypedFromEvent(char character) { manager.charTyped(character); }
        @Override public void keyTypedFromEvent(int key, int keycode) { manager.keyTyped(key, keycode); }
        @Override public boolean mouseClickedFromEvent(double x, double y, int button) { manager.mouseClicked(x, y, button); return true; }
        @Override public boolean mouseReleasedFromEvent(double x, double y, int button) { manager.mouseReleased(x, y, button); return true; }
        @Override public boolean mouseScrolledFromEvent(double x, double y, double dx, double dy) { manager.mouseScrolled(x, y, dx, dy); return true; }

        private static void require(boolean value, String description) {
            if (!value) throw new AssertionError(description);
        }
    }
}
