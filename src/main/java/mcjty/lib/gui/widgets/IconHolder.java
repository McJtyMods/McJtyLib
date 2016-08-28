package mcjty.lib.gui.widgets;

import mcjty.lib.gui.RenderHelper;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.IconArrivesEvent;
import mcjty.lib.gui.events.IconClickedEvent;
import mcjty.lib.gui.events.IconLeavesEvent;
import mcjty.lib.gui.icons.IIcon;
import mcjty.lib.gui.icons.IconManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class IconHolder extends AbstractWidget<IconHolder> {

    private IIcon icon;
    private boolean makeCopy = false;
    private List<IconArrivesEvent> iconArrivesEvents = null;
    private List<IconLeavesEvent> iconLeavesEvents = null;
    private List<IconClickedEvent> iconClickedEvents = null;

    private boolean selectable = false;

    private int border = 0;
    private Integer borderColor = null;
    private Integer selectedBorderColor = 0xffffffff;

    public IconHolder(Minecraft mc, Gui gui) {
        super(mc, gui);
    }

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

    public IconHolder setBorder(int border) {
        this.border = border;
        return this;
    }

    public Integer getBorderColor() {
        return borderColor;
    }

    public IconHolder setBorderColor(Integer borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public Integer getSelectedBorderColor() {
        return selectedBorderColor;
    }

    public IconHolder setSelectedBorderColor(Integer selectedBorderColor) {
        this.selectedBorderColor = selectedBorderColor;
        return this;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public IconHolder setSelectable(boolean selectable) {
        this.selectable = selectable;
        return this;
    }

    public boolean isMakeCopy() {
        return makeCopy;
    }

    public IconHolder setMakeCopy(boolean makeCopy) {
        this.makeCopy = makeCopy;
        return this;
    }

    @Override
    public Widget mouseClick(Window window, int x, int y, int button) {
        if (isEnabledAndVisible()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            } else {
                if (selectable) {
                    window.setTextFocus(this);
                }
                if (icon != null) {
                    int dx = x - this.bounds.x - border;
                    int dy = y - this.bounds.y - border;
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
    public void draw(Window window, int x, int y) {
        if (!visible) {
            return;
        }
        super.draw(window, x, y);

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
    public void drawPhase2(Window window, int x, int y) {
        if (!visible) {
            return;
        }
        super.drawPhase2(window, x, y);
        if (border > 0 && selectable && selectedBorderColor != null && window.getTextFocus() == this) {
            int xx = x + bounds.x;
            int yy = y + bounds.y;
            RenderHelper.drawFlatBox(xx, yy, xx + bounds.width, yy + bounds.height, selectedBorderColor, -1);
        }
    }

    public IconHolder addIconArrivesEvent(IconArrivesEvent event) {
        if (iconArrivesEvents == null) {
            iconArrivesEvents = new ArrayList<>();
        }
        iconArrivesEvents.add(event);
        return this;
    }

    private boolean fireIconArrived(IIcon icon) {
        if (iconArrivesEvents != null) {
            for (IconArrivesEvent event : iconArrivesEvents) {
                boolean b = event.iconArrives(this, icon);
                if (!b) {
                    return false;
                }
            }
        }
        return true;
    }

    public IconHolder addIconLeavesEvent(IconLeavesEvent event) {
        if (iconLeavesEvents == null) {
            iconLeavesEvents = new ArrayList<>();
        }
        iconLeavesEvents.add(event);
        return this;
    }


    private boolean fireIconLeaves(IIcon icon) {
        if (iconLeavesEvents != null) {
            for (IconLeavesEvent event : iconLeavesEvents) {
                boolean b = event.iconLeaves(this, icon);
                if (!b) {
                    return false;
                }
            }
        }
        return true;
    }

    public IconHolder addIconClickedEvent(IconClickedEvent event) {
        if (iconClickedEvents == null) {
            iconClickedEvents = new ArrayList<>();
        }
        iconClickedEvents.add(event);
        return this;
    }


    private boolean fireIconClicked(IIcon icon, int dx, int dy) {
        if (iconClickedEvents != null) {
            for (IconClickedEvent event : iconClickedEvents) {
                boolean b = event.iconClicked(this, icon, dx, dy);
                if (!b) {
                    return false;
                }
            }
        }
        return true;
    }
}
