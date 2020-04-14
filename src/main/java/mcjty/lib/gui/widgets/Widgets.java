package mcjty.lib.gui.widgets;

import mcjty.lib.gui.layout.HorizontalLayout;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.layout.VerticalLayout;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

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
import static mcjty.lib.gui.widgets.Label.TYPE_LABEL;
import static mcjty.lib.gui.widgets.Panel.TYPE_PANEL;
import static mcjty.lib.gui.widgets.ScrollableLabel.TYPE_SCROLLABLELABEL;
import static mcjty.lib.gui.widgets.Slider.TYPE_SLIDER;
import static mcjty.lib.gui.widgets.TabbedPanel.TYPE_TABBEDPANEL;
import static mcjty.lib.gui.widgets.TagSelector.TYPE_TAGSELECTOR;
import static mcjty.lib.gui.widgets.TextField.TYPE_TEXTFIELD;
import static mcjty.lib.gui.widgets.ToggleButton.TYPE_TOGGLEBUTTON;
import static mcjty.lib.gui.widgets.WidgetList.TYPE_WIDGETLIST;

public class Widgets {

    private static final Map<String, Supplier<Widget<?>>> FACTORIES = new HashMap<>();

    static {
        FACTORIES.put(TYPE_BLOCKRENDER, BlockRender::new);
        FACTORIES.put(TYPE_BUTTON, Button::new);
        FACTORIES.put(TYPE_LABEL, Label::new);
        FACTORIES.put(TYPE_CHOICELABEL, ChoiceLabel::new);
        FACTORIES.put(TYPE_COLORCHOICELABEL, ColorChoiceLabel::new);
        FACTORIES.put(TYPE_COLORSELECTOR, ColorSelector::new);
        FACTORIES.put(TYPE_ENERGYBAR, EnergyBar::new);
        FACTORIES.put(TYPE_ICONHOLDER, IconHolder::new);
        FACTORIES.put(TYPE_ICONRENDER, IconRender::new);
        FACTORIES.put(TYPE_IMAGECHOICELABEL, ImageChoiceLabel::new);
        FACTORIES.put(TYPE_IMAGELABEL, ImageLabel::new);
        FACTORIES.put(TYPE_PANEL, Panel::new);
        FACTORIES.put(TYPE_SCROLLABLELABEL, ScrollableLabel::new);
        FACTORIES.put(TYPE_SLIDER, Slider::new);
        FACTORIES.put(TYPE_TABBEDPANEL, TabbedPanel::new);
        FACTORIES.put(TYPE_TEXTFIELD, TextField::new);
        FACTORIES.put(TYPE_TOGGLEBUTTON, ToggleButton::new);
        FACTORIES.put(TYPE_WIDGETLIST, WidgetList::new);
        FACTORIES.put(TYPE_TAGSELECTOR, TagSelector::new);
    }

    @Nullable
    public static Widget<?> createWidget(String type) {
        Supplier<Widget<?>> function = FACTORIES.get(type);
        if (function == null) {
            return null;
        }
        return function.get();
    }

    public static Panel positional() {
        return new Panel().layout(new PositionalLayout());
    }

    public static Panel horizontal() {
        return new Panel().layout(new HorizontalLayout());
    }

    public static Panel horizontal(int margin, int spacing) {
        return new Panel().layout(new HorizontalLayout().setHorizontalMargin(margin).setSpacing(spacing));
    }

    public static Panel vertical() {
        return new Panel().layout(new VerticalLayout());
    }

    public static Panel vertical(int margin, int spacing) {
        return new Panel().layout(new VerticalLayout().setVerticalMargin(margin).setSpacing(spacing));
    }

    public static Button button(int x, int y, int w, int h, String text) {
        return new Button().hint(x, y, w, h).text(text);
    }

    public static Button button(String text) {
        return new Button().text(text);
    }

    public static TextField textfield(int x, int y, int w, int h) {
        return new TextField().hint(x, y, w, h);
    }

    public static Label label(int x, int y, int w, int h, String text) {
        return new Label().hint(x, y, w, h).text(text);
    }

    public static Label label(String text) {
        return new Label().text(text);
    }

    public static ImageChoiceLabel imageChoice(int x, int y, int w, int h) {
        return new ImageChoiceLabel().hint(x, y, w, h);
    }

    public static WidgetList list(int x, int y, int w, int h) {
        return new WidgetList().hint(x, y, w, h);
    }

    public static Slider slider(int x, int y, int w, int h) {
        return new Slider().hint(x, y, w, h);
    }
}
