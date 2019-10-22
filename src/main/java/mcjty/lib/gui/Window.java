package mcjty.lib.gui;

import mcjty.lib.McJtyLib;
import mcjty.lib.bindings.IAction;
import mcjty.lib.bindings.IValue;
import mcjty.lib.gui.events.ChannelEvent;
import mcjty.lib.gui.events.FocusEvent;
import mcjty.lib.gui.widgets.AbstractContainerWidget;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.Widget;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.StringRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 * This class represents a window. It contains a single Widget which
 * represents the contents of this window. That widget is usually a Panel.
 */
public class Window {

    public static final Key<String> PARAM_ID = new Key<>("id", Type.STRING);

    private AbstractContainerWidget<?> toplevel;
    private final Screen gui;
    private Widget<?> textFocus = null;
    private Widget<?> hover = null;
    private GuiStyle currentStyle;
    private WindowManager windowManager;

    private final Map<String, List<ChannelEvent>> channelEvents = new HashMap<>();
    private Set<Integer> activeFlags = new HashSet<>();

    private List<FocusEvent> focusEvents = null;


    public Window(Screen gui, AbstractContainerWidget<?> toplevel) {
        this.gui = gui;
        this.toplevel = toplevel;
    }

    public Screen getGui() {
        return gui;
    }

    public Window(Screen gui, GenericTileEntity tileEntity, SimpleChannel wrapper, ResourceLocation guiDescription) {
        this.gui = gui;
        final int[] dim = {-1, -1};
        final int[] sidesize = {0, 0};
        GuiParserClientTools.parseAndHandleClient(guiDescription, command -> {
            if ("window".equals(command.getId())) {
                command.findCommand("size").ifPresent(cmd -> {
                    dim[0] = cmd.getOptionalPar(0, -1);
                    dim[1] = cmd.getOptionalPar(1, -1);
                });
                command.findCommand("sidesize").ifPresent(cmd -> {
                    sidesize[0] = cmd.getOptionalPar(0, 0);
                    sidesize[1] = cmd.getOptionalPar(1, 0);
                });
                command.commands()
                        .filter(cmd -> "event".equals(cmd.getId()))
                        .forEach(cmd -> {
                            String channel = cmd.getOptionalPar(0, "");
                            String teCommand = cmd.getOptionalPar(1, "");
                            event(channel, (source, params) ->
                                    ((GenericGuiContainer<?,?>) gui).sendServerCommand(wrapper, teCommand, params));
                        });
                command.findCommand("panel").ifPresent(cmd -> {
                    toplevel = new Panel(Minecraft.getInstance(), gui);
                    toplevel.readFromGuiCommand(cmd);
                });
                command.commands()
                        .filter(cmd -> "bind".equals(cmd.getId()))
                        .forEach(cmd -> {
                            String component = cmd.getOptionalPar(0, "");
                            String value = cmd.getOptionalPar(1, "");
                            bind(wrapper, component, tileEntity, value);
                        });
                command.commands()
                        .filter(cmd -> "action".equals(cmd.getId()))
                        .forEach(cmd -> {
                            String component = cmd.getOptionalPar(0, "");
                            String key = cmd.getOptionalPar(1, "");
                            action(wrapper, component, tileEntity, key);
                        });
            }
        });

        if (dim[0] != -1 || dim[1] != -1) {
            if (gui instanceof GenericGuiContainer) {
                ((GenericGuiContainer<?,?>) gui).setWindowDimensions(dim[0], dim[1]);
            }
            int guiLeft = (gui.width - dim[0]) / 2;
            int guiTop = (gui.height - dim[1]) / 2;
            toplevel.setBounds(new Rectangle(guiLeft-sidesize[0], guiTop-sidesize[1], dim[0]+sidesize[0], dim[1]+sidesize[1]));
        }
        Minecraft.getInstance().keyboardListener.enableRepeatEvents(true);
    }

