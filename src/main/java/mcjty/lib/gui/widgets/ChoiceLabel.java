package mcjty.lib.gui.widgets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.gui.RenderHelper;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ChoiceEvent;
import mcjty.lib.varia.JSonTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

import java.util.*;

public class ChoiceLabel extends Label<ChoiceLabel> {

    public static final String TYPE_CHOICELABEL = "choicelabel";

    private List<String> choiceList = new ArrayList<>();
    private Map<String,List<String>> tooltipMap = new HashMap<>();
    private String currentChoice = null;
    private List<ChoiceEvent> choiceEvents = null;

    public ChoiceLabel(Minecraft mc, Gui gui) {
        super(mc, gui);
        setText("");
    }

    public ChoiceLabel addChoices(String ... choices) {
        for (String choice : choices) {
            choiceList.add(choice);
            if (currentChoice == null) {
                currentChoice = choice;
                setText(currentChoice);
                fireChoiceEvents(currentChoice);
            }
        }
        return this;
    }

    public ChoiceLabel setChoiceTooltip(String choice, String... tooltips) {
        tooltipMap.put(choice, Arrays.asList(tooltips));
        return this;
    }

    public ChoiceLabel setChoice(String choice) {
        currentChoice = choice;
        setText(currentChoice);
        return this;
    }

    public String getCurrentChoice() {
        return currentChoice;
    }

    @Override
    public List<String> getTooltips() {
        List<String> tooltips = tooltipMap.get(currentChoice);
        if (tooltips == null) {
            return super.getTooltips();
        } else {
            return tooltips;
        }
    }

    @Override
    public void draw(Window window, int x, int y) {
        if (!visible) {
            return;
        }
        int xx = x + bounds.x;
        int yy = y + bounds.y;

        if (isEnabled()) {
            if (isHovering()) {
                drawStyledBoxHovering(window, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
            } else {
                drawStyledBoxNormal(window, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
            }
            RenderHelper.drawLeftTriangle(xx + bounds.width - 10, yy + bounds.height / 2, StyleConfig.colorCycleButtonTriangleNormal);
            RenderHelper.drawRightTriangle(xx + bounds.width - 4, yy + bounds.height / 2, StyleConfig.colorCycleButtonTriangleNormal);
        } else {
            drawStyledBoxDisabled(window, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
            RenderHelper.drawLeftTriangle(xx + bounds.width - 10, yy + bounds.height / 2, StyleConfig.colorCycleButtonTriangleDisabled);
            RenderHelper.drawRightTriangle(xx + bounds.width - 4, yy + bounds.height / 2, StyleConfig.colorCycleButtonTriangleDisabled);
        }

        super.drawOffset(window, x, y, -3, 1);
    }

    @Override
    public Widget mouseClick(Window window, int x, int y, int button) {
        if (isEnabledAndVisible()) {
            if (choiceList.isEmpty()) {
                return null;
            }
            int index = choiceList.indexOf(currentChoice);
            if (button == 1 || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                index--;
                if (index < 0) {
                    index = choiceList.size()-1;
                }
            } else {
                index++;
                if (index >= choiceList.size()) {
                    index = 0;
                }
            }
            currentChoice = choiceList.get(index);
            setText(currentChoice);
            fireChoiceEvents(currentChoice);
        }
        return null;
    }

    public ChoiceLabel addChoiceEvent(ChoiceEvent event) {
        if (choiceEvents == null) {
            choiceEvents = new ArrayList<>();
        }
        choiceEvents.add(event);
        return this;
    }

    public void removeChoiceEvent(ChoiceEvent event) {
        if (choiceEvents != null) {
            choiceEvents.remove(event);
        }
    }

    private void fireChoiceEvents(String choice) {
        if (choiceEvents != null) {
            for (ChoiceEvent event : choiceEvents) {
                event.choiceChanged(this, choice);
            }
        }
    }

    @Override
    public void readFromJSon(JsonObject object) {
        super.readFromJSon(object);
        JsonArray array = object.getAsJsonArray("choices");
        for (JsonElement element : array) {
            JsonObject o = element.getAsJsonObject();
            String choice = o.get("choice").getAsString();
            choiceList.add(choice);
            List<String> tooltips = JSonTools.getStringList(o, "tooltips");
            if (tooltips != null) {
                tooltipMap.put(choice, tooltips);
            }
        }
    }

    @Override
    public JsonObject writeToJSon() {
        JsonObject object = super.writeToJSon();
        object.add("type", new JsonPrimitive(TYPE_CHOICELABEL));
        JsonArray array = new JsonArray();
        object.add("choices", array);
        for (String s : choiceList) {
            JsonObject o = new JsonObject();
            array.add(o);
            o.add("choice", new JsonPrimitive(s));
            JSonTools.putStringList(o, "tooltips", tooltipMap.get(s));
        }

        return object;
    }
}
