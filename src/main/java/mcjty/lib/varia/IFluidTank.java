package mcjty.lib.varia;

public interface IFluidTank {
    FluidStack getFluid();
    int getFluidAmount();
    int getCapacity();
    boolean isFluidValid(FluidStack stack);
}
