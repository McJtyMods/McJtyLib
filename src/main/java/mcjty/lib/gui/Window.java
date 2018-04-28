package mcjty.lib.gui;

import mcjty.lib.McJtyLib;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.events.ChannelEvent;
import mcjty.lib.gui.events.FocusEvent;
import mcjty.lib.gui.widgets.AbstractContainerWidget;
import mcjty.lib.gui.widgets.Widget;
import mcjty.lib.gui.widgets.WidgetRepository;
import mcjty.lib.preferences.PreferencesProperties;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.StringRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.Rectangle;
import java.io.*;
import java.util.*;

/**
 * This class represents a window. It contains a single Widget which
 * represents the contents of this window. That widget is usually a Panel.
 */
public class Window {

    private Widget toplevel;
    private final GuiScreen gui;
    private Widget textFocus = null;
    private Widget hover = null;
    private GuiStyle currentStyle;
    private WindowManager windowManager;

    private final Map<String, List<ChannelEvent>> channelEvents = new HashMap<>();
    private Set<Integer> activeFlags = new HashSet<>();

    private List<FocusEvent> focusEvents = null;


    public Window(GuiScreen gui, Widget toplevel) {
        this.gui = gui;
        this.toplevel = toplevel;
    }

    public GuiScreen getGui() {
        return gui;
    }

    public Window(GuiScreen gui, ResourceLocation guiDescription) {
        this.gui = gui;
        final int[] dim = {-1, -1};
        GuiParserClientTools.parseAndHandleClient(guiDescription, command -> {
            if ("window".equals(command.getId())) {
                command.findCommand("size").ifPresent(cmd -> {
                    dim[0] = cmd.getOptionalPar(0, -1);
                    dim[1] = cmd.getOptionalPar(1, -1);
                });
                command.findCommand("panel").ifPresent(cmd -> {
                    toplevel = WidgetRepository.createWidget("panel", Minecraft.getMinecraft(), gui);
                    toplevel.readFromGuiCommand(cmd);
                });
            }
        });

        if (dim[0] != -1 || dim[1] != -1) {
            if (gui instanceof GenericGuiContainer) {
                ((GenericGuiContainer) gui).setWindowDimensions(dim[0], dim[1]);
            }
            int guiLeft = (gui.width - dim[0]) / 2;
            int guiTop = (gui.height - dim[1]) / 2;
            toplevel.setBounds(new Rectangle(guiLeft, guiTop, dim[0], dim[1]));
        }
        Keyboard.enableRepeatEvents(true);
    }

