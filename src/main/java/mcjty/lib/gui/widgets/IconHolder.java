package mcjty.lib.gui.widgets;

import mcjty.lib.gui.Window;
import mcjty.lib.gui.icons.IIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

public class IconHolder extends AbstractWidget<IconHolder> {

    private IIcon icon;

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

    @Override
    public Widget mouseClick(Window window, int x, int y, int button) {
        if (isEnabledAndVisible()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            } else {
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

        if (icon != null) {
            int xx = x + bounds.x;
            int yy = y + bounds.y;
            icon.draw(mc, gui, window, xx, yy);
        }
    }
}
