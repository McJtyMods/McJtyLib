package mcjty.lib.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AbstractContainerWidget<P extends AbstractContainerWidget<P>> extends AbstractWidget<P> {
    protected List<Widget> children = new ArrayList<>();

    public AbstractContainerWidget(Minecraft mc, Gui gui) {
        super(mc, gui);
    }

    @Override
    public void setBounds(Rectangle bounds) {
        markDirty();
        super.setBounds(bounds);
    }

    @Override
    public Widget getWidgetAtPosition(int x, int y) {
        x -= bounds.x;
        y -= bounds.y;

        for (Widget child : children) {
            if (child.in(x, y) && child.isVisible()) {
                return child.getWidgetAtPosition(x, y);
            }
        }

        return this;
    }

    @Override
    public boolean mouseWheel(int amount, int x, int y) {
        x -= bounds.x;
        y -= bounds.y;

        for (Widget child : children) {
            if (child.in(x, y) && child.isVisible()) {
                if (child.mouseWheel(amount, x, y)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean containsWidget(Widget w) {
        if (w == this) {
            return true;
        }
        for (Widget child : children) {
            if (child.containsWidget(w)) {
                return true;
            }
        }
        return false;
    }

    public P addChild(Widget child) {
        if (child == null) {
            throw new RuntimeException("THIS IS NOT POSSIBLE!");
        }
        children.add(child);
        markDirty();
        return (P) this;
    }

    public P removeChild(Widget child) {
        children.remove(child);
        markDirty();
        return (P) this;
    }

    public void removeChildren() {
        children.clear();
        markDirty();
    }

    public int getChildCount() {
        return children.size();
    }

    public Widget getChild(int index) { return children.get(index); }
}
