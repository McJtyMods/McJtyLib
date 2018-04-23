package mcjty.lib.gui.widgets;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.*;
import mcjty.lib.varia.JSonTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class Panel extends AbstractContainerWidget<Panel> {

    public static final String TYPE_PANEL = "panel";

    private Layout layout = new HorizontalLayout();

    private Widget focus = null;

    public Panel(Minecraft mc, Gui gui) {
        super(mc, gui);
    }

    public Panel setLayout(Layout layout) {
        this.layout = layout;
        markDirty();
        return this;
    }

    @Override
    public Widget getWidgetAtPosition(int x, int y) {
        if (isDirty()) {
            layout.doLayout(getChildren(), bounds.width, bounds.height);
            markClean();
        }

        return super.getWidgetAtPosition(x, y);
    }

    @Override
    public void draw(Window window, int x, int y) {
        if (!visible) {
            return;
        }
        super.draw(window, x, y);
        int xx = x + bounds.x;
        int yy = y + bounds.y;
//        drawBox(xx, yy, 0xffff0000);


        if (isDirty()) {
            layout.doLayout(getChildren(), bounds.width, bounds.height);
            markClean();
        }

        for (Widget child : getChildren()) {
            child.draw(window, xx, yy);
        }
    }

    @Override
    public void drawPhase2(Window window, int x, int y) {
        if (!visible) {
            return;
        }
        super.drawPhase2(window, x, y);
        int xx = x + bounds.x;
        int yy = y + bounds.y;
        for (Widget child : getChildren()) {
            child.drawPhase2(window, xx, yy);
        }
    }

    @Override
    public Widget mouseClick(Window window, int x, int y, int button) {
        super.mouseClick(window, x, y, button);

        x -= bounds.x;
        y -= bounds.y;

        for (Widget child : getChildren()) {
            if (child.in(x, y) && child.isVisible()) {
                focus = child.mouseClick(window, x, y, button);
                return this;
            }
        }

        return null;
    }

    @Override
    public void mouseRelease(int x, int y, int button) {
        super.mouseRelease(x, y, button);
        x -= bounds.x;
        y -= bounds.y;

        if (focus != null) {
            focus.mouseRelease(x, y, button);
            focus = null;
        } else {
            for (Widget child : getChildren()) {
                if (child.in(x, y) && child.isVisible()) {
                    child.mouseRelease(x, y, button);
                    return;
                }
            }
        }
    }

    @Override
    public void mouseMove(int x, int y) {
        super.mouseMove(x, y);

        x -= bounds.x;
        y -= bounds.y;

        if (focus != null) {
            focus.mouseMove(x, y);
        } else {
            for (Widget child : getChildren()) {
                if (child.in(x, y) && child.isVisible()) {
                    child.mouseMove(x, y);
                    return;
                }
            }
        }
    }


    @Override
    public void readFromJSon(JsonObject object) {
        super.readFromJSon(object);
        if (object.has("layout")) {
            String l = object.get("layout").getAsString();
            if ("horizontal".equals(l)) {
                layout = new HorizontalLayout();
            } else if ("vertical".equals(l)) {
                layout = new VerticalLayout();
            } else {
                layout = new PositionalLayout();
            }
            AbstractLayout abstractLayout = (AbstractLayout) layout;
            abstractLayout.setSpacing(JSonTools.get(object, "spacing", AbstractLayout.DEFAULT_SPACING));
            abstractLayout.setHorizontalAlignment(HorizontalAlignment.getByName(JSonTools.get(object, "horizalign", AbstractLayout.DEFAULT_HORIZONTAL_ALIGN.name())));
            abstractLayout.setVerticalAlignment(VerticalAlignment.getByName(JSonTools.get(object, "vertalign", AbstractLayout.DEFAULT_VERTICAL_ALIGN.name())));
            abstractLayout.setHorizontalMargin(JSonTools.get(object, "horizmargin", AbstractLayout.DEFAULT_HORIZONTAL_MARGIN));
            abstractLayout.setVerticalMargin(JSonTools.get(object, "vertmargin", AbstractLayout.DEFAULT_VERTICAL_MARGIN));
        }
    }

    @Override
    public JsonObject writeToJSon() {
        JsonObject object = super.writeToJSon();
        object.add("type", new JsonPrimitive(TYPE_PANEL));
        if (layout instanceof HorizontalLayout) {
            object.add("layout", new JsonPrimitive("horizontal"));
        } else if (layout instanceof VerticalLayout) {
            object.add("layout", new JsonPrimitive("vertical"));
        } else if (layout instanceof PositionalLayout) {
            object.add("layout", new JsonPrimitive("positional"));
        }
        if (layout instanceof AbstractLayout) {
            AbstractLayout abstractLayout = (AbstractLayout) layout;
            JSonTools.put(object, "spacing", abstractLayout.getSpacing(), AbstractLayout.DEFAULT_SPACING);
            JSonTools.put(object, "horizalign", abstractLayout.getHorizontalAlignment().name(), AbstractLayout.DEFAULT_HORIZONTAL_ALIGN.name());
            JSonTools.put(object, "vertalign", abstractLayout.getVerticalAlignment().name(), AbstractLayout.DEFAULT_VERTICAL_ALIGN.name());
            JSonTools.put(object, "horizmargin", abstractLayout.getHorizontalMargin(), AbstractLayout.DEFAULT_HORIZONTAL_MARGIN);
            JSonTools.put(object, "vertmargin", abstractLayout.getVerticalMargin(), AbstractLayout.DEFAULT_VERTICAL_MARGIN);
        }
        return object;
    }
}
