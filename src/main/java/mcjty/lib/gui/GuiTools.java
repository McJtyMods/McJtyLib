package mcjty.lib.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.gui.screen.Screen;

public class GuiTools {

    public static int getRelativeX(Screen gui) {
        Minecraft mc = Minecraft.getInstance();
        int mainWidth = mc.getMainWindow().getWidth();
        if (mainWidth <= 0) {
            // Safety
            return 0;
        }

        MouseHelper mouse = mc.mouseHelper;
        int mouseX = (int)(mouse.getMouseX());
        return mouseX * gui.width / mainWidth;
    }

    public static int getRelativeY(Screen gui) {
        Minecraft mc = Minecraft.getInstance();
        int mainHeight = mc.getMainWindow().getHeight();
        if (mainHeight <= 0) {
            // Safety
            return 0;
        }

        MouseHelper mouse = mc.mouseHelper;
        int mouseY = (int)(mouse.getMouseY());
        return mouseY * gui.height / mainHeight;
    }
}
