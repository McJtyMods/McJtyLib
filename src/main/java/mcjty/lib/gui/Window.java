package mcjty.lib.gui;

import mcjty.lib.McJtyLib;
import mcjty.lib.gui.widgets.Widget;
import mcjty.lib.preferences.PreferencesProperties;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;

import java.util.List;

/**
 * This class represents a window. It contains a single Widget which
 * represents the contents of this window. That widget is usually a Panel.
 */
public class Window {

    private final Widget toplevel;
    private final GuiScreen gui;
    private Widget textFocus = null;
    private Widget hover = null;
    private GuiStyle currentStyle;

    public Window(GuiScreen gui, Widget toplevel) {
        this.gui = gui;
        this.toplevel = toplevel;
    }

    public Widget getToplevel() {
        return toplevel;
    }

    public void mouseClicked(int x, int y, int button) {
        textFocus = null;
        if (toplevel.in(x, y) && toplevel.isVisible()) {
            toplevel.mouseClick(this, x, y, button);
        }
    }

    public void handleMouseInput() {
        int k = Mouse.getEventButton();
        if (k == -1) {
            mouseMovedOrUp(getRelativeX(), getRelativeY(), k);
        }
    }

    public void mouseMovedOrUp(int x, int y, int button) {
        // -1 == mouse move
        if (button != -1) {
            toplevel.mouseRelease(x, y, button);
        } else {
            toplevel.mouseMove(x, y);
        }
    }

    public void setTextFocus(Widget focus) {
        textFocus = focus;
    }

    public Widget getTextFocus() {
        return textFocus;
    }

    public boolean keyTyped(char typedChar, int keyCode) {
        if (textFocus != null) {
            return textFocus.keyTyped(this, typedChar, keyCode);
        }
        return false;
    }

    public void draw() {
        int x = getRelativeX();
        int y = getRelativeY();

        if (hover != null) {
            hover.setHovering(false);
        }
        hover = toplevel.getWidgetAtPosition(x, y);
        if (hover != null) {
            hover.setHovering(true);
        }

        int dwheel = Mouse.getDWheel();
        if (dwheel != 0) {
            toplevel.mouseWheel(dwheel, x, y);
        }
        PreferencesProperties properties = McJtyLib.getPreferencesProperties(gui.mc.thePlayer);
        if (properties != null) {
            currentStyle = properties.getStyle();
        } else {
            currentStyle = GuiStyle.STYLE_FLAT_GRADIENT;
        }
        toplevel.draw(this, 0, 0);
    }

    public GuiStyle getCurrentStyle() {
        return currentStyle;
    }

    public List<String> getTooltips() {
        int x = getRelativeX();
        int y = getRelativeY();
        if (toplevel.in(x, y) && toplevel.isVisible()) {
            Widget w = toplevel.getWidgetAtPosition(x, y);
            List<String> tooltips = w.getTooltips();
            if (tooltips != null) {
                return tooltips;
            }
        }
        return null;
    }

    public List<ItemStack> getTooltipItems() {
        int x = getRelativeX();
        int y = getRelativeY();
        if (toplevel.in(x, y) && toplevel.isVisible()) {
            Widget w = toplevel.getWidgetAtPosition(x, y);
            List<ItemStack> tooltips = w.getTooltipItems();
            if (tooltips != null) {
                return tooltips;
            }
        }
        return null;
    }

    private int getRelativeX() {
        return Mouse.getEventX() * gui.width / gui.mc.displayWidth;
    }

    private int getRelativeY() {
        return gui.height - Mouse.getEventY() * gui.height / gui.mc.displayHeight - 1;
    }
}
