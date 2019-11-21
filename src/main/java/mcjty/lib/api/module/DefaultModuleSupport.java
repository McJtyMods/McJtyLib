package mcjty.lib.api.module;

/**
 * Helper for IModuleSupport
 */
public abstract class DefaultModuleSupport implements IModuleSupport {

    private final int firstSlot;
    private final int lastSlot;

    public DefaultModuleSupport(int firstSlot, int lastSlot) {
        this.firstSlot = firstSlot;
        this.lastSlot = lastSlot;
    }

    public DefaultModuleSupport(int slot) {
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
