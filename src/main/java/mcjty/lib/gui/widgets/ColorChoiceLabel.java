package mcjty.lib.gui.widgets;

import mcjty.lib.McJtyLib;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.gui.RenderHelper;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ColorChoiceEvent;
import mcjty.lib.gui.layout.PositionalLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.awt.Rectangle;
import java.util.*;

public class ColorChoiceLabel extends Label<ColorChoiceLabel> {
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
            createColorSelectorWindow(window, x, y);

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
            fireChoiceEvents(currentColor);
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

    private void setSelectedColor(TextField red, TextField green, TextField blue, int color) {
        red.setText(String.valueOf((color >> 16) & 255));
        green.setText(String.valueOf((color >> 8) & 255));
        blue.setText(String.valueOf(color & 255));
    }

    private int getInputColor(TextField red, TextField green, TextField blue) {
        int r = parseIntSafe(red.getText());
        int g = parseIntSafe(green.getText());
        int b = parseIntSafe(blue.getText());
        return (r << 16) + (g << 8) + b;
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

        TextField red = new TextField(mc, gui)
                .setLayoutHint(new PositionalLayout.PositionalHint(5, 5, 25, 15));
        TextField green = new TextField(mc, gui)
                .setLayoutHint(new PositionalLayout.PositionalHint(35, 5, 25, 15));
        TextField blue = new TextField(mc, gui)
                .setLayoutHint(new PositionalLayout.PositionalHint(65, 5, 25, 15));
        setSelectedColor(red, green, blue, currentColor);

        red.addTextEnterEvent((parent, newText) -> {
            currentColor = getInputColor(red, green, blue);
            fireChoiceEvents(currentColor);
        });
        green.addTextEnterEvent((parent, newText) -> {
            currentColor = getInputColor(red, green, blue);
            fireChoiceEvents(currentColor);
        });
        blue.addTextEnterEvent((parent, newText) -> {
            currentColor = getInputColor(red, green, blue);
            fireChoiceEvents(currentColor);
        });

        for (EnumDyeColor color : EnumDyeColor.values()) {
            int i = color.ordinal();
            int xx = i % 4;
            int yy = i / 4;
            Button colorLabel = new Button(mc, gui)
                    .setColor(color.getColorValue())
                    .setFilledBackground(color.getColorValue())
                    .addButtonEvent(parent -> {
                        currentColor = color.getColorValue() & 0xffffff;
                        setSelectedColor(red, green, blue, currentColor);
                        fireChoiceEvents(currentColor);
                    })
                    .setLayoutHint(new PositionalLayout.PositionalHint(5 + xx * 16, 23 + yy * 16, 14, 14));
            modalDialog.addChild(colorLabel);
        }

        ImageLabel colors = new ImageLabel<>(mc, gui)
                .setImage(new ResourceLocation(McJtyLib.PROVIDES, "textures/gui/colorpicker.png"), 0, 0)
                .setTextureDimensions(128, 128)
                .setLayoutHint(new PositionalLayout.PositionalHint(105, 5, 128, 128));

        Button close = new Button(mc, gui)
                .setLayoutHint(new PositionalLayout.PositionalHint(90, 140, 60, 15))
                .setText("Close");

        modalDialog.addChild(close).addChild(colors).addChild(red).addChild(green).addChild(blue);

        Window modalWindow = window.getWindowManager().createModalWindow(modalDialog);
        colors.addImageEvent((parent, u, v, color) -> {
            System.out.println("u,v = " + u + "," + v + " -> " + color + " (" + Integer.toHexString(color) + ")");
            currentColor = color & 0xffffff;
            setSelectedColor(red, green, blue, currentColor);
            fireChoiceEvents(currentColor);
        });
        close.addButtonEvent(parent -> window.getWindowManager().closeWindow(modalWindow));
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

    private void fireChoiceEvents(Integer color) {
        if (choiceEvents != null) {
            for (ColorChoiceEvent event : choiceEvents) {
                event.choiceChanged(this, color);
            }
        }
    }
}
