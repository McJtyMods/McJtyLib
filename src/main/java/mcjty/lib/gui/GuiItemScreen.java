package mcjty.lib.gui;

import mcjty.lib.client.GuiTools;
import mcjty.lib.tooltips.ClientTooltipLine;
import mcjty.lib.varia.ComponentFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

import java.util.List;

/**
 * This class can be used by items that want to show a GUI.
 * It supports the side window, the window system in general as well as tooltips
 */
public abstract class GuiItemScreen extends Screen {
    protected Window window;
    protected int xSize;
    protected int ySize;
    protected int guiLeft;
    protected int guiTop;
    private final ManualEntry manual;

    private GuiSideWindow sideWindow;

    public GuiItemScreen(int xSize, int ySize, ManualEntry manualEntry) {
        super(ComponentFactory.literal("todo")); // @todo 1.14
        this.xSize = xSize;
        this.ySize = ySize;
        sideWindow = new GuiSideWindow(manualEntry.manual(), manualEntry.entry(), manualEntry.page());
        this.manual = manualEntry;
    }

    public void setWindowDimensions(int x, int y) {
        this.xSize = x;
        this.ySize = y;
        sideWindow = new GuiSideWindow(manual.manual(), manual.entry(), manual.page());
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
        sideWindow.initGui(getMinecraft(), this, guiLeft, guiTop, xSize, ySize);
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        double x = event.x(), y = event.y(); int button = event.button();
        boolean rc = super.mouseClicked(event, doubleClick);
        window.mouseClicked(x, y, button);
        sideWindow.getWindow().mouseClicked(x, y, button);
        return rc;
    }

    @Override
    public boolean mouseDragged(net.minecraft.client.input.MouseButtonEvent event, double scaledX, double scaledY) {
        double x = event.x(), y = event.y(); int button = event.button();
        boolean rc = super.mouseDragged(event, scaledX, scaledY);
        window.mouseDragged(x, y, button);
        sideWindow.getWindow().mouseDragged(x, y, button);
        return rc;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double dx, double dy) {
        boolean rc = super.mouseScrolled(x, y, dx, dy);
        window.mouseScrolled(x, y, dx, dy);
        sideWindow.getWindow().mouseScrolled(x, y, dx, dy);
        return rc;
    }

    @Override
    public boolean mouseReleased(net.minecraft.client.input.MouseButtonEvent event) {
        double x = event.x(), y = event.y(); int state = event.button();
        boolean rc = super.mouseReleased(event);
        window.mouseReleased(x, y, state);
        sideWindow.getWindow().mouseReleased(x, y, state);
        return rc;
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        int keyCode = event.key(), scanCode = event.scancode(), modifiers = event.modifiers();
        boolean rc = super.keyPressed(event);
//        window.keyTyped(typedChar, keyCode);
        window.keyTyped(keyCode, scanCode);
        return rc;
    }

    public void drawWindow(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        this.extractBackground(graphics, mouseX, mouseY, partialTicks);
        window.draw(graphics);
        sideWindow.getWindow().draw(graphics);
        List<String> tooltips = window.getTooltips();
        if (tooltips != null) {
            ClientTooltipLine.queue(graphics, Minecraft.getInstance().font, tooltips, window.getTooltipItems(),
                    GuiTools.getRelativeX(this), GuiTools.getRelativeY(this));
        }
        tooltips = sideWindow.getWindow().getTooltips();
        if (tooltips != null) {
            ClientTooltipLine.queue(graphics, Minecraft.getInstance().font, tooltips, sideWindow.getWindow().getTooltipItems(),
                    GuiTools.getRelativeX(this), GuiTools.getRelativeY(this));
        }
    }

    protected abstract void renderInternal(GuiGraphicsExtractor graphics, int pMouseX, int pMouseY, float pPartialTick);

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.extractRenderState(graphics, pMouseX, pMouseY, pPartialTick);
        renderInternal(graphics, pMouseX, pMouseY, pPartialTick);
    }
}
