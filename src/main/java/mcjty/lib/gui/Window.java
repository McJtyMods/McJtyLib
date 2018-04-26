package mcjty.lib.gui;

import mcjty.lib.McJtyLib;
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
import net.minecraft.client.resources.IResource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * This class represents a window. It contains a single Widget which
 * represents the contents of this window. That widget is usually a Panel.
 */
public class Window {

    private final Widget toplevel;
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

    public Window(GuiScreen gui, ResourceLocation guiDescription) {
        this.gui = gui;
        try {
            IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(guiDescription);
            try(BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                GuiParser.GuiCommand command = GuiParser.parse(br);
                String type = command.getId();
                toplevel = WidgetRepository.createWidget(type, Minecraft.getMinecraft(), gui);
                toplevel.readFromGuiCommand(command);
            } catch (GuiParser.ParserException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Keyboard.enableRepeatEvents(true);
    }

    public Widget findChild(String name) {
        Widget widget = ((AbstractContainerWidget) toplevel).findChildRecursive(name);
        if (widget == null) {
            Logging.logError("Could not find widget '" + name + "'!");
        }
        return widget;
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
            toplevel.mouseClick(this, x, y, button);
        }
    }

    public void handleMouseInput() {
        int k = Mouse.getEventButton();
        if (k == -1) {
            mouseMovedOrUp(getRelativeX(), getRelativeY(), k);
        }
    }

    public void mouseMovedOrUp(int x, int y, int button) {
        // -1 == mouse move
        if (button != -1) {
            toplevel.mouseRelease(this, x, y, button);
        } else {
            toplevel.mouseMove(this, x, y);
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
            GuiParser.GuiCommand command = toplevel.createGuiCommand();
            toplevel.fillGuiCommand(command);
            try {
                try(PrintWriter writer = new PrintWriter(new File("output.gui"))) {
                    command.write(writer, 0);
                    writer.flush();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (textFocus != null) {
            return textFocus.keyTyped(this, typedChar, keyCode);
        }
        return false;
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
            toplevel.mouseWheel(this, dwheel, x, y);
        }
        PreferencesProperties properties = McJtyLib.getPreferencesProperties(gui.mc.player);
        if (properties != null) {
            currentStyle = properties.getStyle();
        } else {
            currentStyle = GuiStyle.STYLE_FLAT_GRADIENT;
        }
        toplevel.draw(this, 0, 0);
        toplevel.drawPhase2(this, 0, 0);
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
