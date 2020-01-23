package mcjty.lib.gui;

import mcjty.lib.gui.icons.IconManager;
import mcjty.lib.gui.widgets.AbstractContainerWidget;
import mcjty.lib.gui.widgets.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.gui.screen.Screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Use a window manager if you need multipole windows and/or want icon support
 */
public class WindowManager {

    private IconManager iconManager = new IconManager(this);
    private final Screen gui;

    private List<Window> windows = new ArrayList<>();
    private List<Window> modalWindows = new ArrayList<>();

    // If -1 it is not this window manager that manages the mousewheel but
    // the window itself
    private int mouseWheel = -1;

    public WindowManager(Screen gui) {
        this.gui = gui;
    }

    public Screen getGui() {
        return gui;
    }

    public IconManager getIconManager() {
        return iconManager;
    }

    public void clearFocus() {
        Stream.concat(windows.stream(), modalWindows.stream()).forEach(w -> w.setFocus(null));
    }

    public void setFocus(Widget<?> w) {
        clearFocus();
        Stream.concat(windows.stream(), modalWindows.stream())
                .filter(window -> window.isWidgetOnWindow(w))
                .findFirst()
                .ifPresent(window -> window.setTextFocus(w));
    }

    public WindowManager addWindow(Window w) {
        windows.add(w);
        w.setWindowManager(this);
        return this;
    }

    public WindowManager addModalWindow(Window w) {
        modalWindows.add(w);
        w.setWindowManager(this);
        return this;
    }

    public Window createModalWindow(AbstractContainerWidget<?> topLevel) {
        Window w = new Window(gui, topLevel);
        addModalWindow(w);
        return w;
    }

    public void closeWindow(Window window) {
        if (windows.contains(window)) {
            windows.remove(window);
        } else if (modalWindows.contains(window)) {
            modalWindows.remove(window);
        }
    }

    public int getMouseWheel() {
        return mouseWheel;
    }

    public void draw() {
        mouseWheel = 0;// @todo 1.14 Mouse.getDWheel();
        windows.stream().forEach(w -> w.draw());
        modalWindows.stream().forEach(w -> w.draw());
        iconManager.draw(Minecraft.getInstance(), gui);
    }

    private Stream<Window> getInteractableWindows() {
        if (modalWindows.isEmpty()) {
            return windows.stream();
        } else {
            Window window = modalWindows.get(modalWindows.size() - 1);
            return Stream.<Window> builder()
                    .add(window)
                    .build();
        }
    }

    public Stream<Window> getModalWindows() {
        return modalWindows.stream();
    }

    public void drawTooltips() {
        Minecraft mc = gui.getMinecraft();
        MouseHelper mouse = mc.mouseHelper;

        // @todo check for 1.14
        int mouseX = (int)(mouse.getMouseX());// * (double) mc.getMainWindow().getScaledWidth() / (double) mc.getMainWindow().getWidth());
        int mouseY = (int)(mouse.getMouseY());// * (double) mc.getMainWindow().getScaledHeight() / (double) mc.getMainWindow().getHeight());
        int x = mouseX * gui.width / mc.getMainWindow().getWidth();
        int y = mouseY * gui.height / mc.getMainWindow().getHeight() - 1;

//        int x = (int)mouse.getMouseX() * gui.width / mc.getMainWindow().getWidth();
//        int y = gui.height - (int)mouse.getMouseY() * gui.height / mc.getMainWindow().getHeight() - 1;

        getInteractableWindows().forEach(w -> {
            List<String> tooltips = w.getTooltips();
            if (tooltips != null) {
                GenericGuiContainer<?,?> gui = (GenericGuiContainer<?,?>) this.gui;
                gui.drawHoveringText(tooltips, w.getTooltipItems(), x - gui.getGuiLeft(), y - gui.getGuiTop(), gui.getMinecraft().fontRenderer);
            }
        });
        net.minecraft.client.renderer.RenderHelper.setupGuiFlatDiffuseLighting();
    }

    public Optional<Widget<?>> findWidgetAtPosition(int x, int y) {
        Stream<Widget<?>> s = windows.stream().map(w -> w.getWidgetAtPosition(x, y));
        return s.filter(Objects::nonNull).findFirst();
    }

    public void mouseClicked(int x, int y, int button) {
        if ((!iconManager.isClickHoldToDrag()) && iconManager.isDragging()) {
            if (button == 1) {
                iconManager.cancelDragging();
            } else {
                iconManager.stopDragging(x, y);
            }
            return;
        }
        getInteractableWindows().forEach(w -> w.mouseClicked(x, y, button));
    }

    public void handleMouseInput(int k) {
        getInteractableWindows().forEach(w -> w.handleMouseInput(k));
    }

    public void mouseReleased(int x, int y, int state) {
        if (iconManager.isClickHoldToDrag() && iconManager.isDragging()) {
            iconManager.stopDragging(x, y);
        }
        getInteractableWindows().forEach(w -> w.mouseMovedOrUp(x, y, state));
    }

    public boolean keyTyped(int keyCode, int scanCode) {
        return getInteractableWindows().allMatch(w -> !w.keyTyped(keyCode, scanCode));
    }

    public boolean charTyped(char codePoint) {
        return getInteractableWindows().allMatch(w -> !w.charTyped(codePoint));
    }
}
