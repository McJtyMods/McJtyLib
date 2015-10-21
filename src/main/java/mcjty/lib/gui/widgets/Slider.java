package mcjty.lib.gui.widgets;

import mcjty.lib.gui.RenderHelper;
import mcjty.lib.gui.Scrollable;
import mcjty.lib.gui.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class Slider extends AbstractWidget<Slider> {
    private boolean dragging = false;
    private int dx, dy;
    private boolean horizontal = false;
    private int minimumKnobSize = 4;

    private Scrollable scrollable;

    public Slider(Minecraft mc, Gui gui) {
        super(mc, gui);
    }

    public Scrollable getScrollable() {
        return scrollable;
    }

    public Slider setScrollable(Scrollable scrollable) {
        this.scrollable = scrollable;
        return this;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public boolean isVertical() {
        return !horizontal;
    }

    public Slider setMinimumKnobSize(int m) {
        minimumKnobSize = m;
        return this;
    }

    public Slider setHorizontal() {
        this.horizontal = true;
        return this;
    }

    public Slider setVertical() {
        this.horizontal = false;
        return this;
    }

    @Override
    public void draw(Window window, int x, int y) {
        if (!visible) {
            return;
        }
        super.draw(window, x, y);

        int xx = x + bounds.x;
        int yy = y + bounds.y;

        RenderHelper.drawThickBeveledBox(xx, yy, xx + bounds.width - 1, yy + bounds.height - 1, 1, 0xff2b2b2b, 0xffffffff, 0xff636363);

        int divider = scrollable.getMaximum() - scrollable.getCountSelected();

        if (horizontal) {
            int size = calculateKnobSize(divider, bounds.width);
            int first = calculateKnobOffset(divider, size, bounds.width);
            if (dragging) {
                RenderHelper.drawBeveledBox(xx + 1 + first, yy + 2, xx + 1 + first + size - 1, yy + bounds.height - 4, 0xff5c669d, 0xffbcc5ff, 0xff7f89bf);
            } else {
                RenderHelper.drawBeveledBox(xx + 1 + first, yy + 2, xx + 1 + first + size - 1, yy + bounds.height - 4, 0xffeeeeee, 0xff333333, 0xff8b8b8b);
            }
            if (size >= 8) {
                RenderHelper.drawVerticalLine(xx + 1 + first + size / 2 - 1, yy + 3, yy + bounds.height - 6, 0xff4e4e4e);
                if (size >= 10) {
                    RenderHelper.drawVerticalLine(xx + 1 + first + size / 2 - 2 - 1, yy + 3, yy + bounds.height - 6, 0xff4e4e4e);
                    RenderHelper.drawVerticalLine(xx + 1 + first + size / 2 + 2 - 1, yy + 3, yy + bounds.height - 6, 0xff4e4e4e);
                }
            }
        } else {
            int size = calculateKnobSize(divider, bounds.height);
            int first = calculateKnobOffset(divider, size, bounds.height);
            if (dragging) {
                RenderHelper.drawBeveledBox(xx + 1, yy + 1 + first, xx + bounds.width - 2, yy + 1 + first + size - 1, 0xff5c669d, 0xffbcc5ff, 0xff7f89bf);
            } else {
                RenderHelper.drawBeveledBox(xx + 1, yy + 1 + first, xx + bounds.width - 2, yy + 1 + first + size - 1, 0xffeeeeee, 0xff333333, 0xff8b8b8b);
            }
            if (size >= 8) {
                RenderHelper.drawHorizontalLine(xx + 3, yy + 1 + first + size / 2 - 1, xx + bounds.width - 4, 0xff4e4e4e);
                if (size >= 10) {
                    RenderHelper.drawHorizontalLine(xx + 3, yy + 1 + first + size / 2 - 2 - 1, xx + bounds.width - 4, 0xff4e4e4e);
                    RenderHelper.drawHorizontalLine(xx + 3, yy + 1 + first + size / 2 + 2 - 1, xx + bounds.width - 4, 0xff4e4e4e);
                }
            }
        }
    }

    private int calculateKnobOffset(int divider, int size, int boundsSize) {
        int first;
        if (divider <= 0) {
            first = 0;
        } else {
            first = (scrollable.getFirstSelected() * (boundsSize-2-size)) / divider;
        }
        return first;
    }

    private int calculateKnobSize(int divider, int boundsSize) {
        int size;
        if (divider <= 0) {
            size = boundsSize - 2;
        } else {
            size = (scrollable.getCountSelected() * (boundsSize-2)) / scrollable.getMaximum();
        }
        if (size < minimumKnobSize) {
            size = minimumKnobSize;
        }
        return size;
    }

    private void updateScrollable(int x, int y) {
        int first;
        int divider = scrollable.getMaximum() - scrollable.getCountSelected();
        if (divider <= 0) {
            first = 0;
        } else {
            if (horizontal) {
                int size = calculateKnobSize(divider, bounds.width);
                first = ((x-bounds.x-dx) * divider) / (bounds.width-4-size);
            } else {
                int size = calculateKnobSize(divider, bounds.height);
                first = ((y-bounds.y-dy) * divider) / (bounds.height-4-size);
            }
        }
        if (first > divider) {
            first = divider;
        }
        if (first < 0) {
            first = 0;
        }
        scrollable.setFirstSelected(first);
    }

    @Override
    public Widget mouseClick(Window window, int x, int y, int button) {
        super.mouseClick(window, x, y, button);
        dragging = true;

        int divider = scrollable.getMaximum() - scrollable.getCountSelected();

        int first;
        if (horizontal) {
            int size = calculateKnobSize(divider, bounds.width);
            first = calculateKnobOffset(divider, size, bounds.width);
            dx = x-bounds.x-first;
            dy = 0;
        } else {
            int size = calculateKnobSize(divider, bounds.height);
            first = calculateKnobOffset(divider, size, bounds.height);
            dx = 0;
            dy = y-bounds.y-first;
        }

        return this;
    }

    @Override
    public void mouseRelease(int x, int y, int button) {
        super.mouseRelease(x, y, button);
        if (dragging) {
            updateScrollable(x, y);
            dragging = false;
        }
    }

    @Override
    public void mouseMove(int x, int y) {
        super.mouseMove(x, y);
        if (dragging) {
            updateScrollable(x, y);
//            System.out.println("x = " + x + "," + y);
        }
    }
}
