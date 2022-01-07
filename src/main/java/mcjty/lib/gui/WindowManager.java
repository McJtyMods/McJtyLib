package mcjty.lib.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.gui.icons.IconManager;
import mcjty.lib.gui.widgets.AbstractContainerWidget;
import mcjty.lib.gui.widgets.Widget;
import mcjty.lib.client.GuiTools;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Use a window manager if you need multipole windows and/or want icon support
 */
public class WindowManager {

    private final IconManager iconManager = new IconManager(this);
    private final Screen gui;

    private final List<Window> windows = new ArrayList<>();
    private final List<Window> modalWindows = new ArrayList<>();

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

    public Widget<?> getTextFocus() {
        return getInteractableWindows().map(Window::getTextFocus).filter(Objects::nonNull).findFirst().orElse(null);
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

    public <T extends GenericTileEntity> void syncBindings(T te) {
        windows.forEach(window -> window.syncBindings(te));
        modalWindows.forEach(window -> window.syncBindings(te));
    }

    public void draw(PoseStack matrixStack) {
        mouseWheel = 0;// @todo 1.14 Mouse.getDWheel();
        windows.forEach(window -> window.draw(matrixStack));
        modalWindows.forEach(window -> window.draw(matrixStack));
        iconManager.draw(gui, matrixStack);
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

    public List<Window> getWindows() {
        return windows;
    }

    public Stream<Window> getModalWindows() {
        return modalWindows.stream();
    }

    public void drawTooltips(PoseStack matrixStack) {
        int x = GuiTools.getRelativeX(gui);
        int y = GuiTools.getRelativeY(gui);

        getInteractableWindows().forEach(w -> {
            List<String> tooltips = w.getTooltips();
            if (tooltips != null) {
                GenericGuiContainer<?,?> gui = (GenericGuiContainer<?,?>) this.gui;
                gui.drawHoveringText(matrixStack, tooltips, w.getTooltipItems(), x - gui.getGuiLeft(), y - gui.getGuiTop(), gui.getMinecraft().font);
            }
        });
        com.mojang.blaze3d.platform.Lighting.setupForFlatItems();
    }

    public Optional<Widget<?>> findWidgetAtPosition(double x, double y) {
        Stream<Widget<?>> s = windows.stream().map(w -> w.getWidgetAtPosition(x, y));
        return s.filter(Objects::nonNull).findFirst();
    }

    public void mouseClicked(double x, double y, int button) {
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

    public void mouseDragged(double x, double y, int button) {
        getInteractableWindows().forEach(w -> w.mouseDragged(x, y, button));
    }

    public void mouseScrolled(double x, double y, double amount) {
        getInteractableWindows().forEach(w -> w.mouseScrolled(x, y, amount));
    }

    public void mouseReleased(double x, double y, int state) {
        if (iconManager.isClickHoldToDrag() && iconManager.isDragging()) {
            iconManager.stopDragging(x, y);
        }
        getInteractableWindows().forEach(w -> w.mouseReleased(x, y, state));
    }

    public boolean keyTyped(int keyCode, int scanCode) {
        return getInteractableWindows().noneMatch(w -> w.keyTyped(keyCode, scanCode));
    }

    public boolean charTyped(char codePoint) {
        return getInteractableWindows().noneMatch(w -> w.charTyped(codePoint));
    }
}
