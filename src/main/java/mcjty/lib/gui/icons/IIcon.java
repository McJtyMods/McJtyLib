package mcjty.lib.gui.icons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.Map;

public interface IIcon {

    /**
     * Draw this icon on the GUI at the specific position
     */
    void draw(Minecraft mc, Gui gui, int x, int y);

    void addOverlay(IIcon icon);

    void removeOverlay(String id);

    void clearOverlays();

    boolean hasOverlay(String id);

    void addData(String name, Object data);

    void removeData(String name);

    void clearData();

    Map<String, Object> getData();

    // Make a copy of this icon
    IIcon clone();

    // Get a unique identifier for this icon
    String getID();
}
