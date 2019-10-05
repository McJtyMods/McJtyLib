package mcjty.lib.gui;

public interface IKeyReceiver {
    Window getWindow();

    void keyTypedFromEvent(int keyCode, int scanCode);

    void charTypedFromEvent(char codePoint);
}
