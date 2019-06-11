package mcjty.lib.compat;

import net.minecraft.tileentity.TileEntity;

public class EnergySupportEnderIO {

    public static boolean isEnderioTileEntity(TileEntity te) {
        return false;
        // @todo 1.14 return te instanceof IPowerStorage;
    }

    public static long getMaxEnergy(TileEntity te) {
        return 0;
        // @todo 1.14
//        IPowerStorage storage = (IPowerStorage) te;
//        return storage.getMaxEnergyStoredL();
    }

    public static long getCurrentEnergy(TileEntity te) {
        return 0;
        // @todo 1.14
//        IPowerStorage storage = (IPowerStorage) te;
//        return storage.getEnergyStoredL();
    }

}
