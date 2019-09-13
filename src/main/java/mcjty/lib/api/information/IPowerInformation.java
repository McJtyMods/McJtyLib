package mcjty.lib.api.information;

import javax.annotation.Nullable;

/**
 * Implement this capability based interface in your TE so that other
 * mods (like The One Probe) can request information from your block.
 */
public interface IPowerInformation {

    /**
     * Get the energy consumption or production of this machine at this moment.
     * This is the amount per tick.
     * @return a positive number indicating energy production, a negative
     * number indicating energy consumption and 0 otherwise.
     */
    long getEnergyDiffPerTick();

    /**
     * Return a short name indicating the type of energy unit this
     * machine is producing or using. By convention here are a few
     * often used energy units:
     *     * RF
     *     * FE (Forge Energy)
     *     * TESLA
     * @return energy unit or null if this block doesn't need/produce energy
     */
    @Nullable
    String getEnergyUnitName();

    /**
     * Return true if this machine is currently active. That means the machine
     * could actively produce energy, consume materials, produce materials or
     * similar. This should also return true if the machine *could* do the above
     * things but isn't right now due to external circumstances. For example, a
     * quarry that is active but has no room to insert items should return true.
     * A smelter that is active but is out of power should also return true.
     */
    boolean isMachineActive();

    /**
     * Return true if this machine is currently running. That means the machine
     * is actively producing energy, consuming materials, producing materials or
     * similar. In contrast with isMachineActive() this function should only return
     * true if the machine is actually working and doing something. If this is too
     * complicated to compute it is ok to just return the same as isMachineActive()
     * here.
     */
    boolean isMachineRunning();

    /**
     * Return a short (max 30 chars or so) description of what this machine is
     * currently doing
     */
    @Nullable
    String getMachineStatus();
}
