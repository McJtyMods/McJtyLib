package mcjty.lib.compat;

import com.brandon3055.draconicevolution.api.IExtendedRFStorage;
import net.minecraft.tileentity.TileEntity;

public class EnergySupportDraconic {

    public static boolean isDraconicEnergyTile(TileEntity te) {
        return te instanceof IExtendedRFStorage;
    }

    public static long getMaxEnergy(TileEntity te) {
        IExtendedRFStorage storage = (IExtendedRFStorage) te;
        return storage.getExtendedCapacity();
    }

    public static long getCurrentEnergy(TileEntity te) {
        IExtendedRFStorage storage = (IExtendedRFStorage) te;
        return storage.getExtendedStorage();
    }

}
