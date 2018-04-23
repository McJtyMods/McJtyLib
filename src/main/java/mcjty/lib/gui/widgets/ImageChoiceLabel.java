package mcjty.lib.gui.widgets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ChoiceEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageChoiceLabel extends ImageLabel<ImageChoiceLabel> {

    public static final String TYPE_IMAGECHOICELABEL = "imagechoicelabel";

    private static class ChoiceEntry {
        private final String choice;
        private final List<String> tooltips;
        private final ResourceLocation image;
        private final int u;
        private final int v;

        public ChoiceEntry(String choice, List<String> tooltips, ResourceLocation image, int u, int v) {
            this.choice = choice;
            this.tooltips = tooltips;
            this.image = image;
            this.u = u;
            this.v = v;
        }

        public String getChoice() {
            return choice;
        }

        public List<String> getTooltips() {
            return tooltips;
        }

        public ResourceLocation getImage() {
            return image;
        }

        public int getU() {
            return u;
        }

        public int getV() {
            return v;
        }
    }

    private List<ChoiceEntry> choiceList = new ArrayList<>();
    private boolean withBorder = false;
    private int highlightedChoice = -1;     // If this choice is selected we 'highlight' it (only in combination with withBorder=true)

    private int currentChoice = -1;
    private List<ChoiceEvent> choiceEvents = null;

    public ImageChoiceLabel(Minecraft mc, Gui gui) {
        super(mc, gui);
    }

    public boolean isWithBorder() {
        return withBorder;
    }

    public ImageChoiceLabel setWithBorder(boolean withBorder) {
        this.withBorder = withBorder;
        return this;
    }

    public int getHighlightedChoice() {
        return highlightedChoice;
    }

    public ImageChoiceLabel setHighlightedChoice(int highlightedChoice) {
        this.highlightedChoice = highlightedChoice;
        return this;
    }

    @Override
    public List<String> getTooltips() {
        if (currentChoice == -1) {
            return super.getTooltips();
        } else {
            return choiceList.get(currentChoice).getTooltips();
        }
    }

    public ImageChoiceLabel addChoice(String choice, String tooltips, ResourceLocation image, int u, int v) {
        choiceList.add(new ChoiceEntry(choice, Arrays.asList(StringUtils.split(tooltips, "\n")), image, u, v));
        if (currentChoice == -1) {
            currentChoice = 0;
            setCurrentChoice(currentChoice);
        }
        return this;
    }

    public void clear() {
        choiceList.clear();
        currentChoice = -1;
    }

    @Override
    public Widget mouseClick(Window window, int x, int y, int button) {
        if (isEnabledAndVisible()) {
            if (button == 1 || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                currentChoice--;
                if (currentChoice < 0) {
                    currentChoice = choiceList.size() - 1;
                }
            } else {
                currentChoice++;
                if (currentChoice >= choiceList.size()) {
                    currentChoice = 0;
                }
            }
            setCurrentChoice(currentChoice);
            fireChoiceEvents(choiceList.get(currentChoice).getChoice());
        }
        return null;
    }


    public void setCurrentChoice(int currentChoice) {
        this.currentChoice = currentChoice;
        setImage(choiceList.get(currentChoice).getImage(), choiceList.get(currentChoice).getU(), choiceList.get(currentChoice).getV());
    }

    public int findChoice(String choice) {
        if (choice == null) {
            return -1;
        }
        int i = 0;
        for (ChoiceEntry s : choiceList) {
            if (choice.equals(s.getChoice())) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public void setCurrentChoice(String choice) {
        int idx = findChoice(choice);
        if (idx != -1) {
            setCurrentChoice(idx);
        }
    }

    public int getCurrentChoiceIndex() {
        return currentChoice;
    }

    public String getCurrentChoice() {
        if (currentChoice == -1) {
            return null;
        }
        return choiceList.get(currentChoice).getChoice();
    }

    public ImageChoiceLabel addChoiceEvent(ChoiceEvent event) {
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
    public void draw(Window window, int x, int y) {
        if (withBorder) {
            int xx = x + bounds.x;
            int yy = y + bounds.y;

            if (isEnabled()) {
                if (currentChoice == highlightedChoice) {
                    drawStyledBoxSelected(window, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
                } else if (isHovering()) {
                    drawStyledBoxHovering(window, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
                } else {
                    drawStyledBoxNormal(window, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
                }
            } else {
                drawStyledBoxDisabled(window, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
            }
        }

        super.draw(window, x, y);
    }


    @Override
    public void readFromJSon(JsonObject object) {
        super.readFromJSon(object);
        JsonArray array = object.getAsJsonArray("choices");
        for (JsonElement element : array) {
            JsonObject o = element.getAsJsonObject();
            String choice = o.get("choice").getAsString();
            JsonArray tarray = o.getAsJsonArray("tooltips");
            List<String> tooltips = new ArrayList<>();
            for (JsonElement tt : tarray) {
                tooltips.add(tt.getAsString());
            }
            ResourceLocation image = new ResourceLocation(o.get("image").getAsString());
            int u = o.get("u").getAsInt();
            int v = o.get("v").getAsInt();
            choiceList.add(new ChoiceEntry(choice, tooltips, image, u, v));
        }
        withBorder = object.get("withborder").getAsBoolean();
    }

    @Override
    public JsonObject writeToJSon() {
        JsonObject object = super.writeToJSon();
        object.add("type", new JsonPrimitive(TYPE_IMAGECHOICELABEL));
        JsonArray array = new JsonArray();
        object.add("choices", array);
        for (ChoiceEntry entry : choiceList) {
            JsonObject o = new JsonObject();
            array.add(o);
            o.add("choice", new JsonPrimitive(entry.getChoice()));
            JsonArray tarray = new JsonArray();
            o.add("tooltips", tarray);
            for (String tooltip : entry.getTooltips()) {
                tarray.add(new JsonPrimitive(tooltip));
            }
            o.add("image", new JsonPrimitive(entry.getImage().toString()));
            o.add("u", new JsonPrimitive(entry.getU()));
            o.add("v", new JsonPrimitive(entry.getV()));
        }
        object.add("withborder", new JsonPrimitive(withBorder));
        return object;
    }
}
