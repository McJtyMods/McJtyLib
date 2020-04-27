package mcjty.lib.gui.widgets;

import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.TagSelectorWindow;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.TagChoiceEvent;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.client.gui.screen.Screen;

import java.util.ArrayList;
import java.util.List;

import static mcjty.lib.gui.TagSelectorWindow.TYPE_ITEM;


public class TagSelector extends AbstractLabel<TagSelector> {

    public static final String TYPE_TAGSELECTOR = "tagselector";
    public static final Key<String> PARAM_TAG = new Key<>("tag", Type.STRING);

    private String currentTag = null;
    private List<TagChoiceEvent> choiceEvents = null;
    private final TagSelectorWindow selector = new TagSelectorWindow();
    private String type = TYPE_ITEM;

    public TagSelector() {
        text("");
    }

    public TagSelector current(String tag) {
        currentTag = tag;
        return this;
    }

    public String getCurrentTag() {
        return currentTag;
    }

    @Override
    public void draw(Screen gui, int x, int y) {
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

        String tag = getCurrentTagSafe();
        String[] split = tag.split("[/:]");
        text(split[split.length - 1]); // @todo maybe not very clean like this? Better override getText()

        super.drawOffset(gui, x, y, 0, 1);
    }

    private String getCurrentTagSafe() {
        String tag = getCurrentTag();
        if (tag == null) {
            tag = "<unset>";
        }
        return tag;
    }

    @Override
    public Widget<?> mouseClick(double x, double y, int button) {
        if (isEnabledAndVisible()) {
            selector.create(window, type, t -> {
                current(t);
                fireChoiceEvents(t);
            }, this::getCurrentTag, false);
        }
        return null;
    }


    public TagSelector event(TagChoiceEvent event) {
        if (choiceEvents == null) {
            choiceEvents = new ArrayList<>();
        }
        choiceEvents.add(event);
        return this;
    }

    public void removeChoiceEvent(TagChoiceEvent event) {
        if (choiceEvents != null) {
            choiceEvents.remove(event);
        }
    }

    private void fireChoiceEvents(String tag) {
        fireChannelEvents(TypedMap.builder()
                .put(Window.PARAM_ID, "choice")
                .put(PARAM_TAG, tag)
                .build());
        if (choiceEvents != null) {
            for (TagChoiceEvent event : choiceEvents) {
                event.tagChanged(tag);
            }
        }
    }

    @Override
    public void readFromGuiCommand(GuiParser.GuiCommand command) {
        super.readFromGuiCommand(command);
        type = GuiParser.get(command, "type", TYPE_ITEM);
    }

    @Override
    public void fillGuiCommand(GuiParser.GuiCommand command) {
        super.fillGuiCommand(command);
        command.removeParameter(1); // We don't need the name as set by the label
    }

    @Override
    public GuiParser.GuiCommand createGuiCommand() {
        return new GuiParser.GuiCommand(TYPE_TAGSELECTOR);
    }

    @Override
    public <T> void setGenericValue(T value) {
        current(value == null ? null : value.toString());
    }

    @Override
    public Object getGenericValue(Type<?> type) {
        return getCurrentTag();
    }
}