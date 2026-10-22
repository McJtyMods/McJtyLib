package mcjty.lib.varia;

/** Integer energy view used by existing widgets. Native automation uses EnergyStorage. */
public interface IEnergyStorage {

    int receiveEnergy(int amount,boolean simulate);
    int extractEnergy(int amount,boolean simulate);

    int getEnergyStored();
    int getMaxEnergyStored();
    boolean canReceive();
    boolean canExtract();

}
