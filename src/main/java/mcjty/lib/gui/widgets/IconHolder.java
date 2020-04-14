package mcjty.lib.gui.widgets;

import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.*;
import mcjty.lib.gui.icons.IIcon;
import mcjty.lib.gui.icons.IconManager;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.client.gui.screen.Screen;

import java.util.ArrayList;
import java.util.List;

public class IconHolder extends AbstractWidget<IconHolder> {

    public static final String TYPE_ICONHOLDER = "iconholder";
    public static final Key<Integer> PARAM_DX = new Key<>("dx", Type.INTEGER);
    public static final Key<Integer> PARAM_DY = new Key<>("dy", Type.INTEGER);

    public static final boolean DEFAULT_SELECTABLE = false;
    public static final int DEFAULT_BORDER = 0;
    public static final int DEFAULT_SELECTED_BORDER_COLOR = 0xffffffff;

    private IIcon icon;
    private boolean makeCopy = false;
    private List<IconArrivesEvent> iconArrivesEvents = null;
    private List<IconLeavesEvent> iconLeavesEvents = null;
    private List<IconClickedEvent> iconClickedEvents = null;
    private List<IconHolderClickedEvent> iconHolderClickedEvents = null;
    private List<IconHoverEvent> iconHoverEvents = null;

    private boolean selectable = DEFAULT_SELECTABLE;

    private int border = DEFAULT_BORDER;
    private Integer borderColor = null;
    private Integer selectedBorderColor = DEFAULT_SELECTED_BORDER_COLOR;

    public IIcon getIcon() {
        return icon;
    }

    public boolean setIcon(IIcon icon) {
        if (fireIconArrived(icon)) {
            this.icon = icon;
            return true;
        }
        return false;
    }

    public int getBorder() {
        return border;
    }

    public IconHolder border(int border) {
        this.border = border;
        return this;
    }

    public Integer getBorderColor() {
        return borderColor;
    }

