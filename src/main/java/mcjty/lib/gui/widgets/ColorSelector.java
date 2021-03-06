package mcjty.lib.gui.widgets;

import mcjty.lib.McJtyLib;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ColorChoiceEvent;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypeConvertors;
import mcjty.lib.typed.TypedMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ColorSelector extends AbstractLabel<ColorSelector> {

    public static final String TYPE_COLORSELECTOR = "colorselector";
    public static final Key<Integer> PARAM_COLOR = new Key<>("color", Type.INTEGER);

    private Integer currentColor = null;
    private List<ColorChoiceEvent> choiceEvents = null;

    public ColorSelector(Minecraft mc, Gui gui) {
        super(mc, gui);
        setText("");
    }

    public ColorSelector setCurrentColor(Integer color) {
        currentColor = color;
        return this;
    }

    public Integer getCurrentColor() {
        return currentColor;
    }

    @Override
    public void draw(int x, int y) {
        if (!visible) {
            return;
        }
        int xx = x + bounds.x;
        int yy = y + bounds.y;

        if (isEnabled()) {
            Integer color = getCurrentColorSafe();
            drawStyledBoxNormal(window, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1, 0xff000000 | color);
            RenderHelper.drawLeftTriangle(xx + bounds.width - 10, yy + bounds.height / 2, StyleConfig.colorCycleButtonTriangleNormal);
            RenderHelper.drawRightTriangle(xx + bounds.width - 4, yy + bounds.height / 2, StyleConfig.colorCycleButtonTriangleNormal);
        } else {
            drawStyledBoxDisabled(window, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
            RenderHelper.drawLeftTriangle(xx + bounds.width - 10, yy + bounds.height / 2, StyleConfig.colorCycleButtonTriangleDisabled);
            RenderHelper.drawRightTriangle(xx + bounds.width - 4, yy + bounds.height / 2, StyleConfig.colorCycleButtonTriangleDisabled);
        }

        super.drawOffset(x, y, 0, 1);
    }

    private Integer getCurrentColorSafe() {
        Integer color = getCurrentColor();
        if (color == null) {
            color = 0;
        }
        return color;
    }

    @Override
    public Widget<?> mouseClick(int x, int y, int button) {
        if (isEnabledAndVisible()) {
            createColorSelectorWindow(window, x, y);
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
        red.setText(String.valueOf((color >> 16) & 255));
        green.setText(String.valueOf((color >> 8) & 255));
        blue.setText(String.valueOf(color & 255));
        current.setColor(calculateContrastingColor(color));
        current.setFilledBackground(0xff000000 | color);
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
        Minecraft mc = Minecraft.getMinecraft();
        GuiScreen gui = window.getWindowManager().getGui();

        Panel modalDialog = new Panel(mc, gui)
                .setFilledRectThickness(2)
                .setLayout(new PositionalLayout());
        int wx = (int) (window.getToplevel().getBounds().getX() + 20);
        int wy = (int) (window.getToplevel().getBounds().getY() + 20);
        modalDialog.setBounds(new Rectangle(wx, wy, 240, 160));

        int cc = getCurrentColorSafe();

        Button current = new Button(mc, gui).setLayoutHint(new PositionalLayout.PositionalHint(5, 108, 95, 27))
                .setText("Current")
                .setColor(calculateContrastingColor(cc))
                .setFilledBackground(0xff000000 | cc);

        TextField red = new TextField(mc, gui)
                .setLayoutHint(new PositionalLayout.PositionalHint(5, 5, 30, 15));
        TextField green = new TextField(mc, gui)
                .setLayoutHint(new PositionalLayout.PositionalHint(38, 5, 30, 15));
        TextField blue = new TextField(mc, gui)
                .setLayoutHint(new PositionalLayout.PositionalHint(71, 5, 30, 15));
        setSelectedColor(red, green, blue, current, cc);

        red.addTextEnterEvent((parent, newText) -> {
            currentColor = getInputColor(red, green, blue);
            current.setColor(calculateContrastingColor(currentColor));
            current.setFilledBackground(0xff000000 | currentColor);
            fireChoiceEvents(currentColor);
        });
        green.addTextEnterEvent((parent, newText) -> {
            currentColor = getInputColor(red, green, blue);
            current.setColor(calculateContrastingColor(currentColor));
            current.setFilledBackground(0xff000000 | currentColor);
            fireChoiceEvents(currentColor);
        });
        blue.addTextEnterEvent((parent, newText) -> {
            currentColor = getInputColor(red, green, blue);
            current.setColor(calculateContrastingColor(currentColor));
            current.setFilledBackground(0xff000000 | currentColor);
            fireChoiceEvents(currentColor);
        });

        for (EnumDyeColor color : EnumDyeColor.values()) {
            int i = color.ordinal();
            int xx = i % 4;
            int yy = i / 4;
            Button colorLabel = new Button(mc, gui)
                    .setColor(color.getColorValue())
                    .setTooltips(color.getDyeColorName())
                    .setFilledBackground(0xff000000 | color.getColorValue())
                    .addButtonEvent(parent -> {
                        currentColor = color.getColorValue() & 0xffffff;
                        setSelectedColor(red, green, blue, current, currentColor);
                        fireChoiceEvents(currentColor);
                    })
                    .setLayoutHint(new PositionalLayout.PositionalHint(5 + xx * 20, 23 + yy * 20, 18, 18));
            modalDialog.addChild(colorLabel);
        }

        ImageLabel colors = new ImageLabel(mc, gui)
                .setImage(new ResourceLocation(McJtyLib.PROVIDES, "textures/gui/colorpicker.png"), 0, 0)
                .setTextureDimensions(128, 128)
                .setLayoutHint(new PositionalLayout.PositionalHint(105, 5, 128, 128));

        Button close = new Button(mc, gui)
                .setLayoutHint(new PositionalLayout.PositionalHint(90, 140, 60, 15))
                .setText("Close");

        modalDialog.addChildren(close, colors, red, green, blue, current);

        Window modalWindow = window.getWindowManager().createModalWindow(modalDialog);
        colors.addImageEvent((parent, u, v, color) -> {
            currentColor = color & 0xffffff;
            setSelectedColor(red, green, blue, current, currentColor);
            fireChoiceEvents(currentColor);
        });
        close.addButtonEvent(parent -> window.getWindowManager().closeWindow(modalWindow));
    }

    public ColorSelector addChoiceEvent(ColorChoiceEvent event) {
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
                event.choiceChanged(this, color);
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
        setCurrentColor(TypeConvertors.toInt(value));
    }

    @Override
    public Object getGenericValue(Type<?> type) {
        return getCurrentColor();
    }
}
