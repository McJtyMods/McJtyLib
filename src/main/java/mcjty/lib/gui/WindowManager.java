package mcjty.lib.gui;

import mcjty.lib.gui.icons.IconManager;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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

    public WindowManager addWindow(Window w) {
        windows.add(w);
        return this;
    }

    public Stream<Window> stream() {
        return windows.stream();
    }
}
