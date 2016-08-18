package mcjty.lib.gui.icons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public interface IIcon {

    /**
     * Draw this icon on the GUI at the specific position
     */
    void draw(Minecraft mc, Gui gui, int x, int y);

    // Make a copy of this icon
    IIcon clone();

    // Get a unique identifier for this icon
    String getID();
}
