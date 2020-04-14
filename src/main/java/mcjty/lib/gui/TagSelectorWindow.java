package mcjty.lib.gui;

import mcjty.lib.gui.events.DefaultSelectionEvent;
import mcjty.lib.gui.widgets.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static mcjty.lib.gui.widgets.Widgets.*;

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

        Panel modalDialog = positional().filledRectThickness(2);
        int wx = (int) (window.getToplevel().getBounds().getX() + 20);
        int wy = (int) (window.getToplevel().getBounds().getY() + 20);
        modalDialog.bounds(wx, wy, 200, 156);

        WidgetList list = list(5, 20, 180, 115).name("list");
        Slider slider = slider(187, 20, 10, 115).desiredWidth(10).vertical().scrollableName("list");

        Button clear = button(5, 156 - 20, 60, 15, "Clear");
        Button close = button(200 - 65, 156 - 20, 60, 15, "Close");

        refreshList(list, gui);

        TextField filterField = textfield(5, 5, 180, 14)
                .text(filter)
                .event((newText) -> {
                    filter = newText;
                    refreshList(list, gui);
                });

        modalDialog.children(close, clear, list, slider, filterField);

        Window modalWindow = window.getWindowManager().createModalWindow(modalDialog);

        list.event(new DefaultSelectionEvent() {
            @Override
            public void select(int index) {
                if (!onlyDoubleClick) {
                    selectTag(index, list);
                }
            }

            @Override
            public void doubleClick(int index) {
                selectTag(index, list);
                window.getWindowManager().closeWindow(modalWindow);
            }
        });

        close.event(() -> window.getWindowManager().closeWindow(modalWindow));
        clear.event(() -> {
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
                Panel panel = horizontal(0, 0);
                panel.userObject(tag);
                panel.children(label(tag));
                list.children(panel);
                if (tag.equals(currentTag)) {
                    sel = list.getChildCount() - 1;
                }
            }
        }

        list.selected(sel);
    }

}
