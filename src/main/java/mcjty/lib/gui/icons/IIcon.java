package mcjty.lib.gui.icons;

import mcjty.lib.gui.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public interface IIcon {
    /**
     * Draw this icon on the GUI at the specific position
     */
    void draw(Minecraft mc, Gui gui, Window window, int x, int y);
}
