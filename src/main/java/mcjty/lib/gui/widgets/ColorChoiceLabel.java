package mcjty.lib.gui.widgets;

import mcjty.lib.gui.RenderHelper;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ColorChoiceEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.*;

public class ColorChoiceLabel extends Label<ColorChoiceLabel> {
    private List<Integer> colorList = new ArrayList<Integer>();
    private Map<Integer,List<String>> tooltipMap = new HashMap<Integer, List<String>>();
    private Integer currentColor = null;
    private List<ColorChoiceEvent> choiceEvents = null;

    public ColorChoiceLabel(Minecraft mc, Gui gui) {
        super(mc, gui);
        setText("");
    }

    public ColorChoiceLabel addColors(Integer ... colors) {
        for (Integer color : colors) {
            colorList.add(color);
            if (currentColor == null) {
                currentColor = color;
                fireChoiceEvents(currentColor);
            }
        }
        return this;
    }

    public ColorChoiceLabel setChoiceTooltip(Integer color, String... tooltips) {
        tooltipMap.put(color, Arrays.asList(tooltips));
        return this;
    }

    public ColorChoiceLabel setCurrentColor(Integer color) {
        currentColor = color;
        return this;
    }

    public Integer getCurrentColor() {
        return currentColor;
    }

    @Override
    public List<String> getTooltips() {
        List<String> tooltips = tooltipMap.get(currentColor);
        if (tooltips == null) {
            return super.getTooltips();
        } else {
            return tooltips;
        }
    }

    @Override
    public void draw(Window window, int x, int y) {
        if (!visible) {
            return;
        }
        int xx = x + bounds.x;
        int yy = y + bounds.y;

        if (isEnabled()) {
            RenderHelper.drawButtonBox(xx, yy, xx + bounds.width - 1, yy + bounds.height - 1, 0xffeeeeee, 0xff000000 | getCurrentColor(), 0xff333333);
            RenderHelper.drawUpTriangle(xx + bounds.width - 7, yy + bounds.height / 2 - 4, 0xff000000);
            RenderHelper.drawDownTriangle(xx + bounds.width - 7, yy + bounds.height / 2 + 2, 0xff000000);
        } else {
            RenderHelper.drawButtonBox(xx, yy, xx + bounds.width - 1, yy + bounds.height - 1, 0xff888888, 0xff666666, 0xff555555);
            RenderHelper.drawUpTriangle(xx + bounds.width - 7, yy + bounds.height / 2 - 4, 0xff888888);
            RenderHelper.drawDownTriangle(xx + bounds.width - 7, yy + bounds.height / 2 + 2, 0xff888888);
        }

        super.drawOffset(window, x, y, 0, 1);
    }

    @Override
    public Widget mouseClick(Window window, int x, int y, int button) {
        if (isEnabledAndVisible()) {
            int index = colorList.indexOf(currentColor);
            index++;
            if (index >= colorList.size()) {
                index = 0;
            }
            currentColor = colorList.get(index);
            fireChoiceEvents(currentColor);
        }
        return null;
    }

    public ColorChoiceLabel addChoiceEvent(ColorChoiceEvent event) {
        if (choiceEvents == null) {
            choiceEvents = new ArrayList<ColorChoiceEvent>();
        }
        choiceEvents.add(event);
        return this;
    }

    public void removeChoiceEvent(ColorChoiceEvent event) {
        if (choiceEvents != null) {
            choiceEvents.remove(event);
        }
    }

    private void fireChoiceEvents(Integer color) {
        if (choiceEvents != null) {
            for (ColorChoiceEvent event : choiceEvents) {
                event.choiceChanged(this, color);
            }
        }
    }
}
