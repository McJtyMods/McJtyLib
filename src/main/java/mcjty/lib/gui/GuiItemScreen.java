package mcjty.lib.gui;

import mcjty.lib.client.GuiTools;
import mcjty.lib.varia.ComponentFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.stream.Collectors;

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
    public boolean mouseClicked(double x, double y, int button) {
        boolean rc = super.mouseClicked(x, y, button);
        window.mouseClicked(x, y, button);
        sideWindow.getWindow().mouseClicked(x, y, button);
        return rc;
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double scaledX, double scaledY) {
        boolean rc = super.mouseDragged(x, y, button, scaledX, scaledY);
        window.mouseDragged(x, y, button);
        sideWindow.getWindow().mouseDragged(x, y, button);
        return rc;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double amount) {
        boolean rc = super.mouseScrolled(x, y, amount);
        window.mouseScrolled(x, y, amount);
        sideWindow.getWindow().mouseScrolled(x, y, amount);
        return rc;
    }

    @Override
    public boolean mouseReleased(double x, double y, int state) {
        boolean rc = super.mouseReleased(x, y, state);
        window.mouseReleased(x, y, state);
        sideWindow.getWindow().mouseReleased(x, y, state);
        return rc;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean rc = super.keyPressed(keyCode, scanCode, modifiers);
//        window.keyTyped(typedChar, keyCode);
        window.keyTyped(keyCode, scanCode);
        return rc;
    }

    public void drawWindow(GuiGraphics graphics) {
        this.renderBackground(graphics);
        window.draw(graphics);
        sideWindow.getWindow().draw(graphics);
        List<String> tooltips = window.getTooltips();
        if (tooltips != null) {
            int x = GuiTools.getRelativeX(this);
            int y = GuiTools.getRelativeY(this);
            // @todo check on 1.16
            List<FormattedText> properties = tooltips.stream().map(ComponentFactory::literal).collect(Collectors.toList());
            List<FormattedCharSequence> processors = Language.getInstance().getVisualOrder(properties);
            graphics.renderTooltip(Minecraft.getInstance().font, processors, x-guiLeft, y-guiTop);
        }
        tooltips = sideWindow.getWindow().getTooltips();
        if (tooltips != null) {
            int x = GuiTools.getRelativeX(this);
            int y = GuiTools.getRelativeY(this);
            // @todo check on 1.16
            List<FormattedText> properties = tooltips.stream().map(ComponentFactory::literal).collect(Collectors.toList());
            List<FormattedCharSequence> processors = Language.getInstance().getVisualOrder(properties);
            graphics.renderTooltip(Minecraft.getInstance().font, processors, x - guiLeft, y - guiTop);
        }
    }

    protected abstract void renderInternal(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick);

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
        renderInternal(graphics, pMouseX, pMouseY, pPartialTick);
    }
}
