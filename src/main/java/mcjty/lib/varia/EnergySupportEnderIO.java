package mcjty.lib.varia;

import crazypants.enderio.base.power.IPowerStorage;
import net.minecraft.tileentity.TileEntity;

public class EnergySupportEnderIO {

    public static boolean isEnderioTileEntity(TileEntity te) {
        return te instanceof IPowerStorage;
    }

    public static long getMaxEnergy(TileEntity te) {
        IPowerStorage storage = (IPowerStorage) te;
        return storage.getMaxEnergyStoredL();
    }

    public static long getCurrentEnergy(TileEntity te) {
        IPowerStorage storage = (IPowerStorage) te;
        return storage.getEnergyStoredL();
    }

}
