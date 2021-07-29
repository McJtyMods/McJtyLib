package mcjty.lib.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Scrollable;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.SelectionEvent;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class WidgetList extends AbstractContainerWidget<WidgetList> implements Scrollable {

    public static final String TYPE_WIDGETLIST = "widgetlist";
    public static final Key<Integer> PARAM_INDEX = new Key<>("index", Type.INTEGER);

    public static final int DEFAULT_ROWHEIGHT = 16;
    public static final boolean DEFAULT_PROPAGATE = false;
    public static final boolean DEFAULT_NOSELECTION = false;
    public static final boolean DEFAULT_INVISIBLE_SELECTION = false;
    public static final boolean DEFAULT_DRAW_HORIZONTAL_LINES = true;
    public static final int DEFAULT_LEFT_MARGIN = 2;
    public static final int DEFAULT_TOP_MARGIN = 1;

    private int rowheight = DEFAULT_ROWHEIGHT;
    private int first = 0;
    private int selected = -1;
    private long prevTime = -1;
    private boolean propagateEventsToChildren = DEFAULT_PROPAGATE;
    private List<SelectionEvent> selectionEvents = null;
    private Set<Integer> hilightedRows = new HashSet<>();
    private boolean noselection = DEFAULT_NOSELECTION;
    private boolean invisibleselection = DEFAULT_INVISIBLE_SELECTION;
    private boolean drawHorizontalLines = DEFAULT_DRAW_HORIZONTAL_LINES;
    private int leftMargin = DEFAULT_LEFT_MARGIN;
    private int topMargin = DEFAULT_TOP_MARGIN;

    public WidgetList() {
        filledRectThickness(-1);
        filledBackground(StyleConfig.colorListBackground);
    }

    public int getRowheight() {
        return rowheight;
    }

    // Setting rowheight to -1 will use variable height depending on the desired height of every row
    public WidgetList rowheight(int rowheight) {
        this.rowheight = rowheight;
        return this;
    }

    public boolean isDrawHorizontalLines() {
        return drawHorizontalLines;
    }

    public WidgetList drawHorizontalLines(boolean drawHorizontalLines) {
        this.drawHorizontalLines = drawHorizontalLines;
        return this;
    }

    public int getLeftMargin() {
        return leftMargin;
    }

    public WidgetList leftMargin(int leftMargin) {
        this.leftMargin = leftMargin;
        return this;
    }

    public int getTopMargin() {
        return topMargin;
    }

    public WidgetList topMargin(int topMargin) {
        this.topMargin = topMargin;
        return this;
    }

    public int getSelected() {
        if (selected >= getChildren().size()) {
            return -1;
        }
        return selected;
    }

    public WidgetList selected(int selected) {
        this.selected = selected;
        return this;
    }

    public boolean isPropagateEventsToChildren() {
        return propagateEventsToChildren;
    }

    public boolean isInvisibleSelection() {
        return invisibleselection;
    }

    public WidgetList invisibleSelection(boolean invisibleselection) {
        this.invisibleselection = invisibleselection;
        return this;
    }

    public WidgetList noSelectionMode(boolean m) {
        this.noselection = m;
        return this;
    }

    public boolean isNoSelection() {
        return noselection;
    }

    public WidgetList propagateEventsToChildren(boolean propagateEventsToChildren) {
        this.propagateEventsToChildren = propagateEventsToChildren;
        return this;
    }

    public void addHilightedRow(int index) {
        hilightedRows.add(index);
    }

    public void clearHilightedRows() {
        hilightedRows.clear();
    }

    @Override
    public Widget<?> getWidgetAtPosition(double x, double y) {
        x -= bounds.x;
        y -= bounds.y;

        doLayout();
        for (int i = first ; i < first+getCountSelected() && i < getChildren().size(); i++) {
            Widget<?> child = getChildren().get(i);
            if (child.in(x, y) && child.isVisible()) {
                return child.getWidgetAtPosition(x, y);
            }
        }

        return this;
    }

    private void doLayout() {
        int top = 0;
        for (int i = first ; i < first+getCountSelected() && i < getChildren().size(); i++) {
            Widget<?> child = getChildren().get(i);
            int rh = rowheight == -1 ? child.getDesiredHeight() : rowheight;
            child.bounds(0 /*@@@ margin?*/, top, bounds.width, rh);
            top += rh;
        }
    }

    @Override
    public void draw(Screen gui, PoseStack matrixStack, int x, int y) {
        if (!visible) {
            return;
        }

        // Use the function above to force reset of the slider in case the contents changed so that it doesn't look weird.
        mouseScrolled(x, y, 0);

        super.draw(gui, matrixStack, x, y);
        int xx = x + bounds.x + leftMargin;
        int yy = y + bounds.y + topMargin;
        int top = 0;        // Margin@@@?
//        drawBox(xx, yy, 0xffff0000);

        for (int i = first ; i < first+getCountSelected() && i < getChildren().size(); i++) {
            Widget<?> child = getChildren().get(i);
            int rh = rowheight == -1 ? child.getDesiredHeight() : rowheight;
            child.bounds(0 /*@@@ margin?*/, top, bounds.width, rh);
            boolean hilighted = hilightedRows.contains(i);
            if ((top + rh-1 < bounds.height-3) && drawHorizontalLines) {
                RenderHelper.drawHorizontalLine(matrixStack, xx + 2, yy + top + rh - 1, xx + bounds.width - 7, StyleConfig.colorListSeparatorLine);
            }
            if (!invisibleselection) {
                if (i == selected && hilighted) {
                    RenderHelper.drawHorizontalGradientRect(matrixStack, xx, yy + top + 1, xx + bounds.width - 5, yy + top + rh - 2, StyleConfig.colorListSelectedHighlightedGradient1, StyleConfig.colorListSelectedHighlightedGradient2);
                } else if (i == selected) {
                    RenderHelper.drawHorizontalGradientRect(matrixStack, xx, yy + top + 1, xx + bounds.width - 5, yy + top + rh - 2, StyleConfig.colorListSelectedGradient1, StyleConfig.colorListSelectedGradient2);        // 0xff515151
                } else if (hilighted) {
                    RenderHelper.drawHorizontalGradientRect(matrixStack, xx, yy + top + 1, xx + bounds.width - 5, yy + top + rh - 2, StyleConfig.colorListHighlightedGradient1, StyleConfig.colorListHighlightedGradient2);
                }
            }
            if (isEnabledAndVisible()) {
                child.setWindow(window);
                child.draw(gui, matrixStack, xx, yy);
            } else {
                boolean en = child.isEnabled();
                child.enabled(false);
                child.setWindow(window);
                child.draw(gui, matrixStack, xx, yy);
                child.enabled(en);
            }
            top += rh;
        }
    }

    @Override
    public void drawPhase2(Screen gui, PoseStack matrixStack, int x, int y) {
        if (!visible) {
            return;
        }

        super.drawPhase2(gui, matrixStack, x, y);
        int xx = x + bounds.x + leftMargin;
        int yy = y + bounds.y + topMargin;
        int top = 0;        // Margin@@@?
//        drawBox(xx, yy, 0xffff0000);

        for (int i = first ; i < first+getCountSelected() && i < getChildren().size(); i++) {
            Widget<?> child = getChildren().get(i);
            int rh = rowheight == -1 ? child.getDesiredHeight() : rowheight;
            if (isEnabledAndVisible()) {
                child.drawPhase2(gui, matrixStack, xx, yy);
            }
            top += rh;
        }}

    @Override
    public void mouseMove(double x, double y) {
        if (!isEnabledAndVisible()) {
            return;
        }

        if (noselection) {
            return;
        }

        int newSelected = -1;
        int top = bounds.y;        // Margin@@@?

        for (int i = first ; i < first+getCountSelected() && i < getChildren().size(); i++) {
            int rh = rowheight == -1 ? getChildren().get(i).getDesiredHeight() : rowheight;
            Rectangle r = new Rectangle(bounds.x, top, bounds.width, rh);
            if (r.contains(x, y)) {
                newSelected = i;
                break;
            }
            top += rh;
        }
        if (propagateEventsToChildren && newSelected != -1) {
            Widget<?> child = getSelectedWidgetSafe(newSelected);
            double xx = x-bounds.x;
            double yy = y-bounds.y;
            if (child != null && child.in(xx, yy) && child.isVisible()) {
                child.mouseMove(xx, yy);
            }
        }
    }

    @Override
    public Widget<?> mouseClick(double x, double y, int button) {
        if (!isEnabledAndVisible()) {
            return null;
        }

        if (noselection) {
            return null;
        }

        int newSelected = -1;
        int top = bounds.y;        // Margin@@@?

        for (int i = first ; i < first+getCountSelected() && i < getChildren().size(); i++) {
            int rh = rowheight == -1 ? getChildren().get(i).getDesiredHeight() : rowheight;
            Rectangle r = new Rectangle(bounds.x, top, bounds.width, rh);
            if (r.contains(x, y)) {
                newSelected = i;
                break;
            }
            top += rh;
        }
        if (newSelected != selected) {
            selected = newSelected;
            fireSelectionEvents(selected);
        }

        if (propagateEventsToChildren && selected != -1) {
            Widget<?> child = getSelectedWidgetSafe(selected);
            double xx = x-bounds.x;
            double yy = y-bounds.y;
            if (child != null && child.in(xx, yy) && child.isVisible()) {
                child.mouseClick(xx, yy, button);
            }
        }

        long t = System.currentTimeMillis();
        if (prevTime != -1 && (t - prevTime) < 250) {
            fireDoubleClickEvent(selected);
        }
        prevTime = t;

        return null;
    }

    @Override
    public void mouseRelease(double x, double y, int button) {
        if (!isEnabledAndVisible()) {
            return;
        }
        if (noselection) {
            return;
        }

        if (propagateEventsToChildren && selected != -1) {
            Widget<?> child = getSelectedWidgetSafe(selected);
            double xx = x-bounds.x;
            double yy = y-bounds.y;
            if (child != null && child.in(xx, yy) && child.isVisible()) {
                child.mouseRelease(xx, yy, button);
            }
        }

        super.mouseRelease(x, y, button);
    }

    private Widget<?> getSelectedWidgetSafe(int sel) {
        if (sel < getChildren().size()) {
            return getChildren().get(sel);
        } else {
            return null;
        }
    }

    @Override
    public boolean mouseScrolled(double x, double y, double amount) {
        int divider = getMaximum() - getCountSelected();
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

        return true;
    }

    @Override
    public int getMaximum() {
        return getChildren().size();
    }

    @Override
    public int getCountSelected() {
        if (bounds == null) {
            return 0;
        }
        if (rowheight != -1) {
            return bounds.height / rowheight;
        } else {
            int totalh = 0;
            int cnt = 0;
            for (int i = first; i < getChildren().size(); i++) {
                int rh = getChildren().get(i).getDesiredHeight();
                if (totalh + rh > bounds.height) {
                    break;
                }
                totalh += rh;
                cnt++;
            }
            return cnt;
        }
    }

    @Override
    public int getFirstSelected() {
        return first;
    }

    @Override
    public void setFirstSelected(int first) {
        this.first = first;
    }

    public WidgetList event(SelectionEvent event) {
        if (selectionEvents == null) {
            selectionEvents = new ArrayList<> ();
        }
        selectionEvents.add(event);
        return this;
    }

    public void removeSelectionEvent(SelectionEvent event) {
        if (selectionEvents != null) {
            selectionEvents.remove(event);
        }
    }

    private void fireSelectionEvents(int index) {
        fireChannelEvents(TypedMap.builder()
                .put(Window.PARAM_ID, "select")
                .put(PARAM_INDEX, index)
                .build());
        if (selectionEvents != null) {
            for (SelectionEvent event : selectionEvents) {
                event.select(index);
            }
        }
    }

    private void fireDoubleClickEvent(int index) {
        fireChannelEvents(TypedMap.builder()
                .put(Window.PARAM_ID, "doubleclick")
                .put(PARAM_INDEX, index)
                .build());
        if (selectionEvents != null) {
            for (SelectionEvent event : selectionEvents) {
                event.doubleClick(index);
            }
        }
    }

    @Override
    public WidgetList removeChild(Widget<?> child) {
        int index = getChildren().indexOf(child);
        if (index != -1) {
            Set<Integer> newHighlights = new HashSet<>();
            for (Integer i : hilightedRows) {
                if (i < index) {
                    newHighlights.add(i);
                } else if (i > index) {
                    newHighlights.add(i-1);
                }
            }
            hilightedRows = newHighlights;
        }
        return super.removeChild(child);
    }

    @Override
    public void removeChildren() {
        super.removeChildren();
        hilightedRows.clear();
    }

    @Override
    public void readFromGuiCommand(GuiParser.GuiCommand command) {
        super.readFromGuiCommand(command);
        rowheight = GuiParser.get(command, "rowheight", DEFAULT_ROWHEIGHT);
        propagateEventsToChildren = GuiParser.get(command, "propagate", DEFAULT_PROPAGATE);
        noselection = GuiParser.get(command, "noselection", DEFAULT_NOSELECTION);
        invisibleselection = GuiParser.get(command, "invisibleselection", DEFAULT_INVISIBLE_SELECTION);
        drawHorizontalLines = GuiParser.get(command, "horizontallines", DEFAULT_DRAW_HORIZONTAL_LINES);
        leftMargin = GuiParser.get(command, "leftmargin", DEFAULT_LEFT_MARGIN);
        topMargin = GuiParser.get(command, "topmargin", DEFAULT_TOP_MARGIN);
    }

    @Override
    public void fillGuiCommand(GuiParser.GuiCommand command) {
        super.fillGuiCommand(command);
        GuiParser.put(command, "rowheight", rowheight, DEFAULT_ROWHEIGHT);
        GuiParser.put(command, "propagate", propagateEventsToChildren, DEFAULT_PROPAGATE);
        GuiParser.put(command, "noselection", noselection, DEFAULT_NOSELECTION);
        GuiParser.put(command, "invisibleselection", invisibleselection, DEFAULT_INVISIBLE_SELECTION);
        GuiParser.put(command, "horizontallines", drawHorizontalLines, DEFAULT_DRAW_HORIZONTAL_LINES);
        GuiParser.put(command, "leftmargin", leftMargin, DEFAULT_LEFT_MARGIN);
        GuiParser.put(command, "topmargin", topMargin, DEFAULT_TOP_MARGIN);
    }

    @Override
    public GuiParser.GuiCommand createGuiCommand() {
        return new GuiParser.GuiCommand(TYPE_WIDGETLIST);
    }

    @Override
    public <T> void setGenericValue(T value) {
    }

    @Override
    public Object getGenericValue(Type<?> type) {
        return null;
    }
}
