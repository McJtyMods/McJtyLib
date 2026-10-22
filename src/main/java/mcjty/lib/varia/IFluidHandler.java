package mcjty.lib.varia;

/** Container-facing fluid amounts are millibuckets; native Fabric Storage uses droplets. */
public interface IFluidHandler {

    enum FluidAction {
        EXECUTE, SIMULATE;
        public boolean execute() {
            return this==EXECUTE;
        }
        public boolean simulate() {
            return this==SIMULATE;
        }
    }

    int getTanks();
    FluidStack getFluidInTank(int tank);
    int getTankCapacity(int tank);
    boolean isFluidValid(int tank,FluidStack fluid);

    int fill(FluidStack stack,FluidAction action);
    FluidStack drain(FluidStack stack,FluidAction action);
    FluidStack drain(int amount,FluidAction action);

}
