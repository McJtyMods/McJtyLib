package mcjty.lib.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.client.GuiTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class can be used by items that want to show a GUI.
 * It supports the side window, the window system in general as well as tooltips
 */
public class GuiItemScreen extends Screen {
    protected SimpleChannel network;
    protected Window window;
    protected int xSize;
    protected int ySize;
    protected int guiLeft;
    protected int guiTop;

    private GuiSideWindow sideWindow;

    public GuiItemScreen(SimpleChannel network, int xSize, int ySize, ManualEntry manualEntry) {
        super(new StringTextComponent("todo")); // @todo 1.14
        this.network = network;
        this.xSize = xSize;
        this.ySize = ySize;
        sideWindow = new GuiSideWindow(manualEntry.getManual(), manualEntry.getEntry(), manualEntry.getPage());
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

    public void drawWindow(MatrixStack matrixStack) {
        this.renderBackground(matrixStack);
        window.draw(matrixStack);
        sideWindow.getWindow().draw(matrixStack);
        List<String> tooltips = window.getTooltips();
        Minecraft mc = getMinecraft();
        if (tooltips != null) {
            int x = GuiTools.getRelativeX(this);
            int y = GuiTools.getRelativeY(this);
            // @todo check on 1.16
            List<ITextProperties> properties = tooltips.stream().map(StringTextComponent::new).collect(Collectors.toList());
            List<IReorderingProcessor> processors = LanguageMap.getInstance().getVisualOrder(properties);
            renderTooltip(matrixStack, processors, x-guiLeft, y-guiTop);
        }
        tooltips = sideWindow.getWindow().getTooltips();
        if (tooltips != null) {
            int x = GuiTools.getRelativeX(this);
            int y = GuiTools.getRelativeY(this);
            // @todo check on 1.16
            List<ITextProperties> properties = tooltips.stream().map(StringTextComponent::new).collect(Collectors.toList());
            List<IReorderingProcessor> processors = LanguageMap.getInstance().getVisualOrder(properties);
            renderTooltip(matrixStack, processors, x - guiLeft, y - guiTop);
        }
    }

}
