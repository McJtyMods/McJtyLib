package mcjty.lib.gui.widgets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class AbstractContainerWidget<P extends AbstractContainerWidget<P>> extends AbstractWidget<P> {

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
    public void readFromJSon(JsonObject object) {
        super.readFromJSon(object);
        if (object.has("children")) {
            JsonArray array = object.getAsJsonArray("children");
            children.clear();
            for (JsonElement element : array) {
                JsonObject co = element.getAsJsonObject();
                String type = co.get("type").getAsString();
                Widget widget = WidgetRepository.createWidget(type, mc, gui);
                widget.readFromJSon(co);
                children.add(widget);
            }
        }
    }

    @Override
    public JsonObject writeToJSon() {
        JsonObject object = super.writeToJSon();
        JsonArray array = new JsonArray();
        object.add("children", array);
        for (Widget child : children) {
            array.add(child.writeToJSon());
        }
        return object;
    }
}
