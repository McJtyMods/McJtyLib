package mcjty.lib.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.McJtyLib;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ColorChoiceEvent;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypeConvertors;
import mcjty.lib.typed.TypedMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

import static mcjty.lib.gui.widgets.Widgets.*;

public class ColorSelector extends AbstractLabel<ColorSelector> {

    public static final String TYPE_COLORSELECTOR = "colorselector";
    public static final Key<Integer> PARAM_COLOR = new Key<>("color", Type.INTEGER);

    private Integer currentColor = null;
    private List<ColorChoiceEvent> choiceEvents = null;

    public ColorSelector() {
        text("");
    }

    public ColorSelector currentColor(Integer color) {
        currentColor = color;
        return this;
    }

    public Integer getCurrentColor() {
        return currentColor;
    }

    @Override
    public void draw(Screen gui, MatrixStack matrixStack, int x, int y) {
        if (!visible) {
            return;
        }
        int xx = x + bounds.x;
        int yy = y + bounds.y;

        if (isEnabled()) {
            Integer color = getCurrentColorSafe();
            drawStyledBoxNormal(window, matrixStack, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1, 0xff000000 | color);
            RenderHelper.drawLeftTriangle(matrixStack, xx + bounds.width - 10, yy + bounds.height / 2, StyleConfig.colorCycleButtonTriangleNormal);
            RenderHelper.drawRightTriangle(matrixStack, xx + bounds.width - 4, yy + bounds.height / 2, StyleConfig.colorCycleButtonTriangleNormal);
        } else {
            drawStyledBoxDisabled(window, matrixStack, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
            RenderHelper.drawLeftTriangle(matrixStack, xx + bounds.width - 10, yy + bounds.height / 2, StyleConfig.colorCycleButtonTriangleDisabled);
            RenderHelper.drawRightTriangle(matrixStack, xx + bounds.width - 4, yy + bounds.height / 2, StyleConfig.colorCycleButtonTriangleDisabled);
        }

        super.drawOffset(gui, matrixStack, x, y, 0, 1);
    }

    private Integer getCurrentColorSafe() {
        Integer color = getCurrentColor();
        if (color == null) {
            color = 0;
        }
        return color;
    }

    @Override
    public Widget<?> mouseClick(double x, double y, int button) {
        if (isEnabledAndVisible()) {
            createColorSelectorWindow(window, (int)x, (int)y);
        }
        return null;
    }

    private static int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void setSelectedColor(TextField red, TextField green, TextField blue, Button current, int color) {
        red.text(String.valueOf((color >> 16) & 255));
        green.text(String.valueOf((color >> 8) & 255));
        blue.text(String.valueOf(color & 255));
        current.color(calculateContrastingColor(color));
        current.filledBackground(0xff000000 | color);
    }

    private int getInputColor(TextField red, TextField green, TextField blue) {
        int r = parseIntSafe(red.getText());
        int g = parseIntSafe(green.getText());
        int b = parseIntSafe(blue.getText());
        return (r << 16) + (g << 8) + b;
    }

    private int calculateContrastingColor(int color) {
        int red = (color >> 16) & 255;
        int green = (color >> 8) & 255;
        int blue = color & 255;

        red = (red+128) & 255;
        green = (green+128) & 255;
        blue = (blue+128) & 255;

        return (red << 16) + (green << 8) + blue;
    }

    private void createColorSelectorWindow(Window window, int x, int y) {
        Minecraft mc = Minecraft.getInstance();
        Screen gui = window.getWindowManager().getGui();

        Panel modalDialog = positional().filledRectThickness(2);
        int wx = (int) (window.getToplevel().getBounds().getX() + 20);
        int wy = (int) (window.getToplevel().getBounds().getY() + 20);
        modalDialog.bounds(wx, wy, 240, 160);

        int cc = getCurrentColorSafe();

        Button current = button(5, 108, 95, 27, "Current")
                .color(calculateContrastingColor(cc))
                .filledBackground(0xff000000 | cc);

        TextField red = textfield(5, 5, 30, 15);
        TextField green = textfield(38, 5, 30, 15);
        TextField blue = textfield(71, 5, 30, 15);
        setSelectedColor(red, green, blue, current, cc);

        red.addTextEnterEvent((newText) -> {
            currentColor = getInputColor(red, green, blue);
            current.color(calculateContrastingColor(currentColor));
            current.filledBackground(0xff000000 | currentColor);
            fireChoiceEvents(currentColor);
        });
        green.addTextEnterEvent((newText) -> {
            currentColor = getInputColor(red, green, blue);
            current.color(calculateContrastingColor(currentColor));
            current.filledBackground(0xff000000 | currentColor);
            fireChoiceEvents(currentColor);
        });
        blue.addTextEnterEvent((newText) -> {
            currentColor = getInputColor(red, green, blue);
            current.color(calculateContrastingColor(currentColor));
            current.filledBackground(0xff000000 | currentColor);
            fireChoiceEvents(currentColor);
        });

        for (DyeColor color : DyeColor.values()) {
            int i = color.ordinal();
            int xx = i % 4;
            int yy = i / 4;
            Button colorLabel = button(5 + xx * 20, 23 + yy * 20, 18, 18, null)
                    .color(color.getTextColor())
                    .tooltips(color.getString())
                    .filledBackground(0xff000000 | color.getTextColor())
                    .event(() -> {
                        currentColor = color.getTextColor() & 0xffffff;
                        setSelectedColor(red, green, blue, current, currentColor);
                        fireChoiceEvents(currentColor);
                    });
            modalDialog.children(colorLabel);
        }

        ImageLabel colors = new ImageLabel()
                .image(new ResourceLocation(McJtyLib.MODID, "textures/gui/colorpicker.png"), 0, 0)
                .setTextureDimensions(128, 128)
                .hint(105, 5, 128, 128);

        Button close = button(90, 140, 60, 15, "Close");

        modalDialog.children(close, colors, red, green, blue, current);

        Window modalWindow = window.getWindowManager().createModalWindow(modalDialog);
        colors.event((u, v, color) -> {
            currentColor = color & 0xffffff;
            setSelectedColor(red, green, blue, current, currentColor);
            fireChoiceEvents(currentColor);
        });
        close.event(() -> window.getWindowManager().closeWindow(modalWindow));
    }

    public ColorSelector event(ColorChoiceEvent event) {
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
    }

    @Override
    public void fillGuiCommand(GuiParser.GuiCommand command) {
        super.fillGuiCommand(command);
        command.removeParameter(1); // We don't need the name as set by the label
    }

    @Override
    public GuiParser.GuiCommand createGuiCommand() {
        return new GuiParser.GuiCommand(TYPE_COLORSELECTOR);
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
