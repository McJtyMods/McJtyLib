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

    /**
     * If your mod is registered with McJtyLib.registerMod() then this will be called after
     * McJtyLib finds that top is present. You can in your own TOP handler then get the TOP
     * api by getting TOPCompatibility.GetTheOneProbe.probe.
     *
     */
    default void handleTopExtras() {}

    /**
     * If your mod is registered with McJtyLib.registerMod() then this will be called after
     * McJtyLib finds that WAILA or HWYLA is present
     */
    default void handleWailaExtras() {}

}
