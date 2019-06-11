package mcjty.lib.compat;

import net.minecraft.tileentity.TileEntity;

public class EnergySupportDraconic {

    public static boolean isDraconicEnergyTile(TileEntity te) {
        return false;
        // @todo 1.14 return te instanceof IExtendedRFStorage;
    }

    public static long getMaxEnergy(TileEntity te) {
        return 0;
        // @todo 1.14
//        IExtendedRFStorage storage = (IExtendedRFStorage) te;
//        return storage.getExtendedCapacity();
    }

    public static long getCurrentEnergy(TileEntity te) {
        return 0;
        // @todo 1.14
//        IExtendedRFStorage storage = (IExtendedRFStorage) te;
//        return storage.getExtendedStorage();
    }

}
