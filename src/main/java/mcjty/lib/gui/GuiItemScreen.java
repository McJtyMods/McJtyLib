package mcjty.lib.gui;

import mcjty.lib.base.ModBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.io.IOException;
import java.util.List;

public class GuiItemScreen extends Screen {
    protected ModBase modBase;
    protected SimpleChannel network;
    protected Window window;
    protected int xSize;
    protected int ySize;
    protected int guiLeft;
    protected int guiTop;

    private GuiSideWindow sideWindow;

    public GuiItemScreen(ModBase mod, SimpleChannel network, int xSize, int ySize, int manual, String manualNode) {
        super(new StringTextComponent("todo")); // @todo 1.14
        this.modBase = mod;
        this.network = network;
        this.xSize = xSize;
        this.ySize = ySize;
        sideWindow = new GuiSideWindow(manual, manualNode);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void init() {
        super.init();
        guiLeft = (this.width - xSize) / 2;
        guiTop = (this.height - ySize) / 2;
        sideWindow.initGui(modBase, network, getMinecraft(), this, guiLeft, guiTop, xSize, ySize);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        boolean rc = super.mouseClicked(x, y, button);
        window.mouseClicked(x, y, button);
        sideWindow.getWindow().mouseClicked(x, y, button);
        return rc;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        window.handleMouseInput();
        sideWindow.getWindow().handleMouseInput();
    }

    @Override
    protected void mouseReleased(int x, int y, int state) {
        super.mouseReleased(x, y, state);
        window.mouseMovedOrUp(x, y, state);
        sideWindow.getWindow().mouseMovedOrUp(x, y, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        window.keyTyped(typedChar, keyCode);
    }

    public void drawWindow() {
        this.renderBackground();
        window.draw();
        sideWindow.getWindow().draw();
        List<String> tooltips = window.getTooltips();
        Minecraft mc = getMinecraft();
        if (tooltips != null) {
            int x = Mouse.getEventX() * width / mc.displayWidth;
            int y = height - Mouse.getEventY() * height / mc.displayHeight - 1;
            renderTooltip(tooltips, x-guiLeft, y-guiTop, mc.fontRenderer);
        }
        tooltips = sideWindow.getWindow().getTooltips();
        if (tooltips != null) {
            int x = Mouse.getEventX() * width / mc.displayWidth;
            int y = height - Mouse.getEventY() * height / mc.displayHeight - 1;
            renderTooltip(tooltips, x - guiLeft, y - guiTop, mc.fontRenderer);
        }
    }

}
