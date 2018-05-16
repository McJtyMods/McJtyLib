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

    private boolean clickHoldToDrag = false;

    public IconManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public void startDragging(IIcon icon, IconHolder origin, int iconX, int iconY) {
        this.draggingIcon = icon;
        this.origin = origin;
        this.dx = iconX - 2;
        this.dy = iconY - 1;
    }

    public boolean isClickHoldToDrag() {
        return clickHoldToDrag;
    }

    public void setClickHoldToDrag(boolean clickHoldToDrag) {
        this.clickHoldToDrag = clickHoldToDrag;
    }

    public void cancelDragging() {
        if (draggingIcon == null) {
            return;
        }

        if (origin != null) {
            // We assume this always works
            origin.setIcon(draggingIcon);
        }
        draggingIcon = null;
        origin = null;
    }

    public void stopDragging(int x, int y) {
        if (draggingIcon == null) {
            return;
        }

        IconHolder iconHolder = findClosestIconHolder(x, y);
        if (iconHolder == null || iconHolder.getIcon() != null) {
            if (origin != null) {
                // We assume this always works
                origin.setIcon(draggingIcon);
            }
        } else {
            if (!iconHolder.setIcon(draggingIcon)) {
                if (origin != null) {
                    // Set it back, it wasn't accepted
                    origin.setIcon(draggingIcon);
                }
            } else {
                if (iconHolder.isSelectable()) {
                    windowManager.setFocus(iconHolder);
                }
            }
        }

        draggingIcon = null;
        origin = null;
    }

    private IconHolder findClosestIconHolder(int x, int y) {
        Optional<Widget<?>> widget = windowManager.findWidgetAtPosition(x, y);
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
