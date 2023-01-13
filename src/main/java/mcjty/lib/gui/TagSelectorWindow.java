package mcjty.lib.gui;

import mcjty.lib.gui.events.DefaultSelectionEvent;
import mcjty.lib.gui.widgets.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static mcjty.lib.gui.widgets.Widgets.*;

/**
 * This window is used by the TagSelector so that it's possible to select from a list of
 * tags. It's also possible to use this in a standalone manner
 */
public class TagSelectorWindow {

    public static final String TYPE_BOTH = "both";
    public static final String TYPE_ITEM = "item";
    public static final String TYPE_BLOCK = "block";

    private String filter = "";
    private String type;
    private Supplier<String> tagGetter;
    private Consumer<String> tagSetter;

    /**
     * @param parentWindow
     * @param type one of TYPE_BOTH, TYPE_ITEM, or TYPE_BLOCK
     * @param tagSetter this is fired by this class whenever a new tag is selected (or doubleclicked in case of 'onlyDoubleClick')
     * @param tagGetter this is called to get the current selected tag
     * @param onlyDoubleClick if true then only double click will select a tag
     */
    public void create(Window parentWindow, String type, Consumer<String> tagSetter, Supplier<String> tagGetter,
                       boolean onlyDoubleClick) {
        Screen gui = parentWindow.getWindowManager().getGui();
        this.tagSetter = tagSetter;
        this.tagGetter = tagGetter;
        this.type = type;

        Panel modalDialog = positional().filledRectThickness(2);
        int wx = (int) (parentWindow.getToplevel().getBounds().getX() + 20);
        int wy = (int) (parentWindow.getToplevel().getBounds().getY() + 20);
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

        Window modalWindow = parentWindow.getWindowManager().createModalWindow(modalDialog);

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
                parentWindow.getWindowManager().closeWindow(modalWindow);
            }
        });

        close.event(() -> parentWindow.getWindowManager().closeWindow(modalWindow));
        clear.event(() -> {
            tagSetter.accept(null);
            parentWindow.getWindowManager().closeWindow(modalWindow);
        });
    }

    private void selectTag(int index, WidgetList list) {
        if (index < list.getChildCount()) {
            Object t = list.getChild(index).getUserObject();
            tagSetter.accept((String)t);
        }
    }


    private java.util.List<String> getTags() {
        if (TYPE_BOTH.equals(type)) {
            Stream<String> itemStream = ForgeRegistries.ITEMS.tags().stream().map(t -> t.getKey().location().toString());
            Stream<String> blockStream = ForgeRegistries.BLOCKS.tags().stream().map(t -> t.getKey().location().toString());
            Set<String> tags = itemStream.collect(Collectors.toSet());
            blockStream.forEach(tags::add);
            return tags.stream().sorted().collect(Collectors.toList());
        } else if (TYPE_ITEM.equals(type)) {
            Stream<String> itemStream = ForgeRegistries.ITEMS.tags().stream().map(t -> t.getKey().location().toString());
            Set<String> tags = itemStream.collect(Collectors.toSet());
            return tags.stream().sorted().collect(Collectors.toList());
        } else {
            Stream<String> blockStream = ForgeRegistries.BLOCKS.tags().stream().map(t -> t.getKey().location().toString());
            Set<String> tags = blockStream.collect(Collectors.toSet());
            return tags.stream().sorted().collect(Collectors.toList());
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