    public <T extends Widget<T>> T findChild(String name) {
        Widget<?> widget = ((AbstractContainerWidget<?>) toplevel).findChildRecursive(name);
        if (widget == null) {
            Logging.logError("Could not find widget '" + name + "'!");
        }
        return (T)widget;
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public boolean isWidgetOnWindow(Widget<?> w) {
        return toplevel.containsWidget(w);
    }

    public Widget<?> getToplevel() {
        return toplevel;
    }

    public void setFlag(String flag) {
        if (flag.startsWith("!")) {
            // Remove the positive flag
            activeFlags.remove(StringRegister.STRINGS.get(flag.substring(1)));
        } else {
            // Remove the negative flag
            activeFlags.remove(StringRegister.STRINGS.get("!" + flag));
        }
        activeFlags.add(StringRegister.STRINGS.get(flag));
        enableDisableWidgets(toplevel);
    }

    public void clearFlag(String flag) {
        activeFlags.remove(StringRegister.STRINGS.get(flag));
        activeFlags.add(StringRegister.STRINGS.get("!" + flag));
        enableDisableWidgets(toplevel);
    }

    public void setFlag(String flag, boolean v) {
        if (v) {
            activeFlags.remove(StringRegister.STRINGS.get("!" + flag));
            activeFlags.add(StringRegister.STRINGS.get(flag));
        } else {
            activeFlags.remove(StringRegister.STRINGS.get(flag));
            activeFlags.add(StringRegister.STRINGS.get("!" + flag));
        }
        enableDisableWidgets(toplevel);
    }

    private void enableDisableWidgets(Widget<?> widget) {
        Set<Integer> enabledFlags = widget.getEnabledFlags();
        if (!enabledFlags.isEmpty()) {
            boolean enable = activeFlags.containsAll(enabledFlags);
            widget.setEnabled(enable);
        }
        if (widget instanceof AbstractContainerWidget) {
            for (Widget<?> child : ((AbstractContainerWidget<?>) widget).getChildren()) {
                enableDisableWidgets(child);
            }
        }
    }

    public Widget<?> getWidgetAtPosition(int x, int y) {
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

    public void handleMouseInput(int k) {
//        int k = Mouse.getEventButton();
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

    public void setTextFocus(Widget<?> focus) {
        if (windowManager != null) {
            windowManager.clearFocus();
        }
        setFocus(focus);
    }

    // Package visible for the WindowManager
    void setFocus(Widget<?> focus) {
        if (textFocus != focus) {
            textFocus = focus;
            fireFocusEvents(focus);
        }
    }

    public Widget<?> getTextFocus() {
        return textFocus;
    }

    public boolean charTyped(char codePoint) {
        if (textFocus != null) {
            return textFocus.charTyped(codePoint);
        }
        return false;
    }

    public boolean keyTyped(int keyCode, int scanCode) {
        if (keyCode == GLFW.GLFW_KEY_F12) {
            GuiParser.GuiCommand windowCmd = createWindowCommand();
            GuiParser.GuiCommand command = toplevel.createGuiCommand();
            toplevel.fillGuiCommand(command);
            windowCmd.command(command);
            try {
                try (PrintWriter writer = new PrintWriter(new File("output.gui"))) {
                    windowCmd.write(writer, 0);
                    writer.flush();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (textFocus != null) {
            return textFocus.keyTyped(keyCode, scanCode);
        }
        return false;
    }

    private GuiParser.GuiCommand createWindowCommand() {
        GuiParser.GuiCommand windowCmd = new GuiParser.GuiCommand("window");
        windowCmd.command(new GuiParser.GuiCommand("size")
                .parameter((int) toplevel.getBounds().getWidth())
                .parameter((int) toplevel.getBounds().getHeight()));
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
            dwheel = 0;// @todo 1.14 Mouse.getDWheel();
        } else {
            dwheel = windowManager.getMouseWheel();
            if (dwheel == -1) {
                dwheel = 0; // @todo 1.14 Mouse.getDWheel();
            }
        }
        if (dwheel != 0) {
            toplevel.setWindow(this);
            toplevel.mouseWheel(dwheel, x, y);
        }
        currentStyle = McJtyLib.getPreferencesProperties(gui.getMinecraft().player).map(p -> p.getStyle()).orElse(GuiStyle.STYLE_FLAT_GRADIENT);

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
        return (int) gui.getMinecraft().mouseHelper.getMouseX() * gui.width / gui.getMinecraft().mainWindow.getWidth();
    }

    private int getRelativeY() {
//        return gui.height - (int) gui.getMinecraft().mouseHelper.getMouseY() * gui.height / gui.getMinecraft().mainWindow.getHeight();
        return (int) gui.getMinecraft().mouseHelper.getMouseY() * gui.height / gui.getMinecraft().mainWindow.getHeight();
    }

    public Window addFocusEvent(FocusEvent event) {
        if (focusEvents == null) {
            focusEvents = new ArrayList<>();
        }
        focusEvents.add(event);
        return this;
    }


    private void fireFocusEvents(Widget<?> widget) {
        if (focusEvents != null) {
            for (FocusEvent event : focusEvents) {
                event.focus(this, widget);
            }
        }
    }

    public Window event(String channel, ChannelEvent event) {
        if (!channelEvents.containsKey(channel)) {
            channelEvents.put(channel, new ArrayList<>());
        }
        channelEvents.get(channel).add(event);
        return this;
    }

    public <T extends GenericTileEntity> Window action(SimpleChannel network, String componentName, T te, String keyName) {
        for (IAction action : te.getActions()) {
            if (keyName.equals(action.getKey())) {
                initializeAction(network, componentName, action);
                return this;
            }
        }

        Logging.message(Minecraft.getInstance().player, "Could not find action '" + keyName + "' in supplied TE!");
        return this;
    }

    private void initializeAction(SimpleChannel network, String componentName, IAction action) {
        event(componentName, (source, params) -> sendAction(network, action));
    }

    public <T extends GenericTileEntity> void sendAction(SimpleChannel network, T te, String actionKey) {
        for (IAction action : te.getActions()) {
            if (actionKey.equals(action.getKey())) {
                sendAction(network, action);
                return;
            }
        }

        Logging.message(Minecraft.getInstance().player, "Could not find action '" + actionKey + "' in supplied TE!");

    }

    private void sendAction(SimpleChannel network, IAction action) {
        ((GenericGuiContainer<?,?>)gui).sendServerCommand(network, GenericTileEntity.COMMAND_SYNC_ACTION,
                TypedMap.builder()
                        .put(GenericTileEntity.PARAM_KEY, action.getKey())
                        .build());
    }

    public <T extends GenericTileEntity> Window bind(SimpleChannel network, String componentName, T te, String keyName) {
        for (IValue<?> value : te.getValues()) {
            Key<?> key = value.getKey();
            if (keyName.equals(key.getName())) {
                initializeBinding(network, componentName, value);
                return this;
            }
        }

        Logging.message(Minecraft.getInstance().player, "Could not find value '" + keyName + "' in supplied TE!");
        return this;
    }

    private <V> void initializeBinding(SimpleChannel network, String componentName, IValue<V> value) {
        V v = value.getter().get();
        Widget<?> component = findChild(componentName);

        if (component == null) {
            Logging.message(Minecraft.getInstance().player, "Could not find component '" + componentName + "'!");
            return;
        }
        component.setGenericValue(v);

        event(componentName, (source, params) -> {
            Type<V> type = value.getKey().getType();
            ((GenericGuiContainer<?,?>)gui).sendServerCommand(network, GenericTileEntity.COMMAND_SYNC_BINDING,
                    TypedMap.builder()
                            // @todo this conversion can fail!
                            .put(value.getKey(), type.convert(component.getGenericValue(type)))
                            .build());

        });
    }

    public void fireChannelEvents(String channel, Widget<?> widget, @Nonnull TypedMap params) {
        if (channelEvents.containsKey(channel)) {
            for (ChannelEvent event : channelEvents.get(channel)) {
                event.fire(widget, params);
            }
        }
    }
}
