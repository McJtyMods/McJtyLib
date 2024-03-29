package mcjty.lib.gui;

import mcjty.lib.McJtyLib;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.network.Networking;
import mcjty.lib.network.PacketOpenManual;
import mcjty.lib.network.PacketSetGuiStyle;
import mcjty.lib.preferences.PreferencesProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

import static mcjty.lib.gui.widgets.Widgets.button;
import static mcjty.lib.gui.widgets.Widgets.positional;

/**
 * This side window manages both the online help button as well as the style button.
 * Gui's that inherit from GenericGuiContainer will automatically have this
 */
public class GuiSideWindow {
    private GuiStyle style;

    private Window sideWindow;
    private Button guiButton;

    private final ResourceLocation manual;
    private final ResourceLocation manualNode;
    private final int page;

    public GuiSideWindow(ResourceLocation manual, ResourceLocation manualNode, int page) {
        this.manual = manual;
        this.manualNode = manualNode;
        this.page = page;
    }

    public void initGui(final Minecraft mc, Screen gui, int guiLeft, int guiTop, int xSize, int ySize) {
        PreferencesProperties properties = McJtyLib.getPreferencesProperties(mc.player);
        style = properties != null ? properties.getStyle() : GuiStyle.STYLE_FLAT_GRADIENT;

        Button helpButton = button(1, 1, 16, 16, "?")
                .tooltips("Open manual")
                .event(() -> help(mc));
        guiButton = button(1, 19, 16, 16, "s")
                .event(() -> changeStyle());
        setStyleTooltip();
        Panel sidePanel = positional().children(guiButton, helpButton);
        int sideLeft = guiLeft + xSize;
        int sideTop = guiTop + (ySize - 20) / 2 - 8;
        sidePanel.bounds(sideLeft, sideTop, 20, 40);
        sideWindow = new Window(gui, sidePanel);
    }

    private void help(Minecraft mc) {
        if (manual != null) {
            Networking.sendToServer(PacketOpenManual.create(manual, manualNode, page));
        }
    }

    private void setStyleTooltip() {
        guiButton.tooltips("Gui style:", style.getStyle());
    }

    private void changeStyle() {
        int next = style.ordinal() + 1;
        if (next >= GuiStyle.values().length) {
            next = 0;
        }
        style = GuiStyle.values()[next];
        Networking.sendToServer(PacketSetGuiStyle.create(style.getStyle()));

        setStyleTooltip();
    }

    public Window getWindow() {
        return sideWindow;
    }
}
