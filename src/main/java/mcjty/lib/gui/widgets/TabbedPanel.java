package mcjty.lib.gui.widgets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mcjty.lib.gui.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class TabbedPanel extends AbstractContainerWidget<Panel> {

    public static final String TYPE_TABBEDPANEL = "tabbedpanel";

    private Widget current = null;
    private Map<String,Widget> pages = new HashMap<>();

    public TabbedPanel(Minecraft mc, Gui gui) {
        super(mc, gui);
    }

    public TabbedPanel addPage(String name, Widget child) {
        addChild(child);
        pages.put(name, child);
        return this;
    }

    public Widget getCurrent() {
        return current;
    }

    public String getCurrentName() {
        for (Map.Entry<String,Widget> me : pages.entrySet()) {
            if (current == me.getValue()) {
                return me.getKey();
            }
        }
        return null;
    }

    public TabbedPanel setCurrent(Widget current) {
        this.current = current;
        return this;
    }

    public TabbedPanel setCurrent(String name) {
        this.current = pages.get(name);
        return this;
    }

    @Override
    public Widget getWidgetAtPosition(int x, int y) {
        if (current == null) {
            return this;
        }

        setChildBounds();

        x -= bounds.x;
        y -= bounds.y;

        return current.getWidgetAtPosition(x, y);
    }

    @Override
    public void draw(Window window, int x, int y) {
        if (!visible) {
            return;
        }
        super.draw(window, x, y);
        int xx = x + bounds.x;
        int yy = y + bounds.y;
        drawBox(xx, yy, 0xffff0000);

        setChildBounds();

        if (current != null) {
            current.draw(window, xx, yy);
        }
    }

    private void setChildBounds() {
        if (isDirty()) {
            for (Widget child : getChildren()) {
                child.setBounds(new Rectangle(0, 0, getBounds().width, getBounds().height));
            }
            markClean();
        }
    }

    @Override
    public Widget mouseClick(Window window, int x, int y, int button) {
        super.mouseClick(window, x, y, button);

        setChildBounds();

        x -= bounds.x;
        y -= bounds.y;

        if (current != null) {
            if (current.in(x, y) && current.isVisible()) {
                return current.mouseClick(window, x, y, button);
            }
        }

        return null;
    }

    @Override
    public void mouseRelease(int x, int y, int button) {
        super.mouseRelease(x, y, button);

        setChildBounds();

        x -= bounds.x;
        y -= bounds.y;

        if (current != null) {
            current.mouseRelease(x, y, button);
        }
    }

    @Override
    public void mouseMove(int x, int y) {
        super.mouseMove(x, y);

        setChildBounds();

        x -= bounds.x;
        y -= bounds.y;

        if (current != null) {
            current.mouseMove(x, y);
        }
    }


    @Override
    public void readFromJSon(JsonObject object) {
        super.readFromJSon(object);
        JsonArray array = object.getAsJsonArray("pages");
        for (JsonElement element : array) {
            JsonObject o = element.getAsJsonObject();
            String page = o.get("page").getAsString();
            String widgetName = o.get("widget").getAsString();
            Widget child = findChild(widgetName);
            if (child != null) {
                pages.put(page, child);
            }
        }
    }

    @Override
    public JsonObject writeToJSon() {
        JsonObject object = super.writeToJSon();
        object.add("type", new JsonPrimitive(TYPE_TABBEDPANEL));

        JsonArray array = new JsonArray();
        object.add("pages", array);
        for (Map.Entry<String, Widget> entry : pages.entrySet()) {
            JsonObject o = new JsonObject();
            array.add(o);
            o.add("page", new JsonPrimitive(entry.getKey()));
            o.add("widget", new JsonPrimitive(entry.getValue().getName()));
        }

        return object;
    }
}
