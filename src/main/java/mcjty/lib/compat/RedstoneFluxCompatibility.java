package mcjty.lib.compat;

import cofh.redstoneflux.api.IEnergyConnection;
import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.redstoneflux.api.IEnergyHandler;
import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class RedstoneFluxCompatibility {

    public static boolean isEnergyHandler(TileEntity te) {
        return te instanceof IEnergyHandler;
    }

    public static boolean isEnergyReceiver(TileEntity te) {
        return te instanceof IEnergyReceiver;
    }

    public static int getEnergy(TileEntity te, EnumFacing side) {
        return ((IEnergyHandler) te).getEnergyStored(side);
    }

    public static int getMaxEnergy(TileEntity te, EnumFacing side) {
        return ((IEnergyHandler) te).getMaxEnergyStored(side);
    }

    public static int receiveEnergy(TileEntity te, EnumFacing from, int maxReceive) {
        return ((IEnergyReceiver) te).receiveEnergy(from, maxReceive, false);
    }

    public static boolean isEnergyItem(Item item) {
        return item instanceof IEnergyContainerItem;
    }

    public static int receiveEnergy(Item item, ItemStack stack, int maxReceive, boolean simulate) {
        return ((IEnergyContainerItem) item).receiveEnergy(stack, maxReceive, simulate);
    }

    public static boolean isEnergyConnection(TileEntity te) {
        return te instanceof IEnergyConnection;
    }

    public static boolean canConnectEnergy(TileEntity te, EnumFacing facing) {
        return ((IEnergyConnection)te).canConnectEnergy(facing);
    }
}
