package mcjty.lib.gui;

import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.icons.IconManager;
import mcjty.lib.gui.widgets.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Use a window manager if you need multipole windows and/or want icon support
 */
public class WindowManager {

    private IconManager iconManager = new IconManager(this);
    private final GuiScreen gui;

    private List<Window> windows = new ArrayList<>();

    public WindowManager(GuiScreen gui) {
        this.gui = gui;
    }

    public GuiScreen getGui() {
        return gui;
    }

    public IconManager getIconManager() {
        return iconManager;
    }

    public WindowManager addWindow(Window w) {
        windows.add(w);
        w.setWindowManager(this);
        return this;
    }

    public void draw() {
        windows.stream().forEach(w -> w.draw());
        iconManager.draw(Minecraft.getMinecraft(), gui);
    }

    public void drawTooltips() {
        int x = Mouse.getEventX() * gui.width / gui.mc.displayWidth;
        int y = gui.height - Mouse.getEventY() * gui.height / gui.mc.displayHeight - 1;

        windows.stream().forEach(w -> {
            List<String> tooltips = w.getTooltips();
            if (tooltips != null) {
                GenericGuiContainer gui = (GenericGuiContainer) this.gui;
                gui.drawHoveringText(tooltips, w.getTooltipItems(), x - gui.guiLeft, y - gui.guiTop, gui.mc.fontRendererObj);
            }
        });
        net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
    }

    public Optional<Widget> findWidgetAtPosition(int x, int y) {
        return windows.stream()
                .map(w -> w.getWidgetAtPosition(x, y))
                .filter(w -> w != null)
                .findFirst();
    }

    public void mouseClicked(int x, int y, int button) throws IOException {
        if ((!iconManager.isClickHoldToDrag()) && iconManager.isDragging()) {
            if (button == 1) {
                iconManager.cancelDragging();
            } else {
                iconManager.stopDragging(x, y);
            }
            return;
        }
        windows.stream().forEach(w -> w.mouseClicked(x, y, button));
    }

    public void handleMouseInput() throws IOException {
        windows.stream().forEach(Window::handleMouseInput);
    }

    public void mouseReleased(int x, int y, int state) {
        if (iconManager.isClickHoldToDrag() && iconManager.isDragging()) {
            iconManager.stopDragging(x, y);
        }
        windows.stream().forEach(w -> w.mouseMovedOrUp(x, y, state));
    }

    public boolean keyTyped(char typedChar, int keyCode) throws IOException {
        return windows.stream().allMatch(w -> !w.keyTyped(typedChar, keyCode));
    }
}
