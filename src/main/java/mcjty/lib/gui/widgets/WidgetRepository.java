package mcjty.lib.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static mcjty.lib.gui.widgets.BlockRender.TYPE_BLOCKRENDER;
import static mcjty.lib.gui.widgets.Button.TYPE_BUTTON;
import static mcjty.lib.gui.widgets.ChoiceLabel.TYPE_CHOICELABEL;
import static mcjty.lib.gui.widgets.ColorChoiceLabel.TYPE_COLORCHOICELABEL;
import static mcjty.lib.gui.widgets.ColorSelector.TYPE_COLORSELECTOR;
import static mcjty.lib.gui.widgets.EnergyBar.TYPE_ENERGYBAR;
import static mcjty.lib.gui.widgets.IconHolder.TYPE_ICONHOLDER;
import static mcjty.lib.gui.widgets.IconRender.TYPE_ICONRENDER;
import static mcjty.lib.gui.widgets.ImageChoiceLabel.TYPE_IMAGECHOICELABEL;
import static mcjty.lib.gui.widgets.ImageLabel.TYPE_IMAGELABEL;
import static mcjty.lib.gui.widgets.Panel.TYPE_PANEL;
import static mcjty.lib.gui.widgets.ScrollableLabel.TYPE_SCROLLABLELABEL;
import static mcjty.lib.gui.widgets.Slider.TYPE_SLIDER;
import static mcjty.lib.gui.widgets.TabbedPanel.TYPE_TABBEDPANEL;
import static mcjty.lib.gui.widgets.TextField.TYPE_TEXTFIELD;
import static mcjty.lib.gui.widgets.ToggleButton.TYPE_TOGGLEBUTTON;
import static mcjty.lib.gui.widgets.WidgetList.TYPE_WIDGETLIST;

public class WidgetRepository {

    private static final Map<String, BiFunction<Minecraft, Gui, Widget>> FACTORIES = new HashMap<>();

    static {
        FACTORIES.put(TYPE_BLOCKRENDER, (minecraft, gui) -> new BlockRender(minecraft, gui));
        FACTORIES.put(TYPE_BUTTON, (minecraft, gui) -> new Button(minecraft, gui));
        FACTORIES.put(TYPE_CHOICELABEL, (minecraft, gui) -> new ChoiceLabel(minecraft, gui));
        FACTORIES.put(TYPE_COLORCHOICELABEL, (minecraft, gui) -> new ColorChoiceLabel(minecraft, gui));
        FACTORIES.put(TYPE_COLORSELECTOR, (minecraft, gui) -> new ColorSelector(minecraft, gui));
        FACTORIES.put(TYPE_ENERGYBAR, (minecraft, gui) -> new EnergyBar(minecraft, gui));
        FACTORIES.put(TYPE_ICONHOLDER, (minecraft, gui) -> new IconHolder(minecraft, gui));
        FACTORIES.put(TYPE_ICONRENDER, (minecraft, gui) -> new IconRender(minecraft, gui));
        FACTORIES.put(TYPE_IMAGECHOICELABEL, (minecraft, gui) -> new ImageChoiceLabel(minecraft, gui));
        FACTORIES.put(TYPE_IMAGELABEL, (minecraft, gui) -> new ImageLabel(minecraft, gui));
        FACTORIES.put(TYPE_PANEL, (minecraft, gui) -> new Panel(minecraft, gui));
        FACTORIES.put(TYPE_SCROLLABLELABEL, (minecraft, gui) -> new ScrollableLabel(minecraft, gui));
        FACTORIES.put(TYPE_SLIDER, (minecraft, gui) -> new Slider(minecraft, gui));
        FACTORIES.put(TYPE_TABBEDPANEL, (minecraft, gui) -> new TabbedPanel(minecraft, gui));
        FACTORIES.put(TYPE_TEXTFIELD, (minecraft, gui) -> new TextField(minecraft, gui));
//        FACTORIES.put(TYPE_TEXTPAGE, (minecraft, gui) -> new TextPage(minecraft, gui));
        FACTORIES.put(TYPE_TOGGLEBUTTON, (minecraft, gui) -> new ToggleButton(minecraft, gui));
        FACTORIES.put(TYPE_WIDGETLIST, (minecraft, gui) -> new WidgetList(minecraft, gui));
    }

    public static Widget createWidget(String type, Minecraft minecraft, Gui gui) {
        return FACTORIES.get(type).apply(minecraft, gui);
    }
}
