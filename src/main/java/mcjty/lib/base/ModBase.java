package mcjty.lib.base;

public interface ModBase {

    String getModId();

    /**
     * If your mod is registered with McJtyLib.registerMod() then this will be called after
     * McJtyLib finds that top is present. You can in your own TOP handler then get the TOP
     * api by getting TOPCompatibility.GetTheOneProbe.probe.
     *
     */
    default void handleTopExtras() {}

    /**
     * If your mod is registered with McJtyLib.registerMod() then this will be called after
     * McJtyLib finds that WAILA or HWYLA is present. You can get the wail registrar
     * at WailaCompatibility.registrar.
     */
    default void handleWailaExtras() {}

}
