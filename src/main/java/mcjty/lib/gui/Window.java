package mcjty.lib.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.McJtyLib;
import mcjty.lib.bindings.IValue;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.IRunnable;
import mcjty.lib.gui.events.ChannelEvent;
import mcjty.lib.gui.events.FocusEvent;
import mcjty.lib.gui.widgets.AbstractContainerWidget;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.Widget;
import mcjty.lib.network.PacketServerCommandTyped;
import mcjty.lib.preferences.PreferencesProperties;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.StringRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
                            String channel = cmd.getOptionalPar(0, "");
                            String teCommand = cmd.getOptionalPar(1, "");
                            event(channel, (source, params) ->
                                    ((GenericGuiContainer<?,?>) gui).sendServerCommandTyped(wrapper, teCommand, params));
                        });
                command.findCommand("panel").ifPresent(cmd -> {
                    toplevel = new Panel();
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
            toplevel.bounds(guiLeft-sidesize[0], guiTop-sidesize[1], dim[0]+sidesize[0], dim[1]+sidesize[1]);
        }
        Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(true);
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

    public void mouseScrolled(double x, double y, double amount) {
        toplevel.setWindow(this);
        toplevel.mouseScrolled(x, y, amount);
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

    public void draw(MatrixStack matrixStack) {
        int x = getRelativeX();
        int y = getRelativeY();

        if (hover != null) {
            hover.hovering(false);
        }
        hover = toplevel.getWidgetAtPosition(x, y);
        if (hover != null) {
            hover.hovering(true);
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
            toplevel.mouseScrolled(x, y, dwheel);
        }
        currentStyle = McJtyLib.getPreferencesProperties(gui.getMinecraft().player).map(PreferencesProperties::getStyle).orElse(GuiStyle.STYLE_FLAT_GRADIENT);

        toplevel.setWindow(this);
        toplevel.draw(gui, matrixStack, 0, 0);
        toplevel.drawPhase2(gui, matrixStack, 0, 0);
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
            List<ItemStack> tooltips = w.getTooltipItems();
            if (tooltips != null) {
                return tooltips;
            }
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
    public <T extends GenericTileEntity> Window action(SimpleChannel network, String componentName, T te, Command<?> command) {
        return action(network, componentName, te, command.getName());
    }

    public <T extends GenericTileEntity> Window action(SimpleChannel network, String componentName, T te, String keyName) {
        IRunnable<?> serverCommand = te.findServerCommand(keyName);
        if (serverCommand != null) {
            initializeAction(network, componentName, keyName, te);
            return this;
        }

        Logging.message(Minecraft.getInstance().player, "Could not find action '" + keyName + "' in supplied TE!");
        return this;
    }

    private <T extends GenericTileEntity> void initializeAction(SimpleChannel network, String componentName, String command, T te) {
        event(componentName, (source, params) -> {
            network.sendToServer(new PacketServerCommandTyped(te.getBlockPos(), te.getDimension(), command, params));
        });
    }

    public void sendServerCommand(SimpleChannel network, Command<?> action, @Nonnull TypedMap params) {
        GenericGuiContainer<?, ?> guiContainer = (GenericGuiContainer<?, ?>) this.gui;
        guiContainer.sendServerCommandTyped(network, action, params);
    }

    public <T extends GenericTileEntity> Window bind(SimpleChannel network, String componentName, T te, String keyName) {
        Map<String, IValue<?>> valueMap = te.getValueMap();
        if (valueMap.containsKey(keyName)) {
            IValue<?> value = valueMap.get(keyName);
            Key<?> key = value.getKey();
            initializeBinding(network, te.getDimension(), componentName, value);
            return this;
        }

        Logging.message(Minecraft.getInstance().player, "Could not find value '" + keyName + "' in supplied TE!");
        return this;
    }

    private <V> void initializeBinding(SimpleChannel network, @Nonnull RegistryKey<World> dimensionType, String componentName, IValue<V> value) {
        V v = value.getter().get();
        Widget<?> component = findChild(componentName);

        if (component == null) {
            Logging.message(Minecraft.getInstance().player, "Could not find component '" + componentName + "'!");
            return;
        }
        component.setGenericValue(v);

        event(componentName, (source, params) -> {
            Type<V> type = value.getKey().getType();
            GenericGuiContainer<?, ?> guiContainer = (GenericGuiContainer<?, ?>) this.gui;
            guiContainer.sendServerCommandTyped(network, dimensionType, guiContainer.tileEntity.COMMAND_SYNC_BINDING.getName(),
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
