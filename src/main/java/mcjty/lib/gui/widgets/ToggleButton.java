package mcjty.lib.gui.widgets;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.gui.RenderHelper;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ButtonEvent;
import mcjty.lib.varia.JSonTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.ArrayList;
import java.util.List;

import static mcjty.lib.base.StyleConfig.*;

public class ToggleButton extends Label<ToggleButton> {

    public static final String TYPE_TOGGLEBUTTON = "togglebutton";

    public static final boolean DEFAULT_CHECKMARKER = false;

    private List<ButtonEvent> buttonEvents = null;
    private boolean pressed = false;
    private boolean checkMarker = DEFAULT_CHECKMARKER;

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

    @Override
    public int getDesiredWidth() {
        int w = desiredWidth;
        if (isDynamic()) {
            return w;
        }
        if (w == -1) {
            w = mc.fontRenderer.getStringWidth(getText())+6 + (checkMarker ? 10 : 0);
        }
        return w;
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
            } else if (isHovering()) {
                drawStyledBoxHovering(window, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
            } else {
                drawStyledBoxNormal(window, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
            }
            if (checkMarker) {
                RenderHelper.drawBeveledBox(xx + 2, yy + bounds.height / 2 - 4, xx + 10, yy + bounds.height / 2 + 4, colorToggleNormalBorderTopLeft, colorToggleNormalBorderBottomRight, colorToggleNormalFiller);
                if (pressed) {
                    mc.fontRenderer.drawString("v", xx + 3, yy + bounds.height / 2 - 4, StyleConfig.colorToggleTextNormal);
                }
            }
        } else {
            drawStyledBoxDisabled(window, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
            if (checkMarker) {
                RenderHelper.drawBeveledBox(xx + 2, yy + bounds.height / 2 - 4, xx + 10, yy + bounds.height / 2 + 4, colorToggleDisabledBorderTopLeft, colorToggleDisabledBorderBottomRight, colorToggleDisabledFiller);
                if (pressed) {
                    mc.fontRenderer.drawString("v", xx + 3, yy + bounds.height / 2 - 4, StyleConfig.colorToggleTextDisabled);
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
        if (buttonEvents != null) {
            for (ButtonEvent event : buttonEvents) {
                event.buttonClicked(this);
            }
        }
    }


    @Override
    public void readFromJSon(JsonObject object) {
        super.readFromJSon(object);
        checkMarker = JSonTools.get(object, "check", DEFAULT_CHECKMARKER);
    }

    @Override
    public JsonObject writeToJSon() {
        JsonObject object = super.writeToJSon();
        object.add("type", new JsonPrimitive(TYPE_TOGGLEBUTTON));
        JSonTools.put(object, "check", checkMarker, DEFAULT_CHECKMARKER);
        return object;
    }
}
