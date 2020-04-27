package mcjty.lib.gui;

/**
 * Implement this interface on a Screen (gui) if you want to be certain to receive
 * keyboard events even if another interface (like JEI) takes over. This is
 * already implemented by GenericGuiContainer
 */
public interface IKeyReceiver {
    Window getWindow();

    void keyTypedFromEvent(int keyCode, int scanCode);

    void charTypedFromEvent(char codePoint);
}
