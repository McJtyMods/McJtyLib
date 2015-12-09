package mcjty.lib.gui;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import mcjty.lib.base.ModBase;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;

public class GuiItemScreen extends GuiScreen {
    protected ModBase modBase;
    protected SimpleNetworkWrapper network;
    protected Window window;
    protected int xSize;
    protected int ySize;
    protected int guiLeft;
    protected int guiTop;

    private GuiSideWindow sideWindow;

    public GuiItemScreen(ModBase mod, SimpleNetworkWrapper network, int xSize, int ySize, int manual, String manualNode) {
        this.modBase = mod;
        this.network = network;
        this.xSize = xSize;
        this.ySize = ySize;
        sideWindow = new GuiSideWindow(manual, manualNode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        super.initGui();
        guiLeft = (this.width - xSize) / 2;
        guiTop = (this.height - ySize) / 2;
        sideWindow.initGui(modBase, network, mc, this, guiLeft, guiTop, xSize, ySize);
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);
        window.mouseClicked(x, y, button);
        sideWindow.getWindow().mouseClicked(x, y, button);
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
        window.draw();
        sideWindow.getWindow().draw();
        List<String> tooltips = window.getTooltips();
        if (tooltips != null) {
            int x = Mouse.getEventX() * width / mc.displayWidth;
            int y = height - Mouse.getEventY() * height / mc.displayHeight - 1;
            drawHoveringText(tooltips, x-guiLeft, y-guiTop, mc.fontRendererObj);
        }
        tooltips = sideWindow.getWindow().getTooltips();
        if (tooltips != null) {
            int x = Mouse.getEventX() * width / mc.displayWidth;
            int y = height - Mouse.getEventY() * height / mc.displayHeight - 1;
            drawHoveringText(tooltips, x - guiLeft, y - guiTop, mc.fontRendererObj);
        }
    }

}
