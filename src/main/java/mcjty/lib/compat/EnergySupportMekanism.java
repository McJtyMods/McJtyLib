package mcjty.lib.compat;

import net.minecraft.tileentity.TileEntity;

public class EnergySupportMekanism {

    public static boolean isMekanismTileEntity(TileEntity te) {
        //@todo 1.14
        return false;
//        return te instanceof IStrictEnergyStorage;
    }

    public static long getMaxEnergy(TileEntity te) {
        // @todo 1.14
//        IStrictEnergyStorage storage = (IStrictEnergyStorage) te;
//        return (long) storage.getMaxEnergy();
        return 0;
    }

    public static long getCurrentEnergy(TileEntity te) {
        // @todo 1.14
//        IStrictEnergyStorage storage = (IStrictEnergyStorage) te;
//        return (long) storage.getEnergy();
        return 0;
    }

}
