package mcjty.lib.gui.widgets;

import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.layout.*;
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
    public void draw(int x, int y) {
        if (!visible) {
            return;
        }
        super.draw(x, y);
        int xx = x + bounds.x;
        int yy = y + bounds.y;
//        drawBox(xx, yy, 0xffff0000);


        if (isDirty()) {
            layout.doLayout(getChildren(), bounds.width, bounds.height);
            markClean();
        }

        for (Widget child : getChildren()) {
            child.setWindow(window);
            child.draw(xx, yy);
        }
    }

    @Override
    public void drawPhase2(int x, int y) {
        if (!visible) {
            return;
        }
        super.drawPhase2(x, y);
        int xx = x + bounds.x;
        int yy = y + bounds.y;
        for (Widget child : getChildren()) {
            child.drawPhase2(xx, yy);
        }
    }

    @Override
    public Widget mouseClick(int x, int y, int button) {
        super.mouseClick(x, y, button);

        x -= bounds.x;
        y -= bounds.y;

        for (Widget child : getChildren()) {
            if (child.in(x, y) && child.isVisible()) {
                focus = child.mouseClick(x, y, button);
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
    public void readFromGuiCommand(GuiParser.GuiCommand command) {
        super.readFromGuiCommand(command);
        String l = command.getOptionalPar(1, "");
        if ("horizontal".equals(l)) {
            layout = new HorizontalLayout();
        } else if ("vertical".equals(l)) {
            layout = new VerticalLayout();
        } else {
            layout = new PositionalLayout();
        }
        AbstractLayout abstractLayout = (AbstractLayout) layout;
        abstractLayout.setSpacing(GuiParser.get(command, "spacing", AbstractLayout.DEFAULT_SPACING));
        abstractLayout.setHorizontalAlignment(HorizontalAlignment.getByName(GuiParser.get(command, "horizalign", AbstractLayout.DEFAULT_HORIZONTAL_ALIGN.name())));
        abstractLayout.setVerticalAlignment(VerticalAlignment.getByName(GuiParser.get(command, "vertalign", AbstractLayout.DEFAULT_VERTICAL_ALIGN.name())));
        abstractLayout.setHorizontalMargin(GuiParser.get(command, "horizmargin", AbstractLayout.DEFAULT_HORIZONTAL_MARGIN));
        abstractLayout.setVerticalMargin(GuiParser.get(command, "vertmargin", AbstractLayout.DEFAULT_VERTICAL_MARGIN));
    }

    @Override
    public void fillGuiCommand(GuiParser.GuiCommand command) {
        super.fillGuiCommand(command);
        if (layout instanceof HorizontalLayout) {
            command.parameter("horizontal");
        } else if (layout instanceof VerticalLayout) {
            command.parameter("vertical");
        } else if (layout instanceof PositionalLayout) {
            command.parameter("positional");
        }
        if (layout instanceof AbstractLayout) {
            AbstractLayout abstractLayout = (AbstractLayout) layout;
            GuiParser.put(command, "spacing", abstractLayout.getSpacing(), AbstractLayout.DEFAULT_SPACING);
            GuiParser.put(command, "horizalign", abstractLayout.getHorizontalAlignment().name(), AbstractLayout.DEFAULT_HORIZONTAL_ALIGN.name());
            GuiParser.put(command, "vertalign", abstractLayout.getVerticalAlignment().name(), AbstractLayout.DEFAULT_VERTICAL_ALIGN.name());
            GuiParser.put(command, "horizmargin", abstractLayout.getHorizontalMargin(), AbstractLayout.DEFAULT_HORIZONTAL_MARGIN);
            GuiParser.put(command, "vertmargin", abstractLayout.getVerticalMargin(), AbstractLayout.DEFAULT_VERTICAL_MARGIN);
        }
    }

    @Override
    public GuiParser.GuiCommand createGuiCommand() {
        return new GuiParser.GuiCommand(TYPE_PANEL);
    }
}
