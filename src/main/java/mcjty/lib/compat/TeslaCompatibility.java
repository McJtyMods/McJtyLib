package mcjty.lib.compat;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;

public class TeslaCompatibility {

    public static boolean isEnergyHandler(TileEntity te, @Nullable Direction side) {
        return false;   // @todo 1.14 return te != null && te.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, side);
    }

    public static boolean isEnergyReceiver(TileEntity te, @Nullable Direction side) {
        return false;   // @todo 1.14 return te != null && te.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, side);
    }

    public static long getEnergy(TileEntity te, @Nullable Direction side) {
        return 0;   // @todo 1.14 return te.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, side).getStoredPower();
    }

    public static long getMaxEnergy(TileEntity te, @Nullable Direction side) {
        return 0;   // @todo 1.14 return te.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, side).getCapacity();
    }

    public static long receiveEnergy(TileEntity te, @Nullable Direction from, long maxReceive) {
        return 0;   // @todo 1.14 return te.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, from).givePower(maxReceive, false);
    }

    public static boolean isEnergyItem(ItemStack stack) {
        return false;   // @todo 1.14 return stack.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null);
    }

    public static long receiveEnergy(ItemStack stack, long maxReceive, boolean simulate) {
        return 0;   // @todo 1.14 return stack.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null).givePower(maxReceive, simulate);
    }
}