    public IconHolder borderColor(Integer borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public Integer getSelectedBorderColor() {
        return selectedBorderColor;
    }

    public IconHolder selectedBorderColor(Integer selectedBorderColor) {
        this.selectedBorderColor = selectedBorderColor;
        return this;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public IconHolder selectable(boolean selectable) {
        this.selectable = selectable;
        return this;
    }

    public boolean isMakeCopy() {
        return makeCopy;
    }

    public IconHolder makeCopy(boolean makeCopy) {
        this.makeCopy = makeCopy;
        return this;
    }

    @Override
    public void mouseMove(double x, double y) {
        if (isEnabledAndVisible()) {
            int dx = (int) (x - this.bounds.x - border);
            int dy = (int) (y - this.bounds.y - border);
            fireIconHover(icon, dx, dy);
        }
    }

    @Override
    public Widget<?> mouseClick(double x, double y, int button) {
        if (isEnabledAndVisible()) {
//            if (McJtyLib.proxy.isShiftKeyDown()) {
            // @todo 1.14
            if (false) {
            } else {
                if (selectable) {
                    window.setTextFocus(this);
                }
                int dx = (int) (x - this.bounds.x - border);
                int dy = (int) (y - this.bounds.y - border);
                fireIconHolderClicked(icon, dx, dy);
                if (icon != null) {
                    if (fireIconClicked(icon, dx, dy)) {
                        if (fireIconLeaves(icon)) {
                            IconManager iconManager = window.getWindowManager().getIconManager();
                            if (makeCopy) {
                                iconManager.startDragging(icon.clone(), this, dx, dy);
                            } else {
                                iconManager.startDragging(icon, this, dx, dy);
                                icon = null;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }


    @Override
    public void draw(Screen gui, int x, int y) {
        if (!visible) {
            return;
        }
        super.draw(gui, x, y);

        int xx = x + bounds.x;
        int yy = y + bounds.y;

        if (border > 0) {
            if (borderColor != null) {
                if ((!selectable) || window.getTextFocus() != this) {
                    RenderHelper.drawFlatBox(xx, yy, xx + bounds.width, yy + bounds.height, borderColor, -1);
                }
            }
        }

        if (icon != null) {
            icon.draw(mc, gui, xx + border, yy + border);
        }
    }

    @Override
    public void drawPhase2(Screen gui, int x, int y) {
        if (!visible) {
            return;
        }
        super.drawPhase2(gui, x, y);
        if (border > 0 && selectable && selectedBorderColor != null && window.getTextFocus() == this) {
            int xx = x + bounds.x;
            int yy = y + bounds.y;
            RenderHelper.drawFlatBox(xx, yy, xx + bounds.width, yy + bounds.height, selectedBorderColor, -1);
        }
    }

    public IconHolder arrivesEvent(IconArrivesEvent event) {
        if (iconArrivesEvents == null) {
            iconArrivesEvents = new ArrayList<>();
        }
        iconArrivesEvents.add(event);
        return this;
    }

    private boolean fireIconArrived(IIcon icon) {
        fireChannelEvents("arrived");
        if (iconArrivesEvents != null) {
            for (IconArrivesEvent event : iconArrivesEvents) {
                boolean b = event.iconArrives(icon);
                if (!b) {
                    return false;
                }
            }
        }
        return true;
    }

    public IconHolder leavesEvent(IconLeavesEvent event) {
        if (iconLeavesEvents == null) {
            iconLeavesEvents = new ArrayList<>();
        }
        iconLeavesEvents.add(event);
        return this;
    }


    private boolean fireIconLeaves(IIcon icon) {
        fireChannelEvents("leaves");
        if (iconLeavesEvents != null) {
            for (IconLeavesEvent event : iconLeavesEvents) {
                boolean b = event.iconLeaves(icon);
                if (!b) {
                    return false;
                }
            }
        }
        return true;
    }

    public IconHolder clickedEvent(IconClickedEvent event) {
        if (iconClickedEvents == null) {
            iconClickedEvents = new ArrayList<>();
        }
        iconClickedEvents.add(event);
        return this;
    }


    private boolean fireIconClicked(IIcon icon, int dx, int dy) {
        fireChannelEvents(TypedMap.builder()
                .put(Window.PARAM_ID, "clicked")
                .put(PARAM_DX, dx)
                .put(PARAM_DY, dy)
                .build());
        if (iconClickedEvents != null) {
            for (IconClickedEvent event : iconClickedEvents) {
                boolean b = event.iconClicked(icon, dx, dy);
                if (!b) {
                    return false;
                }
            }
        }
        return true;
    }

    public IconHolder holderClickedEvent(IconHolderClickedEvent event) {
        if (iconHolderClickedEvents == null) {
            iconHolderClickedEvents = new ArrayList<>();
        }
        iconHolderClickedEvents.add(event);
        return this;
    }


    private void fireIconHolderClicked(IIcon icon, int dx, int dy) {
        fireChannelEvents(TypedMap.builder()
                .put(Window.PARAM_ID, "holderclicked")
                .put(PARAM_DX, dx)
                .put(PARAM_DY, dy)
                .build());
        if (iconHolderClickedEvents != null) {
            for (IconHolderClickedEvent event : iconHolderClickedEvents) {
                event.holderClicked(icon, dx, dy);
            }
        }
    }

    public IconHolder hoverEvent(IconHoverEvent event) {
        if (iconHoverEvents == null) {
            iconHoverEvents = new ArrayList<>();
        }
        iconHoverEvents.add(event);
        return this;
    }


    private void fireIconHover(IIcon icon, int dx, int dy) {
        fireChannelEvents(TypedMap.builder()
                .put(Window.PARAM_ID, "hover")
                .put(PARAM_DX, dx)
                .put(PARAM_DY, dy)
                .build());
        if (iconHoverEvents != null) {
            for (IconHoverEvent event : iconHoverEvents) {
                event.hover(icon, dx, dy);
            }
        }
    }


    @Override
    public void readFromGuiCommand(GuiParser.GuiCommand command) {
        super.readFromGuiCommand(command);
        selectable = GuiParser.get(command, "selectable", DEFAULT_SELECTABLE);
        border = GuiParser.get(command, "border", DEFAULT_BORDER);
        selectedBorderColor = GuiParser.get(command, "selectedbordercolor", DEFAULT_SELECTED_BORDER_COLOR);
    }

    @Override
    public void fillGuiCommand(GuiParser.GuiCommand command) {
        super.fillGuiCommand(command);
        GuiParser.put(command, "selectable", selectable, DEFAULT_SELECTABLE);
        GuiParser.put(command, "border", border, DEFAULT_BORDER);
        GuiParser.put(command, "selectedbordercolor", selectedBorderColor, DEFAULT_SELECTED_BORDER_COLOR);
    }

    @Override
    public GuiParser.GuiCommand createGuiCommand() {
        return new GuiParser.GuiCommand(TYPE_ICONHOLDER);
    }

    @Override
    public <T> void setGenericValue(T value) {
    }

    @Override
    public Object getGenericValue(Type<?> type) {
        return null;
    }
}
