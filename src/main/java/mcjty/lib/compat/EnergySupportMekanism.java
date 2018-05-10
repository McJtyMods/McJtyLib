package mcjty.lib.compat;

import mekanism.api.energy.IStrictEnergyStorage;
import net.minecraft.tileentity.TileEntity;

public class EnergySupportMekanism {

    public static boolean isMekanismTileEntity(TileEntity te) {
        return te instanceof IStrictEnergyStorage;
    }

    public static long getMaxEnergy(TileEntity te) {
        IStrictEnergyStorage storage = (IStrictEnergyStorage) te;
        return (long) storage.getMaxEnergy();
    }

    public static long getCurrentEnergy(TileEntity te) {
        IStrictEnergyStorage storage = (IStrictEnergyStorage) te;
        return (long) storage.getEnergy();
    }

}
