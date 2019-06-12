package mcjty.lib.gui;

import mcjty.lib.McJtyLib;
import mcjty.lib.base.ModBase;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.network.PacketSetGuiStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.awt.Rectangle;

public class GuiSideWindow {
    protected GuiStyle style;

    protected Window sideWindow;
    private Button guiButton;
    private Button helpButton;
    private int sideLeft;
    private int sideTop;

    private int manual;
    private String manualNode;

    public GuiSideWindow(int manual, String manualNode) {
        this.manual = manual;
        this.manualNode = manualNode;
    }

    public void initGui(final ModBase modBase, final SimpleChannel network, final Minecraft mc, Screen gui, int guiLeft, int guiTop, int xSize, int ySize) {
        style = McJtyLib.getPreferencesProperties(mc.player).map(p -> p.getStyle()).orElse(GuiStyle.STYLE_FLAT_GRADIENT);

        helpButton = new Button(mc, gui).setText("?").setLayoutHint(new PositionalLayout.PositionalHint(1, 1, 16, 16)).
                setTooltips("Open manual").
                addButtonEvent(parent -> help(modBase, mc));
        guiButton = new Button(mc, gui).setText("s").setLayoutHint(new PositionalLayout.PositionalHint(1, 19, 16, 16)).
                addButtonEvent(parent -> changeStyle(McJtyLib.networkHandler));
        setStyleTooltip();
        Panel sidePanel = new Panel(mc, gui).setLayout(new PositionalLayout()).addChild(guiButton).addChild(helpButton);
        sideLeft = guiLeft + xSize;
        sideTop = guiTop + (ySize - 20) / 2 - 8;
        sidePanel.setBounds(new Rectangle(sideLeft, sideTop, 20, 40));
        sideWindow = new Window(gui, sidePanel);
    }

    private void help(ModBase modBase, Minecraft mc) {
        PlayerEntity player = mc.player;
        modBase.openManual(player, manual, manualNode);
    }

    private void setStyleTooltip() {
        guiButton.setTooltips("Gui style:", style.getStyle());
    }

    private void changeStyle(SimpleChannel network) {
        int next = style.ordinal() + 1;
        if (next >= GuiStyle.values().length) {
            next = 0;
        }
        style = GuiStyle.values()[next];
        network.sendToServer(new PacketSetGuiStyle(style.getStyle()));

        setStyleTooltip();
    }

    public Window getWindow() {
        return sideWindow;
    }
}
