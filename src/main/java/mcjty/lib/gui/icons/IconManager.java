package mcjty.lib.gui.icons;

import mcjty.lib.gui.WindowManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

public class IconManager {

    private final WindowManager windowManager;

    private IIcon draggingIcon;
    private int dx;
    private int dy;
    private int x;
    private int y;

    public IconManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public void startDragging(IIcon icon, int iconX, int iconY) {
        draggingIcon = icon;
        System.out.println("getRelativeX() = " + getRelativeX());
        dx = iconX;
        dy = iconY;
    }

    public void stopDragging() {
        draggingIcon = null;
    }

    public boolean isDragging() {
        return draggingIcon != null;
    }

    public void draw(Minecraft mc, Gui gui) {
        if (draggingIcon != null) {
            draggingIcon.draw(mc, gui, getRelativeX() - dx, getRelativeY() - dy);
        }
    }

    private int getRelativeX() {
        GuiScreen gui = windowManager.getGui();
        return Mouse.getEventX() * gui.width / gui.mc.displayWidth;
    }

    private int getRelativeY() {
        GuiScreen gui = windowManager.getGui();
        return gui.height - Mouse.getEventY() * gui.height / gui.mc.displayHeight - 1;
    }

}
