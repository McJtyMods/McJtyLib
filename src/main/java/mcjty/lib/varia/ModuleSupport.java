package mcjty.lib.varia;

import mcjty.lib.api.IModuleSupport;

/**
 * Helper for IModuleSupport
 */
public abstract class ModuleSupport implements IModuleSupport {

    private final int firstSlot;
    private final int lastSlot;

    public ModuleSupport(int firstSlot, int lastSlot) {
        this.firstSlot = firstSlot;
        this.lastSlot = lastSlot;
    }

    public ModuleSupport(int slot) {
        this.firstSlot = slot;
        this.lastSlot = slot;
    }

    @Override
    public int getFirstSlot() {
        return firstSlot;
    }

    @Override
    public int getLastSlot() {
        return lastSlot;
    }
}
