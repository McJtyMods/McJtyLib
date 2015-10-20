package mcjty.lib.gui.widgets;

import cofh.api.energy.IEnergyHandler;
import mcjty.lib.gui.RenderHelper;
import mcjty.lib.gui.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

public class EnergyBar extends AbstractWidget<EnergyBar> {
    private int value;
    private int maxValue;
    private int color = 0xFF000000;
    private int leftColor = 0xFFFF0000;
    private int rightColor = 0xFF550000;
    private boolean horizontal = false;
    private IEnergyHandler handler = null;
    private boolean showText = true;
    private boolean showRfPerTick = false;
    private int rfPerTick = 0;

    public EnergyBar(Minecraft mc, Gui gui) {
        super(mc, gui);
    }

    public EnergyBar setHorizontal() {
        horizontal = true;
        return this;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public EnergyBar setHandler(IEnergyHandler handler) {
        this.handler = handler;
        return this;
    }

    public IEnergyHandler getHandler() {
        return handler;
    }

    public EnergyBar setVertical() {
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
            List<String> tt = new ArrayList<String>();
            tt.add(s);
            return tt;
        } else {
            return tooltips;
        }
    }

    public boolean isShowText() {
        return showText;
    }

    public EnergyBar setShowText(boolean showText) {
        this.showText = showText;
        return this;
    }

    public boolean isShowRfPerTick() {
        return showRfPerTick;
    }

    public EnergyBar setShowRfPerTick(boolean showRfPerTick) {
        this.showRfPerTick = showRfPerTick;
        return this;
    }

    public int getRfPerTick() {
        return rfPerTick;
    }

    public EnergyBar setRfPerTick(int rfPerTick) {
        this.rfPerTick = rfPerTick;
        return this;
    }

    public int getValue() {
        if (handler != null) {
            return handler.getEnergyStored(ForgeDirection.DOWN);
        }
        return value;
    }

    public EnergyBar setValue(int value) {
        this.value = value;
        return this;
    }

    public int getMaxValue() {
       if (handler != null) {
           return handler.getMaxEnergyStored(ForgeDirection.DOWN);
       }
       return maxValue;
    }

    public EnergyBar setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    public int getColor() {
        return color;
    }

    public EnergyBar setColor(int color) {
        this.color = color;
        return this;
    }

    public int getColor1() {
        return leftColor;
    }

    public EnergyBar setColor1(int leftColor) {
        this.leftColor = leftColor;
        return this;
    }

    public int getColor2() {
        return rightColor;
    }

    public EnergyBar setColor2(int rightColor) {
        this.rightColor = rightColor;
        return this;
    }

    @Override
    public void draw(Window window, final int x, final int y) {
        if (!visible) {
            return;
        }
        super.draw(window, x, y);

        int bx = x + bounds.x;
        int by = y + bounds.y;
        RenderHelper.drawThickBeveledBox(bx, by, bx + bounds.width - 1, by + bounds.height - 1, 1, 0xff2b2b2b, 0xffffffff, 0xff636363);

        int currentValue = getValue();
        int maximum = getMaxValue();
        if (maximum > 0) {
            int color;
            boolean on = false;
            if (horizontal) {
                int w = (int) ((bounds.width-2) * (float) currentValue / maximum);
                for (int xx = bx+1 ; xx < bx + bounds.width-2 ; xx++) {
                    color = getColor(bx, w, on, xx);
                    RenderHelper.drawVerticalLine(xx, by+1, by + bounds.height - 2, color);
                    on = !on;
                }
            } else {
                int h = (int) ((bounds.height-2) * (float) currentValue / maximum);
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
            mc.fontRenderer.drawString(mc.fontRenderer.trimStringToWidth(s, getBounds().width), x+bounds.x, y+bounds.y+(bounds.height-mc.fontRenderer.FONT_HEIGHT)/2, color);
        }
    }

    private int getColor(int pos, int total, boolean on, int cur) {
        int color;
        if (on) {
            if (cur < pos + total) {
                color = 0xffdd0000;
            } else {
                color = 0xff631111;
            }
        } else {
            color = 0xff430000;
        }
        return color;
    }

    private int getColorReversed(int pos, int total, boolean on, int cur) {
        int color;
        if (on) {
            if (cur < pos + total) {
                color = 0xffdd0000;
            } else {
                color = 0xff631111;
            }
        } else {
            color = 0xff430000;
        }
        return color;
    }
}

