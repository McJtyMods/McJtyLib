package mcjty.lib.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Scrollable;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.SliderEvent;
import mcjty.lib.typed.Type;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.List;

public class Slider extends AbstractWidget<Slider> {

    public static final String TYPE_SLIDER = "slider";

    public static final boolean DEFAULT_HORIZONTAL = false;
    public static final int DEFAULT_MINIMUM_KNOBSIZE = 4;

    private boolean dragging = false;
    private int dx;
    private int dy;
    private boolean horizontal = DEFAULT_HORIZONTAL;
    private int minimumKnobSize = DEFAULT_MINIMUM_KNOBSIZE;

    private List<SliderEvent> sliderEvents = null;

    private Scrollable scrollable;      // Old (used as cache in case scrollableName is used)
    private String scrollableName;      // New

    public Scrollable getScrollable() {
        return scrollable;
    }

    public String getScrollableName() {
        return scrollableName;
    }

    public Slider scrollableName(String scrollableName) {
        this.scrollableName = scrollableName;
        return this;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public boolean isVertical() {
        return !horizontal;
    }

    public Slider minimumKnobSize(int m) {
        minimumKnobSize = m;
        return this;
    }

    public Slider horizontal() {
        this.horizontal = true;
        return this;
    }

    public Slider vertical() {
        this.horizontal = false;
        return this;
    }

    private void findScrollable(Window window) {
        if (scrollable == null) {
            Widget<?> child = window.findChild(scrollableName);
            if (child instanceof Scrollable) {
                scrollable = (Scrollable) child;
            }
        }
    }

    @Override
    public void draw(Screen gui, PoseStack matrixStack, int x, int y) {
        if (!visible) {
            return;
        }
        super.draw(gui, matrixStack, x, y);

        findScrollable(window);

        int xx = x + bounds.x;
        int yy = y + bounds.y;

        RenderHelper.drawThickBeveledBox(matrixStack, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1, 1, StyleConfig.colorSliderTopLeft, StyleConfig.colorSliderBottomRight, StyleConfig.colorSliderFiller);

        int divider = scrollable.getMaximum() - scrollable.getCountSelected();

        if (horizontal) {
            int size = calculateKnobSize(divider, bounds.width);
            int first = calculateKnobOffset(divider, size, bounds.width);
            if (dragging) {
                RenderHelper.drawBeveledBox(matrixStack, xx + 1 + first, yy + 2, xx + 1 + first + size - 1, yy + bounds.height - 4, StyleConfig.colorSliderKnobDraggingTopLeft, StyleConfig.colorSliderKnobDraggingBottomRight, StyleConfig.colorSliderKnobDraggingFiller);
            } else if (isHovering()) {
                RenderHelper.drawBeveledBox(matrixStack, xx + 1 + first, yy + 2, xx + 1 + first + size - 1, yy + bounds.height - 4, StyleConfig.colorSliderKnobHoveringTopLeft, StyleConfig.colorSliderKnobHoveringBottomRight, StyleConfig.colorSliderKnobHoveringFiller);
            } else {
                RenderHelper.drawBeveledBox(matrixStack, xx + 1 + first, yy + 2, xx + 1 + first + size - 1, yy + bounds.height - 4, StyleConfig.colorSliderKnobTopLeft, StyleConfig.colorSliderKnobBottomRight, StyleConfig.colorSliderKnobFiller);
            }
            if (size >= 8) {
                RenderHelper.drawVerticalLine(matrixStack, xx + 1 + first + size / 2 - 1, yy + 3, yy + bounds.height - 6, StyleConfig.colorSliderKnobMarkerLine);
                if (size >= 10) {
                    RenderHelper.drawVerticalLine(matrixStack, xx + 1 + first + size / 2 - 2 - 1, yy + 3, yy + bounds.height - 6, StyleConfig.colorSliderKnobMarkerLine);
                    RenderHelper.drawVerticalLine(matrixStack, xx + 1 + first + size / 2 + 2 - 1, yy + 3, yy + bounds.height - 6, StyleConfig.colorSliderKnobMarkerLine);
                }
            }
        } else {
            int size = calculateKnobSize(divider, bounds.height);
            int first = calculateKnobOffset(divider, size, bounds.height);
            if (dragging) {
                RenderHelper.drawBeveledBox(matrixStack, xx + 1, yy + 1 + first, xx + bounds.width - 2, yy + 1 + first + size - 1, StyleConfig.colorSliderKnobDraggingTopLeft, StyleConfig.colorSliderKnobDraggingBottomRight, StyleConfig.colorSliderKnobDraggingFiller);
            } else if (isHovering()) {
                RenderHelper.drawBeveledBox(matrixStack, xx + 1, yy + 1 + first, xx + bounds.width - 2, yy + 1 + first + size - 1, StyleConfig.colorSliderKnobHoveringTopLeft, StyleConfig.colorSliderKnobHoveringBottomRight, StyleConfig.colorSliderKnobHoveringFiller);
            } else {
                RenderHelper.drawBeveledBox(matrixStack, xx + 1, yy + 1 + first, xx + bounds.width - 2, yy + 1 + first + size - 1, StyleConfig.colorSliderKnobTopLeft, StyleConfig.colorSliderKnobBottomRight, StyleConfig.colorSliderKnobFiller);
            }
            if (size >= 8) {
                RenderHelper.drawHorizontalLine(matrixStack, xx + 3, yy + 1 + first + size / 2 - 1, xx + bounds.width - 4, StyleConfig.colorSliderKnobMarkerLine);
                if (size >= 10) {
                    RenderHelper.drawHorizontalLine(matrixStack, xx + 3, yy + 1 + first + size / 2 - 2 - 1, xx + bounds.width - 4, StyleConfig.colorSliderKnobMarkerLine);
                    RenderHelper.drawHorizontalLine(matrixStack, xx + 3, yy + 1 + first + size / 2 + 2 - 1, xx + bounds.width - 4, StyleConfig.colorSliderKnobMarkerLine);
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

    private void updateScrollable(double x, double y) {
        int first;
        int divider = scrollable.getMaximum() - scrollable.getCountSelected();
        if (divider <= 0) {
            first = 0;
        } else {
            if (horizontal) {
                int size = calculateKnobSize(divider, bounds.width);
                first = (int) (((x-bounds.x-dx) * divider) / (bounds.width-4-size));
            } else {
                int size = calculateKnobSize(divider, bounds.height);
                first = (int) (((y-bounds.y-dy) * divider) / (bounds.height-4-size));
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

    public Slider event(SliderEvent event) {
        if (sliderEvents == null) {
            sliderEvents = new ArrayList<>();
        }
        sliderEvents.add(event);
        return this;
    }

    public void removeSliderEvent(SliderEvent event) {
        if (sliderEvents != null) {
            sliderEvents.remove(event);
        }
    }

    private void fireSliderEvents() {
        fireChannelEvents();
        if (sliderEvents != null) {
            for (SliderEvent event : sliderEvents) {
                event.sliderReleased();
            }
        }
    }

    @Override
    public boolean mouseScrolled(double x, double y, double amount) {
        int first = scrollable.getFirstSelected();

        int divider = scrollable.getMaximum() - scrollable.getCountSelected();
        if (divider <= 0) {
            first = 0;
        } else {
            if (amount > 0) {
                first -= 3;
            } else if (amount < 0) {
                first += 3;
            }
        }
        if (first > divider) {
            first = divider;
        }
        if (first < 0) {
            first = 0;
        }

        scrollable.setFirstSelected(first);
        return true;
    }

    @Override
    public Widget<?> mouseClick(double x, double y, int button) {
        super.mouseClick(x, y, button);
        dragging = true;
        findScrollable(window);

        int divider = scrollable.getMaximum() - scrollable.getCountSelected();

        int first;
        if (horizontal) {
            int size = calculateKnobSize(divider, bounds.width);
            first = calculateKnobOffset(divider, size, bounds.width);
            dx = (int) (x-bounds.x-first);
            dy = 0;
        } else {
            int size = calculateKnobSize(divider, bounds.height);
            first = calculateKnobOffset(divider, size, bounds.height);
            dx = 0;
            dy = (int) (y-bounds.y-first);
        }

        return this;
    }

    @Override
    public void mouseRelease(double x, double y, int button) {
        super.mouseRelease(x, y, button);
        if (dragging) {
            updateScrollable(x, y);
            dragging = false;
            fireSliderEvents();
        }
    }

    @Override
    public void mouseMove(double x, double y) {
        super.mouseMove(x, y);
        if (dragging) {
            updateScrollable(x, y);
//            System.out.println("x = " + x + "," + y);
        }
    }

    @Override
    public void readFromGuiCommand(GuiParser.GuiCommand command) {
        super.readFromGuiCommand(command);
        horizontal = GuiParser.get(command, "horizontal", DEFAULT_HORIZONTAL);
        minimumKnobSize = GuiParser.get(command, "minimumknob", DEFAULT_MINIMUM_KNOBSIZE);
        scrollableName = GuiParser.get(command, "scrollable", null);
    }

    @Override
    public void fillGuiCommand(GuiParser.GuiCommand command) {
        super.fillGuiCommand(command);
        GuiParser.put(command, "horizontal", horizontal, DEFAULT_HORIZONTAL);
        GuiParser.put(command, "minimumknob", minimumKnobSize, DEFAULT_MINIMUM_KNOBSIZE);
        GuiParser.put(command, "scrollable", scrollableName, null);
    }

    @Override
    public GuiParser.GuiCommand createGuiCommand() {
        return new GuiParser.GuiCommand(TYPE_SLIDER);
    }

    @Override
    public <T> void setGenericValue(T value) {
    }

    @Override
    public Object getGenericValue(Type<?> type) {
        return null;
    }
}
