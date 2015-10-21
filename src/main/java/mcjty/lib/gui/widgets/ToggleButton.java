package mcjty.lib.gui.widgets;

import mcjty.lib.gui.RenderHelper;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ButtonEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.ArrayList;
import java.util.List;

import static mcjty.lib.gui.widgets.AbstractWidget.COLOR_AVERAGE_FILLER;
import static mcjty.lib.gui.widgets.AbstractWidget.COLOR_BRIGHT_BORDER;
import static mcjty.lib.gui.widgets.AbstractWidget.COLOR_DARK_BORDER;

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
                drawStyledBoxSelected(window, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
                if (checkMarker) {
                    RenderHelper.drawBeveledBox(xx+2, yy+bounds.height/2-4, xx+10, yy+bounds.height/2+4, COLOR_BRIGHT_BORDER, COLOR_DARK_BORDER, COLOR_AVERAGE_FILLER);
                    mc.fontRenderer.drawString("v", xx+3, yy+bounds.height/2-4, Label.DEFAULT_TEXT_COLOR);
                }
            } else {
                drawStyledBoxNormal(window, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
                if (checkMarker) {
                    RenderHelper.drawBeveledBox(xx+2, yy+bounds.height/2-3, xx+9, yy+bounds.height/2+4, COLOR_BRIGHT_BORDER, COLOR_DARK_BORDER, COLOR_AVERAGE_FILLER);
                }
            }
        } else {
            drawStyledBoxDisabled(window, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
            if (checkMarker) {
                RenderHelper.drawBeveledBox(xx+2, yy+bounds.height/2-4, xx+10, yy+bounds.height/2+4, COLOR_BRIGHT_BORDER, COLOR_DARK_BORDER, COLOR_AVERAGE_FILLER);
                if (pressed) {
                    mc.fontRenderer.drawString("v", xx + 3, yy + bounds.height / 2 - 4, Label.DEFAULT_DISABLED_TEXT_COLOR);
                }
            }
        }

        super.drawOffset(window, x, y, checkMarker ? 6 : 0, 1);
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
