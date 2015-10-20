package mcjty.lib.gui.widgets;

import mcjty.lib.gui.RenderHelper;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ButtonEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.ArrayList;
import java.util.List;

public class ToggleButton extends Label<ToggleButton> {
    private List<ButtonEvent> buttonEvents = null;
    private boolean pressed = false;
    private boolean checkMarker = false;

    public ToggleButton(Minecraft mc, Gui gui) {
        super(mc, gui);
    }

    public boolean isPressed() {
        return pressed;
    }

    public ToggleButton setPressed(boolean pressed) {
        this.pressed = pressed;
        return this;
    }

    public boolean isCheckMarker() {
        return checkMarker;
    }

    public ToggleButton setCheckMarker(boolean checkMarker) {
        this.checkMarker = checkMarker;
        return this;
    }

    @Override
    public void draw(Window window, int x, int y) {
        if (!visible) {
            return;
        }
        int xx = x + bounds.x;
        int yy = y + bounds.y;

        if (isEnabled()) {
            if (pressed) {
                RenderHelper.drawButtonBox(xx, yy, xx + bounds.width - 1, yy + bounds.height - 1, 0xff666666, 0xffeeeeee, 0xff333333);
                if (checkMarker) {
                    RenderHelper.drawHorizontalGradientRect(xx - 5, yy + 2, xx - 1, yy + bounds.height - 3, 0xffff0000, 0xffff0000);
                }
            } else {
                RenderHelper.drawButtonBox(xx, yy, xx + bounds.width - 1, yy + bounds.height - 1, 0xffeeeeee, 0xffc6c6c6, 0xff333333);
            }
        } else {
            RenderHelper.drawButtonBox(xx, yy, xx + bounds.width - 1, yy + bounds.height - 1, 0xff888888, 0xffc6c6c6, 0xff555555);
        }

        super.draw(window, x, y);
    }

    @Override
    public Widget mouseClick(Window window, int x, int y, int button) {
        if (isEnabledAndVisible()) {
            pressed = !pressed;
            fireButtonEvents();
            return this;
        }
        return null;
    }

    public ToggleButton addButtonEvent(ButtonEvent event) {
        if (buttonEvents == null) {
            buttonEvents = new ArrayList<ButtonEvent>();
        }
        buttonEvents.add(event);
        return this;
    }

    public void removeButtonEvent(ButtonEvent event) {
        if (buttonEvents != null) {
            buttonEvents.remove(event);
        }
    }

    private void fireButtonEvents() {
        if (buttonEvents != null) {
            for (ButtonEvent event : buttonEvents) {
                event.buttonClicked(this);
            }
        }
    }
}
