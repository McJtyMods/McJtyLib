package mcjty.lib.gui.widgets;

import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.DefaultSelectionEvent;
import mcjty.lib.gui.events.SelectionEvent;
import mcjty.lib.gui.events.TagChoiceEvent;
import mcjty.lib.gui.layout.HorizontalLayout;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class TagSelector extends AbstractLabel<TagSelector> {

    public static final String TYPE_TAGSELECTOR = "tagselector";
    public static final Key<String> PARAM_TAG = new Key<>("tag", Type.STRING);

    private String currentTag = null;
    private List<TagChoiceEvent> choiceEvents = null;
    private String filter = "";
    private String type = "item";

    public TagSelector(Minecraft mc, Screen gui) {
        super(mc, gui);
        setText("");
    }

    public TagSelector setCurrentTag(String tag) {
        currentTag = tag;
        return this;
    }

    public String getCurrentTag() {
        return currentTag;
    }

    @Override
    public void draw(int x, int y) {
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
        setText(split[split.length - 1]); // @todo maybe not very clean like this? Better override getText()

        super.drawOffset(x, y, 0, 1);
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
            createTagSelectorWindow(window, (int) x, (int) y);
        }
        return null;
    }

    private List<String> getTags() {
        if ("item".equals(type)) {
            return ItemTags.getCollection().getRegisteredTags().stream().map(ResourceLocation::toString).sorted().collect(Collectors.toList());
        } else {
            return BlockTags.getCollection().getRegisteredTags().stream().map(ResourceLocation::toString).sorted().collect(Collectors.toList());
        }
    }

    private void refreshList(WidgetList list) {
        list.removeChildren();
        String currentTag = getCurrentTag();
        int sel = -1;
        List<String> tags = getTags();
        for (String tag : tags) {
            if (tag.contains(filter)) {
                Panel panel = new Panel(mc, gui).setLayout(new HorizontalLayout().setHorizontalMargin(0).setSpacing(0));
                panel.setUserObject(tag);
                panel.addChild(new Label(mc, gui).setText(tag));
                list.addChild(panel);
                if (tag.equals(currentTag)) {
                    sel = list.getChildCount() - 1;
                }
            }
        }

        list.setSelected(sel);
    }

    private void createTagSelectorWindow(Window window, int x, int y) {
        Minecraft mc = Minecraft.getInstance();
        Screen gui = window.getWindowManager().getGui();

        Panel modalDialog = new Panel(mc, gui)
                .setFilledRectThickness(2)
                .setLayout(new PositionalLayout());
        int wx = (int) (window.getToplevel().getBounds().getX() + 20);
        int wy = (int) (window.getToplevel().getBounds().getY() + 20);
        modalDialog.setBounds(new Rectangle(wx, wy, 200, 156));

        WidgetList list = new WidgetList(mc, gui)
                .setName("list")
                .setLayoutHint(new PositionalLayout.PositionalHint(5, 20, 180, 115));
        Slider slider = new Slider(mc, gui).setDesiredWidth(10).setVertical().setScrollableName("list")
                .setLayoutHint(new PositionalLayout.PositionalHint(187, 20, 10, 115));

        Button clear = new Button(mc, gui)
                .setLayoutHint(new PositionalLayout.PositionalHint(5, 156 - 20, 60, 15))
                .setText("Clear");

        Button close = new Button(mc, gui)
                .setLayoutHint(new PositionalLayout.PositionalHint(200 - 65, 156 - 20, 60, 15))
                .setText("Close");

        refreshList(list);

        TextField filterField = new TextField(mc, gui)
                .setText(filter)
                .addTextEvent((parent, newText) -> {
                    filter = newText;
                    refreshList(list);
                })
                .setLayoutHint(new PositionalLayout.PositionalHint(5, 5, 180, 14));

        modalDialog.addChildren(close, clear, list, slider, filterField);

        Window modalWindow = window.getWindowManager().createModalWindow(modalDialog);

        list.addSelectionEvent(new DefaultSelectionEvent() {
            @Override
            public void select(Widget<?> parent, int index) {
                selectTag(index, list);
            }

            @Override
            public void doubleClick(Widget<?> parent, int index) {
                selectTag(index, list);
                window.getWindowManager().closeWindow(modalWindow);
            }
        });

        close.addButtonEvent(parent -> window.getWindowManager().closeWindow(modalWindow));
        clear.addButtonEvent(parent -> {
            setCurrentTag(null);
            fireChoiceEvents(null);
            window.getWindowManager().closeWindow(modalWindow);
        });
    }

    private void selectTag(int index, WidgetList list) {
        if (index < list.getChildCount()) {
            Object t = list.getChild(index).getUserObject();
            setCurrentTag((String) t);
            fireChoiceEvents((String) t);
        }
    }

    public TagSelector addChoiceEvent(TagChoiceEvent event) {
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
                event.tagChanged(this, tag);
            }
        }
    }

    @Override
    public void readFromGuiCommand(GuiParser.GuiCommand command) {
        super.readFromGuiCommand(command);
        type = GuiParser.get(command, "type", "item");
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
        setCurrentTag(value == null ? null : value.toString());
    }

    @Override
    public Object getGenericValue(Type<?> type) {
        return getCurrentTag();
    }
}