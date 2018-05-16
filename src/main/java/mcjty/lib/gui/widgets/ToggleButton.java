package mcjty.lib.gui.widgets;

import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ButtonEvent;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypeConvertors;
import mcjty.lib.typed.TypedMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.ArrayList;
import java.util.List;

import static mcjty.lib.base.StyleConfig.*;

public class ToggleButton extends AbstractLabel<ToggleButton> {

    public static final String TYPE_TOGGLEBUTTON = "togglebutton";
    public static final Key<Boolean> PARAM_ON = new Key<>("on", Type.BOOLEAN);

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
    public void draw(int x, int y) {
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

        super.drawOffset(x, y, checkMarker ? 6 : 0, 1);
    }

    @Override
    public Widget mouseClick(int x, int y, int button) {
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
        fireChannelEvents(TypedMap.builder()
                .put(Window.PARAM_ID, "enter")
                .put(PARAM_ON, isPressed())
                .build());
        if (buttonEvents != null) {
            for (ButtonEvent event : buttonEvents) {
                event.buttonClicked(this);
            }
        }
    }

    @Override
    public void readFromGuiCommand(GuiParser.GuiCommand command) {
        super.readFromGuiCommand(command);
        checkMarker = GuiParser.get(command, "check", DEFAULT_CHECKMARKER);
    }

    @Override
    public void fillGuiCommand(GuiParser.GuiCommand command) {
        super.fillGuiCommand(command);
        GuiParser.put(command, "check", checkMarker, DEFAULT_CHECKMARKER);
    }

    @Override
    public GuiParser.GuiCommand createGuiCommand() {
        return new GuiParser.GuiCommand(TYPE_TOGGLEBUTTON);
    }

    @Override
    public <T> void setGenericValue(T value) {
        setPressed(TypeConvertors.toBoolean(value));
    }

    @Override
    public Object getGenericValue(Type type) {
        return isPressed();
    }
}
