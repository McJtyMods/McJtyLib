package mcjty.lib.gui;

import mcjty.lib.base.ModBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

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
        sideWindow.initGui(modBase, getMinecraft(), this, guiLeft, guiTop, xSize, ySize);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        boolean rc = super.mouseClicked(x, y, button);
        window.mouseClicked((int)x, (int)y, button);
        sideWindow.getWindow().mouseClicked((int)x, (int)y, button);
        return rc;
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double scaledX, double scaledY) {
        boolean rc = super.mouseDragged(x, y, button, scaledX, scaledX);
        // @todo 1.14
        window.handleMouseInput(button);
        sideWindow.getWindow().handleMouseInput(button);
        return rc;
    }


    @Override
    public boolean mouseReleased(double x, double y, int state) {
        boolean rc = super.mouseReleased(x, y, state);
        window.mouseMovedOrUp((int)x, (int)y, state);
        sideWindow.getWindow().mouseMovedOrUp((int)x, (int)y, state);
        return rc;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean rc = super.keyPressed(keyCode, scanCode, modifiers);
//        window.keyTyped(typedChar, keyCode);
        window.keyTyped(keyCode, scanCode);
        return rc;
    }

    public void drawWindow() {
        this.renderBackground();
        window.draw();
        sideWindow.getWindow().draw();
        List<String> tooltips = window.getTooltips();
        Minecraft mc = getMinecraft();
        if (tooltips != null) {
            int x = (int) mc.mouseHelper.getMouseX() * width / mc.getMainWindow().getWidth();
            int y = (int) mc.mouseHelper.getMouseY() * height / mc.getMainWindow().getHeight() - 1;
            renderTooltip(tooltips, x-guiLeft, y-guiTop, mc.fontRenderer);
        }
        tooltips = sideWindow.getWindow().getTooltips();
        if (tooltips != null) {
            int x = (int)mc.mouseHelper.getMouseX() * width / mc.getMainWindow().getWidth();
            int y = (int)mc.mouseHelper.getMouseY() * height / mc.getMainWindow().getHeight() - 1;
            renderTooltip(tooltips, x - guiLeft, y - guiTop, mc.fontRenderer);
        }
    }

}
