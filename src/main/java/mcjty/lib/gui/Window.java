package mcjty.lib.gui;

import mcjty.lib.McJtyLib;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.IRunnable;
import mcjty.lib.gui.events.ChannelEvent;
import mcjty.lib.gui.events.FocusEvent;
import mcjty.lib.gui.widgets.AbstractContainerWidget;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.Widget;
import mcjty.lib.gui.widgets.Widgets;
import mcjty.lib.network.Networking;
import mcjty.lib.network.PacketSendServerCommand;
import mcjty.lib.network.PacketServerCommandTyped;
import mcjty.lib.preferences.PreferencesProperties;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.tileentity.ValueHolder;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.StringRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.Function;

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
    private final Map<Widget, Function> bindings = new HashMap<>();
    private final Set<Integer> activeFlags = new HashSet<>();

    private List<FocusEvent> focusEvents = null;

    public Screen getGui() {
        return gui;
    }

    public Window(Screen gui, AbstractContainerWidget<?> toplevel) {
        this.gui = gui;
        this.toplevel = toplevel;
    }

    public Window(Screen gui, ResourceLocation guiDescription) {
        this.gui = gui;
        final int[] dim = {-1, -1};
        final int[] sidesize = {0, 0};
        parseInternal(null, null, guiDescription, dim, sidesize);

        if (dim[0] != -1 || dim[1] != -1) {
            if (gui instanceof GuiItemScreen container) {
                container.setWindowDimensions(dim[0], dim[1]);
            }
            int guiLeft = (gui.width - dim[0]) / 2;
            int guiTop = (gui.height - dim[1]) / 2;
            toplevel.bounds(guiLeft-sidesize[0], guiTop-sidesize[1], dim[0]+sidesize[0], dim[1]+sidesize[1]);
        }
    }

    public Window(Screen gui, GenericTileEntity tileEntity, ResourceLocation guiDescription) {
        this.gui = gui;
        final int[] dim = {-1, -1};
        final int[] sidesize = {0, 0};
        parseInternal((GenericGuiContainer<?, ?>) gui, tileEntity, guiDescription, dim, sidesize);

        if (dim[0] != -1 || dim[1] != -1) {
            if (gui instanceof GenericGuiContainer container) {
                container.setWindowDimensions(dim[0], dim[1]);
            }
            int guiLeft = (gui.width - dim[0]) / 2;
            int guiTop = (gui.height - dim[1]) / 2;
            toplevel.bounds(guiLeft-sidesize[0], guiTop-sidesize[1], dim[0]+sidesize[0], dim[1]+sidesize[1]);
        }
        // @todo 1.19.3
//        Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(true);
    }

    private void parseInternal(@Nullable GenericGuiContainer<?, ?> gui, @Nullable GenericTileEntity tileEntity, ResourceLocation guiDescription, int[] dim, int[] sidesize) {
        try {
            WindowTools.parseAndHandleClient(guiDescription, command -> {
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
                                if (gui != null) {
                                    String channel = cmd.getOptionalPar(0, "");
                                    String teCommand = cmd.getOptionalPar(1, "");
                                    event(channel, (source, params) ->
                                            gui.sendServerCommandTyped(teCommand, params));
                                }
                            });
                    command.commands()
                            .filter(cmd -> "cmdevent".equals(cmd.getId()))
                            .forEach(cmd -> {
                                String channel = cmd.getOptionalPar(0, "");
                                String modidCmd = cmd.getOptionalPar(1, "");
                                ResourceLocation rl = new ResourceLocation(modidCmd);
                                event(channel, (source, params) ->
                                        Networking.sendToServer(PacketSendServerCommand.create(rl.getNamespace(), rl.getPath(), TypedMap.EMPTY)));
                            });
                    command.findCommand("panel").ifPresent(cmd -> {
                        toplevel = new Panel();
                        toplevel.readFromGuiCommand(cmd);
                    });
                    command.commands()
                            .filter(cmd -> "bind".equals(cmd.getId()))
                            .forEach(cmd -> {
                                if (tileEntity != null) {
                                    String component = cmd.getOptionalPar(0, "");
                                    String value = cmd.getOptionalPar(1, "");
                                    bind(component, tileEntity, value);
                                }
                            });
                    command.commands()
                            .filter(cmd -> "action".equals(cmd.getId()))
                            .forEach(cmd -> {
                                if (tileEntity != null) {
                                    String component = cmd.getOptionalPar(0, "");
                                    String key = cmd.getOptionalPar(1, "");
                                    action(component, tileEntity, key);
                                }
                            });
                }
            });
        } catch (Exception e) {
            String message = e.getMessage();
            if (message.length() > 70) {
                int i = message.lastIndexOf(':');
                if (i == -1) {
                    message = message.substring(message.length() - 70);
                } else {
                    message = message.substring(i+1);
                }
            }
            toplevel = Widgets.positional()
                    .desiredWidth(400).desiredHeight(50)
                    .filledBackground(0xffffffff)
                    .filledRectThickness(2)
                    .children(Widgets.label(message)
                            .hint(5, 5, 390, 40));
            dim[0] = 400;
            dim[1] = 50;
            e.printStackTrace();
        }
    }

    public <T extends Widget<T>> T findChild(String name) {
        Widget<?> widget = toplevel.findChildRecursive(name);
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
            widget.enabled(enable);
        }
        if (widget instanceof AbstractContainerWidget) {
            for (Widget<?> child : ((AbstractContainerWidget<?>) widget).getChildren()) {
                enableDisableWidgets(child);
            }
        }
    }

    public Widget<?> getWidgetAtPosition(double x, double y) {
        if (toplevel.in(x, y) && toplevel.isVisible()) {
            return toplevel.getWidgetAtPosition(x, y);
        } else {
            return null;
        }
    }

    public void mouseClicked(double x, double y, int button) {
        if (textFocus != null) {
            textFocus = null;
            fireFocusEvents(null);
        }
        if (toplevel.in(x, y) && toplevel.isVisible()) {
            toplevel.setWindow(this);
            toplevel.mouseClick(x, y, button);
        }
    }

    public void mouseDragged(double x, double y, int button) {
        toplevel.setWindow(this);
        toplevel.mouseMove(x, y);
    }

    public void mouseScrolled(double x, double y, double dx, double dy) {
        toplevel.setWindow(this);
        toplevel.mouseScrolled(x, y, dx, dy);
    }

    public void mouseReleased(double x, double y, int button) {
        toplevel.setWindow(this);
        toplevel.mouseRelease(x, y, button);
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
                Logging.logError("Problem writing output.gui!", e);
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

    public <T extends GenericTileEntity> void syncBindings(T te) {
        bindings.entrySet().forEach(entry -> entry.getKey().setGenericValue(entry.getValue().apply(te)));
    }

    public void draw(GuiGraphics graphics) {
        int x = getRelativeX();
        int y = getRelativeY();

        if (hover != null) {
            hover.hovering(false);
        }
        hover = toplevel.getWidgetAtPosition(x, y);
        if (hover != null) {
            hover.hovering(true);
        }

        int dwheelX, dwheelY;
        if (windowManager == null) {
            dwheelX = 0;// @todo 1.14 Mouse.getDWheel();
            dwheelY = 0;// @todo 1.14 Mouse.getDWheel();
        } else {
            dwheelX = windowManager.getMouseWheelX();
            if (dwheelX == -1) {
                dwheelX = 0; // @todo 1.14 Mouse.getDWheel();
            }
            dwheelY = windowManager.getMouseWheelY();
            if (dwheelY == -1) {
                dwheelY = 0; // @todo 1.14 Mouse.getDWheel();
            }
        }
        if (dwheelX != 0 || dwheelY != 0) {
            toplevel.setWindow(this);
            toplevel.mouseScrolled(x, y, dwheelX, dwheelY);
        }
        PreferencesProperties preferencesProperties = McJtyLib.getPreferencesProperties(gui.getMinecraft().player);
        currentStyle = preferencesProperties != null ? preferencesProperties.getStyle() : GuiStyle.STYLE_FLAT_GRADIENT;

        toplevel.setWindow(this);
        toplevel.draw(gui, graphics, 0, 0);
        toplevel.drawPhase2(gui, graphics, 0, 0);
    }

    public GuiStyle getCurrentStyle() {
        return currentStyle;
    }

    @Nullable
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

    @Nullable
    public List<ItemStack> getTooltipItems() {
        int x = getRelativeX();
        int y = getRelativeY();
        if (toplevel.in(x, y) && toplevel.isVisible()) {
            Widget<?> w = toplevel.getWidgetAtPosition(x, y);
            return w.getTooltipItems();
        }
        return null;
    }

    private int getRelativeX() {
        int windowWidth = gui.getMinecraft().getWindow().getScreenWidth();
        if (windowWidth == 0) {
            return 0;
        } else {
            return (int) gui.getMinecraft().mouseHandler.xpos() * gui.width / windowWidth;
        }
    }

    private int getRelativeY() {
        int windowHeight = gui.getMinecraft().getWindow().getScreenHeight();
        if (windowHeight == 0) {
            return 0;
        } else {
            return (int) gui.getMinecraft().mouseHandler.ypos() * gui.height / windowHeight;
        }
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
                event.focus(widget);
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
    public <T extends GenericTileEntity> Window action(String componentName, T te, Command<?> command) {
        return action(componentName, te, command.name());
    }

    public <T extends GenericTileEntity> Window action(String componentName, T te, String keyName) {
        IRunnable<?> serverCommand = te.findServerCommand(keyName);
        if (serverCommand != null) {
            initializeAction(componentName, keyName, te);
            return this;
        }

        Logging.message(Minecraft.getInstance().player, "Could not find action '" + keyName + "' in supplied TE!");
        return this;
    }

    private <T extends GenericTileEntity> void initializeAction(String componentName, String command, T te) {
        event(componentName, (source, params) -> {
            Networking.sendToServer(PacketServerCommandTyped.create(te.getBlockPos(), te.getDimension(), command, params));
        });
    }

    public void sendServerCommand(Command<?> action, @Nonnull TypedMap params) {
        GenericGuiContainer<?, ?> guiContainer = (GenericGuiContainer<?, ?>) this.gui;
        guiContainer.sendServerCommandTyped(action, params);
    }

    public <T extends GenericTileEntity> Window bind(String componentName, T te, String keyName) {
        Map<String, ValueHolder<?, ?>> valueMap = te.getValueMap();
        if (valueMap.containsKey(keyName)) {
            ValueHolder<?, ?> value = valueMap.get(keyName);
            initializeBinding(te.getDimension(), componentName, te, value);
            return this;
        }

        Logging.message(Minecraft.getInstance().player, "Could not find value '" + keyName + "' in supplied TE!");
        return this;
    }

    private <T extends GenericTileEntity, V> void initializeBinding(@Nonnull ResourceKey<Level> dimensionType, String componentName,
                                                                    T te, ValueHolder value) {
        V v = (V) value.getter().apply(te);
        Widget<?> component = findChild(componentName);

        if (component == null) {
            Logging.message(Minecraft.getInstance().player, "Could not find component '" + componentName + "'!");
            return;
        }
        component.setGenericValue(v);
        bindings.put(component, value.getter());

        event(componentName, (source, params) -> {
            Type<V> type = value.key().type();
            // @todo this conversion can fail!
            V converted = type.convert(component.getGenericValue(type));

            // Set client-side
            value.setter().accept(te, converted);

            GenericGuiContainer<?, ?> guiContainer = (GenericGuiContainer<?, ?>) this.gui;
            guiContainer.sendServerCommandTyped(dimensionType, GenericTileEntity.COMMAND_SYNC_BINDING.name(),
                    TypedMap.builder()
                            .put(value.key(), converted)
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
