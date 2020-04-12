package mcjty.lib.gui;

import mcjty.lib.gui.events.DefaultSelectionEvent;
import mcjty.lib.gui.layout.HorizontalLayout;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.gui.widgets.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TagSelectorWindow {

    private String filter = "";
    private String type;
    private Supplier<String> tagGetter;
    private Consumer<String> tagSetter;
    private boolean onlyDoubleClick;

    public void create(Window window, int x, int y, String type, Consumer<String> callback, Supplier<String> tagGetter,
                       boolean onlyDoubleClick) {
        Minecraft mc = Minecraft.getInstance();
        Screen gui = window.getWindowManager().getGui();
        this.tagSetter = callback;
        this.tagGetter = tagGetter;
        this.onlyDoubleClick = onlyDoubleClick;

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

        refreshList(list, gui);

        mcjty.lib.gui.widgets.TextField filterField = new TextField(mc, gui)
                .setText(filter)
                .addTextEvent((parent, newText) -> {
                    filter = newText;
                    refreshList(list, gui);
                })
                .setLayoutHint(new PositionalLayout.PositionalHint(5, 5, 180, 14));

        modalDialog.addChildren(close, clear, list, slider, filterField);

        Window modalWindow = window.getWindowManager().createModalWindow(modalDialog);

        list.addSelectionEvent(new DefaultSelectionEvent() {
            @Override
            public void select(Widget<?> parent, int index) {
                if (!onlyDoubleClick) {
                    selectTag(index, list);
                }
            }

            @Override
            public void doubleClick(Widget<?> parent, int index) {
                selectTag(index, list);
                window.getWindowManager().closeWindow(modalWindow);
            }
        });

        close.addButtonEvent(parent -> window.getWindowManager().closeWindow(modalWindow));
        clear.addButtonEvent(parent -> {
            callback.accept(null);
            window.getWindowManager().closeWindow(modalWindow);
        });
    }

    private void selectTag(int index, WidgetList list) {
        if (index < list.getChildCount()) {
            Object t = list.getChild(index).getUserObject();
            tagSetter.accept((String)t);
        }
    }


    private java.util.List<String> getTags() {
        if ("both".equals(type)) {
            Set<ResourceLocation> tags = new HashSet<>(ItemTags.getCollection().getRegisteredTags());
            tags.addAll(BlockTags.getCollection().getRegisteredTags());
            return tags.stream().map(ResourceLocation::toString).sorted().collect(Collectors.toList());
        } else if ("item".equals(type)) {
            return ItemTags.getCollection().getRegisteredTags().stream().map(ResourceLocation::toString).sorted().collect(Collectors.toList());
        } else {
            return BlockTags.getCollection().getRegisteredTags().stream().map(ResourceLocation::toString).sorted().collect(Collectors.toList());
        }
    }

    private void refreshList(WidgetList list, Screen gui) {
        list.removeChildren();
        String currentTag = tagGetter.get();
        int sel = -1;
        List<String> tags = getTags();
        for (String tag : tags) {
            if (tag.contains(filter)) {
                Panel panel = new Panel(Minecraft.getInstance(), gui).setLayout(new HorizontalLayout().setHorizontalMargin(0).setSpacing(0));
                panel.setUserObject(tag);
                panel.addChild(new Label(Minecraft.getInstance(), gui).setText(tag));
                list.addChild(panel);
                if (tag.equals(currentTag)) {
                    sel = list.getChildCount() - 1;
                }
            }
        }

        list.setSelected(sel);
    }

}
