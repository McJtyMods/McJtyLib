package mcjty.lib.gui.widgets;

import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractContainerWidget<P extends AbstractContainerWidget<P>> extends AbstractWidget<P> {

    private final List<Widget> children = new ArrayList<>();

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
    public boolean mouseWheel(Window window, int amount, int x, int y) {
        x -= bounds.x;
        y -= bounds.y;

        for (Widget child : children) {
            if (child.in(x, y) && child.isVisible()) {
                if (child.mouseWheel(window, amount, x, y)) {
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

    public P addChildren(Widget... children) {
        for (Widget child : children) {
            addChild(child);
        }
        return (P) this;
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

    public List<Widget> getChildren() {
        return children;
    }

    public Widget getChild(int index) { return children.get(index); }

    public Widget findChild(String name) {
        for (Widget child : children) {
            if (name.equals(child.getName())) {
                return child;
            }
        }
        return null;
    }

    public Widget findChildRecursive(String name) {
        for (Widget child : children) {
            if (name.equals(child.getName())) {
                return child;
            }
            if (child instanceof AbstractContainerWidget) {
                Widget widget = ((AbstractContainerWidget) child).findChildRecursive(name);
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
            Widget widget = WidgetRepository.createWidget(type, mc, gui);
            widget.readFromGuiCommand(cmd);
            children.add(widget);
        });
    }

    @Override
    public void fillGuiCommand(GuiParser.GuiCommand command) {
        super.fillGuiCommand(command);
        for (Widget child : children) {
            GuiParser.GuiCommand childCommand = child.createGuiCommand();
            child.fillGuiCommand(childCommand);
            command.command(childCommand);
        }
    }
}
