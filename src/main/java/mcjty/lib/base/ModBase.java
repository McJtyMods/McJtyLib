package mcjty.lib.base;

import net.minecraft.entity.player.EntityPlayer;

public interface ModBase {
    String getModId();

    /**
     * Open the manual at a specific page.
     * @param player
     * @param bookindex
     * @param page
     */
    void openManual(EntityPlayer player, int bookindex, String page);
}
