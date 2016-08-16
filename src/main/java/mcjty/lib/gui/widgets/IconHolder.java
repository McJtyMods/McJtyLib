package mcjty.lib.gui.widgets;

import mcjty.lib.gui.Window;
import mcjty.lib.gui.icons.IIcon;
import mcjty.lib.gui.icons.IconManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class IconHolder extends AbstractWidget<IconHolder> {

    private IIcon icon;
    private int border = 0;

    public IconHolder(Minecraft mc, Gui gui) {
        super(mc, gui);
    }

    public IIcon getIcon() {
        return icon;
    }

    public IconHolder setIcon(IIcon icon) {
        this.icon = icon;
        return this;
    }

    public int getBorder() {
        return border;
    }

    public IconHolder setBorder(int border) {
        this.border = border;
        return this;
    }

    @Override
    public Widget mouseClick(Window window, int x, int y, int button) {
        if (isEnabledAndVisible()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            } else {
                if (icon != null) {
                    IconManager iconManager = window.getWindowManager().getIconManager();
                    Rectangle windowBounds = window.getToplevel().getBounds();
                    System.out.println("x = " + x);
                    System.out.println("windowBounds.x = " + windowBounds.x);
                    System.out.println("bounds.x = " + bounds.x);
                    iconManager.startDragging(icon,
                            x - this.bounds.x,// + windowBounds.x,
                            y - this.bounds.y);// + windowBounds.y);
                    icon = null;
                }
            }
//            fireChoiceEvents(choiceList.get(currentChoice));
        }
        return null;
    }


    @Override
    public void draw(Window window, int x, int y) {
        if (!visible) {
            return;
        }
        super.draw(window, x, y);

        int xx = x + bounds.x;
        int yy = y + bounds.y;

        if (border > 0) {
            drawBox(xx, yy, 0xffffffff);
        }

        if (icon != null) {
            icon.draw(mc, gui, xx+border, yy+border);
        }
    }
}
