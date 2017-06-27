package mcjty.lib.compat;

import cofh.redstoneflux.api.IEnergyHandler;
import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class RedstoneFluxCompatibility {

    public static boolean isEnergyHandler(TileEntity te) {
        return te instanceof IEnergyHandler;
    }

    public static boolean isEnergyReceiver(TileEntity te) {
        return te instanceof IEnergyReceiver;
    }

    public static int getEnergy(TileEntity te) {
        return ((IEnergyHandler) te).getEnergyStored(EnumFacing.DOWN);
    }

    public static int getMaxEnergy(TileEntity te) {
        return ((IEnergyHandler) te).getMaxEnergyStored(EnumFacing.DOWN);
    }

    public static int receiveEnergy(TileEntity te, EnumFacing from, int maxReceive) {
        return ((IEnergyReceiver) te).receiveEnergy(from, maxReceive, false);
    }
}
