package mcjty.lib.gui.widgets;

import mcjty.lib.base.StyleConfig;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.RenderHelper;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ColorChoiceEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

import java.util.*;
import java.util.stream.Collectors;

public class ColorChoiceLabel extends Label<ColorChoiceLabel> {

    public static final String TYPE_COLORCHOICELABEL = "colorchoicelabel";

    private List<Integer> colorList = new ArrayList<>();
    private Map<Integer,List<String>> tooltipMap = new HashMap<>();
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
                fireChoiceEvents(null, currentColor);
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
            drawStyledBoxNormal(window, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1, 0xff000000 | getCurrentColor());
            RenderHelper.drawLeftTriangle(xx + bounds.width - 10, yy + bounds.height / 2, StyleConfig.colorCycleButtonTriangleNormal);
            RenderHelper.drawRightTriangle(xx + bounds.width - 4, yy + bounds.height / 2, StyleConfig.colorCycleButtonTriangleNormal);
        } else {
            drawStyledBoxDisabled(window, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
            RenderHelper.drawLeftTriangle(xx + bounds.width - 10, yy + bounds.height / 2, StyleConfig.colorCycleButtonTriangleDisabled);
            RenderHelper.drawRightTriangle(xx + bounds.width - 4, yy + bounds.height / 2, StyleConfig.colorCycleButtonTriangleDisabled);
        }

        super.drawOffset(window, x, y, 0, 1);
    }

    @Override
    public Widget mouseClick(Window window, int x, int y, int button) {
        if (isEnabledAndVisible()) {
            int index = colorList.indexOf(currentColor);
            if (button == 1 || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                index--;
                if (index < 0) {
                    index = colorList.size()-1;
                }
            } else {
                index++;
                if (index >= colorList.size()) {
                    index = 0;
                }
            }
            currentColor = colorList.get(index);
            fireChoiceEvents(window, currentColor);
        }
        return null;
    }

    public ColorChoiceLabel addChoiceEvent(ColorChoiceEvent event) {
        if (choiceEvents == null) {
            choiceEvents = new ArrayList<>();
        }
        choiceEvents.add(event);
        return this;
    }

    public void removeChoiceEvent(ColorChoiceEvent event) {
        if (choiceEvents != null) {
            choiceEvents.remove(event);
        }
    }

    private void fireChoiceEvents(Window window, Integer color) {
            fireChannelEvents(window, "");
        if (choiceEvents != null) {
            for (ColorChoiceEvent event : choiceEvents) {
                event.choiceChanged(this, color);
            }
        }
    }

    @Override
    public void readFromGuiCommand(GuiParser.GuiCommand command) {
        super.readFromGuiCommand(command);
        command.findCommand("choices").ifPresent(cmd -> {
            cmd.commands().forEach(choiceCmd -> {
                Integer color = choiceCmd.getOptionalPar(0, 0);
                colorList.add(color);
                choiceCmd.findCommand("tooltips")
                        .ifPresent(tooltipsCmd -> tooltipMap.put(color, tooltipsCmd.parameters()
                                .map(Object::toString)
                                .collect(Collectors.toList())));
            });
        });
    }

    @Override
    public void fillGuiCommand(GuiParser.GuiCommand command) {
        super.fillGuiCommand(command);
        command.removeParameter(1); // We don't need the name as set by the label
        GuiParser.GuiCommand choicesCmd = new GuiParser.GuiCommand("choices");
        for (Integer s : colorList) {
            GuiParser.GuiCommand choiceCmd = new GuiParser.GuiCommand("choice").parameter(s);
            choicesCmd.command(choiceCmd);
            List<String> tooltips = tooltipMap.get(s);
            if (tooltips != null && !tooltips.isEmpty()) {
                GuiParser.GuiCommand tooltipsCmd = new GuiParser.GuiCommand("tooltips");
                choiceCmd.command(tooltipsCmd);
                for (String tt : tooltips) {
                    tooltipsCmd.parameter(tt);
                }
            }
        }
        command.command(choicesCmd);
    }

    @Override
    public GuiParser.GuiCommand createGuiCommand() {
        return new GuiParser.GuiCommand(TYPE_COLORCHOICELABEL);
    }
}
