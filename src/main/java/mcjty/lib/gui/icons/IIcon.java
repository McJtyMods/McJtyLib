package mcjty.lib.gui.icons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public interface IIcon {
    /**
     * Draw this icon on the GUI at the specific position
     */
    void draw(Minecraft mc, Gui gui, int x, int y);
}
