package mcjty.lib.gui.widgets;

import mcjty.lib.gui.GuiParser;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractContainerWidget<P extends AbstractContainerWidget<P>> extends AbstractWidget<P> {

    private final List<Widget<?>> children = new ArrayList<>();

    @Override
    public void setBounds(Rectangle bounds) {
        markLayoutDirty();
        super.setBounds(bounds);
    }

    @Override
    public void bounds(int x, int y, int w, int h) {
        markLayoutDirty();
        super.bounds(x, y, w, h);
    }

    @Override
    public Widget<?> getWidgetAtPosition(double x, double y) {
        x -= bounds.x;
        y -= bounds.y;

        for (Widget<?> child : children) {
            if (child.in(x, y) && child.isVisible()) {
                return child.getWidgetAtPosition(x, y);
            }
        }

        return this;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double amount) {
        x -= bounds.x;
        y -= bounds.y;

        for (Widget<?> child : children) {
            if (child.in(x, y) && child.isVisible()) {
                if (child.mouseScrolled(x, y, amount)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean containsWidget(Widget<?> w) {
        if (w == this) {
            return true;
        }
        for (Widget<?> child : children) {
            if (child.containsWidget(w)) {
                return true;
            }
        }
        return false;
    }

    public P children(Widget<?>... children) {
        for (Widget<?> child : children) {
            addChild(child);
        }
        return (P) this;
    }

    private P addChild(Widget<?> child) {
        if (child == null) {
            throw new RuntimeException("THIS IS NOT POSSIBLE!");
        }
        children.add(child);
        markLayoutDirty();
        return (P) this;
    }

    public P removeChild(Widget<?> child) {
        children.remove(child);
        markLayoutDirty();
        return (P) this;
    }

    public void removeChildren() {
        children.clear();
        markLayoutDirty();
    }

    public int getChildCount() {
        return children.size();
    }

    public List<Widget<?>> getChildren() {
        return children;
    }

    public <T extends Widget<?>> T getChild(int index) { return (T) children.get(index); }

    public <T extends Widget<?>> T findChild(String name) {
        for (Widget<?> child : children) {
            if (name.equals(child.getName())) {
                return (T) child;
            }
        }
        return null;
    }

    public Widget<?> findChildRecursive(String name) {
        for (Widget<?> child : children) {
            if (name.equals(child.getName())) {
                return child;
            }
            if (child instanceof AbstractContainerWidget) {
                Widget<?> widget = ((AbstractContainerWidget<?>) child).findChildRecursive(name);
                if (widget != null) {
                    return widget;
                }
            }
        }
        return null;
    }

    @Override
    public void readFromGuiCommand(GuiParser.GuiCommand command) {
        super.readFromGuiCommand(command);
        command.commands().forEach(cmd -> {
            String type = cmd.getId();
            Widget<?> widget = Widgets.createWidget(type);
            if (widget != null) {
                widget.readFromGuiCommand(cmd);
                children.add(widget);
            }
        });
    }

    @Override
    public void fillGuiCommand(GuiParser.GuiCommand command) {
        super.fillGuiCommand(command);
        for (Widget<?> child : children) {
            GuiParser.GuiCommand childCommand = child.createGuiCommand();
            child.fillGuiCommand(childCommand);
            command.command(childCommand);
        }
    }
}
