package mcjty.lib.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ColorChoiceEvent;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypeConvertors;
import mcjty.lib.typed.TypedMap;
import net.minecraft.client.gui.screens.Screen;

import java.util.*;
import java.util.stream.Collectors;

public class ColorChoiceLabel extends AbstractLabel<ColorChoiceLabel> {

    public static final String TYPE_COLORCHOICELABEL = "colorchoicelabel";
    public static final Key<Integer> PARAM_COLOR = new Key<>("color", Type.INTEGER);

    private List<Integer> colorList = new ArrayList<>();
    private Map<Integer,List<String>> tooltipMap = new HashMap<>();
    private Integer currentColor = null;
    private List<ColorChoiceEvent> choiceEvents = null;

    public ColorChoiceLabel() {
        text("");
    }

    public ColorChoiceLabel colors(Integer ... colors) {
        for (Integer color : colors) {
            colorList.add(color);
            if (currentColor == null) {
                currentColor = color;
                fireChoiceEvents(currentColor);
            }
        }
        return this;
    }

    public ColorChoiceLabel choiceTooltip(Integer color, String... tooltips) {
        tooltipMap.put(color, Arrays.asList(tooltips));
        return this;
    }

    public ColorChoiceLabel currentColor(Integer color) {
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
    public void draw(Screen gui, PoseStack matrixStack, int x, int y) {
        if (!visible) {
            return;
        }
        int xx = x + bounds.x;
        int yy = y + bounds.y;

        if (isEnabled()) {
            drawStyledBoxNormal(window, matrixStack, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1, 0xff000000 | getCurrentColor());
            RenderHelper.drawLeftTriangle(matrixStack, xx + bounds.width - 10, yy + bounds.height / 2, StyleConfig.colorCycleButtonTriangleNormal);
            RenderHelper.drawRightTriangle(matrixStack, xx + bounds.width - 4, yy + bounds.height / 2, StyleConfig.colorCycleButtonTriangleNormal);
        } else {
            drawStyledBoxDisabled(window, matrixStack, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
            RenderHelper.drawLeftTriangle(matrixStack, xx + bounds.width - 10, yy + bounds.height / 2, StyleConfig.colorCycleButtonTriangleDisabled);
            RenderHelper.drawRightTriangle(matrixStack, xx + bounds.width - 4, yy + bounds.height / 2, StyleConfig.colorCycleButtonTriangleDisabled);
        }

        super.drawOffset(gui, matrixStack, x, y, 0, 1);
    }

    @Override
    public Widget<?> mouseClick(double x, double y, int button) {
        if (isEnabledAndVisible()) {
            int index = colorList.indexOf(currentColor);
            if (button == 1) {// @todo 1.14 || McJtyLib.proxy.isSneaking()) {
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
            fireChoiceEvents(currentColor);
        }
        return null;
    }

    public ColorChoiceLabel event(ColorChoiceEvent event) {
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

    private void fireChoiceEvents(Integer color) {
        fireChannelEvents(TypedMap.builder()
                .put(Window.PARAM_ID, "choice")
                .put(PARAM_COLOR, color)
                .build());
        if (choiceEvents != null) {
            for (ColorChoiceEvent event : choiceEvents) {
                event.choiceChanged(color);
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

    @Override
    public <T> void setGenericValue(T value) {
        currentColor(TypeConvertors.toInt(value));
    }

    @Override
    public Object getGenericValue(Type<?> type) {
        return getCurrentColor();
    }
}
