package mcjty.lib.gui.icons;

import mcjty.lib.gui.WindowManager;
import mcjty.lib.gui.widgets.IconHolder;
import mcjty.lib.gui.widgets.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.util.Optional;

public class IconManager {

    private final WindowManager windowManager;

    private IIcon draggingIcon;
    private IconHolder origin;
    private int dx;
    private int dy;
    private int x;
    private int y;

    public IconManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public void startDragging(IIcon icon, IconHolder origin, int iconX, int iconY) {
        this.draggingIcon = icon;
        this.origin = origin;
        this.dx = iconX;
        this.dy = iconY;
    }

    public void stopDragging(int x, int y) {
        if (draggingIcon == null) {
            return;
        }

        IconHolder iconHolder = findClosestIconHolder(x, y);
        if (iconHolder == null) {
            if (origin != null) {
                origin.setIcon(draggingIcon);
            }
        } else {
            iconHolder.setIcon(draggingIcon);
        }

        draggingIcon = null;
        origin = null;
    }

    private IconHolder findClosestIconHolder(int x, int y) {
        Optional<Widget> widget = windowManager.stream()
                .map(w -> w.getWidgetAtPosition(x, y))
                .filter(w -> w != null)
                .findFirst();
        if (widget.isPresent() && widget.get() instanceof IconHolder) {
            return (IconHolder) widget.get();
        }
        return null;
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
