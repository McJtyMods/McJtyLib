package mcjty.lib.gui.icons;

import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

public class IconManager {

    private final GuiScreen gui;

    private IIcon draggingIcon;
    private int dx;
    private int dy;
    private int x;
    private int y;

    public IconManager(GuiScreen gui) {
        this.gui = gui;
    }

    public void startDragging(IIcon icon) {
        draggingIcon = icon;
    }

    public void stopDragging() {
        draggingIcon = null;
    }

    private int getRelativeX() {
        return Mouse.getEventX() * gui.width / gui.mc.displayWidth;
    }

    private int getRelativeY() {
        return gui.height - Mouse.getEventY() * gui.height / gui.mc.displayHeight - 1;
    }

}
