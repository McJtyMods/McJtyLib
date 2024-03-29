package mcjty.lib.gui;

/**
 * Implement this interface on a Screen (gui) if you want to be certain to receive
 * keyboard events even if another interface (like JEI) takes over. This is
 * already implemented by GenericGuiContainer
 */
public interface IKeyReceiver {
    Window getWindow();

    void keyTypedFromEvent(int keyCode, int scanCode);

    // Return true if event is handled and should be canceled
    // This is called only if there is a global window open
    boolean mouseClickedFromEvent(double x, double y, int button);
    boolean mouseReleasedFromEvent(double x, double y, int button);
    boolean mouseScrolledFromEvent(double x, double y, double dx, double dy);

    void charTypedFromEvent(char codePoint);
}