    public <T extends Widget> T findChild(String name) {
        Widget widget = ((AbstractContainerWidget) toplevel).findChildRecursive(name);
        if (widget == null) {
            Logging.logError("Could not find widget '" + name + "'!");
        }
        return (T) widget;
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public boolean isWidgetOnWindow(Widget w) {
        return toplevel.containsWidget(w);
    }

    public Widget getToplevel() {
        return toplevel;
    }

    public void setFlag(String flag) {
        if (flag.startsWith("!")) {
            // Remove the positive flag
            activeFlags.remove(StringRegister.STRINGS.get(flag.substring(1)));
        } else {
            // Remove the negative flag
            activeFlags.remove(StringRegister.STRINGS.get("!"+flag));
        }
        activeFlags.add(StringRegister.STRINGS.get(flag));
        enableDisableWidgets(toplevel);
    }

    private void enableDisableWidgets(Widget<?> widget) {
        Set<Integer> enabledFlags = widget.getEnabledFlags();
        if (!enabledFlags.isEmpty()) {
            boolean enable = activeFlags.containsAll(enabledFlags);
            widget.setEnabled(enable);
        }
        if (widget instanceof AbstractContainerWidget) {
            for (Widget child : ((AbstractContainerWidget<?>) widget).getChildren()) {
                enableDisableWidgets(child);
            }
        }
    }

    public Widget getWidgetAtPosition(int x, int y) {
        if (toplevel.in(x, y) && toplevel.isVisible()) {
            return toplevel.getWidgetAtPosition(x, y);
        } else {
            return null;
        }
    }

    public void mouseClicked(int x, int y, int button) {
        if (textFocus != null) {
            textFocus = null;
            fireFocusEvents(null);
        }
        if (toplevel.in(x, y) && toplevel.isVisible()) {
            toplevel.setWindow(this);
            toplevel.mouseClick(x, y, button);
        }
    }

    public void handleMouseInput() {
        int k = Mouse.getEventButton();
        if (k == -1) {
            mouseMovedOrUp(getRelativeX(), getRelativeY(), k);
        }
    }

    public void mouseMovedOrUp(int x, int y, int button) {
        toplevel.setWindow(this);
        // -1 == mouse move
        if (button != -1) {
            toplevel.mouseRelease(x, y, button);
        } else {
            toplevel.mouseMove(x, y);
        }
    }

    public void setTextFocus(Widget focus) {
        if (windowManager != null) {
            windowManager.clearFocus();
        }
        setFocus(focus);
    }

    // Package visible for the WindowManager
    void setFocus(Widget focus) {
        if (textFocus != focus) {
            textFocus = focus;
            fireFocusEvents(focus);
        }
    }

    public Widget getTextFocus() {
        return textFocus;
    }

    public boolean keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_F12) {
            GuiParser.GuiCommand windowCmd = createWindowCommand();
            GuiParser.GuiCommand command = toplevel.createGuiCommand();
            toplevel.fillGuiCommand(command);
            windowCmd.command(command);
            try {
                try(PrintWriter writer = new PrintWriter(new File("output.gui"))) {
                    windowCmd.write(writer, 0);
                    writer.flush();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (textFocus != null) {
            return textFocus.keyTyped(typedChar, keyCode);
        }
        return false;
    }

    private GuiParser.GuiCommand createWindowCommand() {
        GuiParser.GuiCommand windowCmd = new GuiParser.GuiCommand("window");
        windowCmd.command(new GuiParser.GuiCommand("size")
                .parameter((int)toplevel.getBounds().getWidth())
                .parameter((int)toplevel.getBounds().getHeight()));
        return windowCmd;
    }

    public void draw() {
        int x = getRelativeX();
        int y = getRelativeY();

        if (hover != null) {
            hover.setHovering(false);
        }
        hover = toplevel.getWidgetAtPosition(x, y);
        if (hover != null) {
            hover.setHovering(true);
        }

        int dwheel;
        if (windowManager == null) {
            dwheel = Mouse.getDWheel();
        } else {
            dwheel = windowManager.getMouseWheel();
            if (dwheel == -1) {
                dwheel = Mouse.getDWheel();
            }
        }
        if (dwheel != 0) {
            toplevel.setWindow(this);
            toplevel.mouseWheel(dwheel, x, y);
        }
        PreferencesProperties properties = McJtyLib.getPreferencesProperties(gui.mc.player);
        if (properties != null) {
            currentStyle = properties.getStyle();
        } else {
            currentStyle = GuiStyle.STYLE_FLAT_GRADIENT;
        }

        toplevel.setWindow(this);
        toplevel.draw(0, 0);
        toplevel.drawPhase2(0, 0);
    }

    public GuiStyle getCurrentStyle() {
        return currentStyle;
    }

    public List<String> getTooltips() {
        int x = getRelativeX();
        int y = getRelativeY();
        if (toplevel.in(x, y) && toplevel.isVisible()) {
            Widget<?> w = toplevel.getWidgetAtPosition(x, y);
            List<String> tooltips = w.getTooltips();
            if (tooltips != null) {
                return tooltips;
            }
        }
        return null;
    }

    public List<ItemStack> getTooltipItems() {
        int x = getRelativeX();
        int y = getRelativeY();
        if (toplevel.in(x, y) && toplevel.isVisible()) {
            Widget<?> w = toplevel.getWidgetAtPosition(x, y);
            List<ItemStack> tooltips = w.getTooltipItems();
            if (tooltips != null) {
                return tooltips;
            }
        }
        return null;
    }

    private int getRelativeX() {
        return Mouse.getEventX() * gui.width / gui.mc.displayWidth;
    }

    private int getRelativeY() {
        return gui.height - Mouse.getEventY() * gui.height / gui.mc.displayHeight - 1;
    }

    public Window addFocusEvent(FocusEvent event) {
        if (focusEvents == null) {
            focusEvents = new ArrayList<>();
        }
        focusEvents.add(event);
        return this;
    }


    private void fireFocusEvents(Widget widget) {
        if (focusEvents != null) {
            for (FocusEvent event : focusEvents) {
                event.focus(this, widget);
            }
        }
    }

    public Window addChannelEvent(String channel, ChannelEvent event) {
        if (!channelEvents.containsKey(channel)) {
            channelEvents.put(channel, new ArrayList<>());
        }
        channelEvents.get(channel).add(event);
        return this;
    }


    public void fireChannelEvents(String channel, Widget widget, String id) {
        if (channelEvents.containsKey(channel)) {
            for (ChannelEvent event : channelEvents.get(channel)) {
                event.fire(widget, id);
            }
        }
    }
}
