package mcjty.lib.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.typed.Type;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.ArrayList;
import java.util.List;

public class EnergyBar extends AbstractWidget<EnergyBar> {

    public static final String TYPE_ENERGYBAR = "energybar";

    public static final boolean DEFAULT_HORIZONTAL = false;
    public static final boolean DEFAULT_SHOWTEXT = true;
    public static final boolean DEFAULT_SHOWRFPERTICK = false;

    private long value;
    private long maxValue;
    private Integer energyOnColor = null;
    private Integer energyOffColor = null;
    private Integer spacerColor = null;
    private Integer textColor = null;
    private boolean horizontal = DEFAULT_HORIZONTAL;
    private IEnergyStorage handler = null;
    private boolean showText = DEFAULT_SHOWTEXT;
    private boolean showRfPerTick = DEFAULT_SHOWRFPERTICK;
    private long rfPerTick = 0;

    public EnergyBar horizontal() {
        horizontal = true;
        return this;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public EnergyBar handler(IEnergyStorage handler) {
        this.handler = handler;
        return this;
    }

    public IEnergyStorage getHandler() {
        return handler;
    }

    public EnergyBar vertical() {
        horizontal = false;
        return this;
    }

    public boolean isVertical() {
        return !horizontal;
    }

    @Override
    public List<String> getTooltips() {
        if (tooltips == null) {
            String s = getValue() + " / " + getMaxValue();
            List<String> tt = new ArrayList<>();
            tt.add(s);
            return tt;
        } else {
            return tooltips;
        }
    }

    public boolean isShowText() {
        return showText;
    }

    public EnergyBar showText(boolean showText) {
        this.showText = showText;
        return this;
    }

    public boolean isShowRfPerTick() {
        return showRfPerTick;
    }

    public EnergyBar showRfPerTick(boolean showRfPerTick) {
        this.showRfPerTick = showRfPerTick;
        return this;
    }

    public long getRfPerTick() {
        return rfPerTick;
    }

    public EnergyBar rfPerTick(long rfPerTick) {
        this.rfPerTick = rfPerTick;
        return this;
    }

    public long getValue() {
        if (handler != null) {
            return handler.getEnergyStored();
        }
        return value;
    }

    public EnergyBar value(long value) {
        this.value = value;
        return this;
    }

    public long getMaxValue() {
       if (handler != null) {
           return handler.getMaxEnergyStored();
       }
       return maxValue;
    }

    public EnergyBar maxValue(long maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    public int getEnergyOnColor() {
        return energyOnColor == null ? StyleConfig.colorEnergyBarHighEnergy : energyOnColor;
    }

    public EnergyBar setEnergyOnColor(int energyOnColor) {
        this.energyOnColor = energyOnColor;
        return this;
    }

    public int getEnergyOffColor() {
        return energyOffColor == null ? StyleConfig.colorEnergyBarLowEnergy : energyOffColor;
    }

    public EnergyBar setEnergyOffColor(int leftColor) {
        this.energyOffColor = leftColor;
        return this;
    }

    public int getSpacerColor() {
        return spacerColor == null ? StyleConfig.colorEnergyBarSpacer : spacerColor;
    }

    public EnergyBar setSpacerColor(int rightColor) {
        this.spacerColor = rightColor;
        return this;
    }

    public int getTextColor() {
        return textColor == null ? StyleConfig.colorEnergyBarText : textColor;
    }

    public EnergyBar setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    @Override
    public void draw(Screen gui, final int x, final int y) {
        if (!visible) {
            return;
        }
        super.draw(gui, x, y);

        int bx = x + bounds.x;
        int by = y + bounds.y;
        RenderHelper.drawThickBeveledBox(bx, by, bx + bounds.width - 1, by + bounds.height - 1, 1, StyleConfig.colorEnergyBarTopLeft, StyleConfig.colorEnergyBarBottomRight, 0xff636363);

        long currentValue = getValue();
        long maximum = getMaxValue();
        if (maximum > 0) {
            int color;
            boolean on = false;
            if (horizontal) {
                int w = (int) ((bounds.width-2) * (double) currentValue / maximum);
                for (int xx = bx+1 ; xx < bx + bounds.width-2 ; xx++) {
                    color = getColor(bx, w, on, xx);
                    RenderHelper.drawVerticalLine(xx, by+1, by + bounds.height - 2, color);
                    on = !on;
                }
            } else {
                int h = (int) ((bounds.height-2) * (double) currentValue / maximum);
                for (int yy = y+1 ; yy < y + bounds.height-2 ; yy++) {
                    color = getColorReversed(y, h, on, yy);
                    RenderHelper.drawHorizontalLine(bx+1, y + by + bounds.height - yy -2, bx + bounds.width - 2, color);
                    on = !on;
                }
            }
        }
        if (showText) {
            String s;
            if (showRfPerTick) {
                s = rfPerTick + "RF/t";
            } else {
                s = currentValue + "/" + maximum;
            }
            mc.fontRenderer.drawString(new MatrixStack(), mc.fontRenderer.func_238412_a_(s, getBounds().width), x+bounds.x + 5, y+bounds.y+(bounds.height-mc.fontRenderer.FONT_HEIGHT)/2, getTextColor());  // @todo 1.16
        }
    }

    private int getColor(int pos, int total, boolean on, int cur) {
        int color;
        if (on) {
            if (cur < pos + total) {
                color = getEnergyOnColor();
            } else {
                color = getEnergyOffColor();
            }
        } else {
            color = getSpacerColor();
        }
        return color;
    }

    private int getColorReversed(int pos, int total, boolean on, int cur) {
        int color;
        if (on) {
            if (cur < pos + total) {
                color = getEnergyOnColor();
            } else {
                color = getEnergyOffColor();
            }
        } else {
            color = getSpacerColor();
        }
        return color;
    }


    @Override
    public void readFromGuiCommand(GuiParser.GuiCommand command) {
        super.readFromGuiCommand(command);
        energyOnColor = GuiParser.get(command, "energyoncolor", null);
        energyOffColor = GuiParser.get(command, "energyoffcolor", null);
        spacerColor = GuiParser.get(command, "spacercolor", null);
        textColor = GuiParser.get(command, "textcolor", null);
        horizontal = GuiParser.get(command, "horizontal", DEFAULT_HORIZONTAL);
        showText = GuiParser.get(command, "showtext", DEFAULT_SHOWTEXT);
        showRfPerTick = GuiParser.get(command, "showrfpertick", DEFAULT_SHOWRFPERTICK);
    }

    @Override
    public void fillGuiCommand(GuiParser.GuiCommand command) {
        super.fillGuiCommand(command);
        GuiParser.put(command, "energyoncolor", energyOnColor, null);
        GuiParser.put(command, "energyoffcolor", energyOffColor, null);
        GuiParser.put(command, "spacercolor", spacerColor, null);
        GuiParser.put(command, "textcolor", textColor, null);
        GuiParser.put(command, "horizontal", horizontal, DEFAULT_HORIZONTAL);
        GuiParser.put(command, "showtext", showText, DEFAULT_SHOWTEXT);
        GuiParser.put(command, "showrfpertick", showRfPerTick, DEFAULT_SHOWRFPERTICK);
    }

    @Override
    public GuiParser.GuiCommand createGuiCommand() {
        return new GuiParser.GuiCommand(TYPE_ENERGYBAR);
    }

    @Override
    public <T> void setGenericValue(T value) {

    }

    @Override
    public Object getGenericValue(Type<?> type) {
        return null;
    }
}

