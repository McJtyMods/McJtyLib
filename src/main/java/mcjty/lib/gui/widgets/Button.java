package mcjty.lib.gui.widgets;

import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.events.ButtonEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.ArrayList;
import java.util.List;

public class Button extends Label<Button> {

    public static final String TYPE_BUTTON = "button";

    private List<ButtonEvent> buttonEvents = null;
    private boolean pressed = false;

    public Button(Minecraft mc, Gui gui) {
        super(mc, gui);
    }

    @Override
    public void draw(int x, int y) {
        if (!visible) {
            return;
        }
        int xx = x + bounds.x;
        int yy = y + bounds.y;

        if (isEnabledAndVisible()) {
            if (pressed) {
                drawStyledBoxSelected(window, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
            } else if (isHovering()) {
                drawStyledBoxHovering(window, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
            } else {
                drawStyledBoxNormal(window, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
            }
        } else {
            drawStyledBoxDisabled(window, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
        }

        super.drawOffset(x, y, 0, 1);
    }

    @Override
    public Widget mouseClick(int x, int y, int button) {
        if (isEnabledAndVisible()) {
            pressed = true;
            return this;
        }
        return null;
    }

    @Override
    public void mouseRelease(int x, int y, int button) {
        super.mouseRelease(x, y, button);
        if (pressed) {
            pressed = false;
            if (isEnabledAndVisible()) {
                fireButtonEvents();
            }
        }
    }

    public Button addButtonEvent(ButtonEvent event) {
        if (buttonEvents == null) {
            buttonEvents = new ArrayList<>();
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
        fireChannelEvents();
        if (buttonEvents != null) {
            for (ButtonEvent event : buttonEvents) {
                event.buttonClicked(this);
            }
        }
    }

    @Override
    public GuiParser.GuiCommand createGuiCommand() {
        return new GuiParser.GuiCommand(TYPE_BUTTON);
    }
}
