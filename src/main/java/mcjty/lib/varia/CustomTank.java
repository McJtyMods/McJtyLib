package mcjty.lib.varia;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class CustomTank implements IFluidHandler, IFluidTank {

    @Nonnull
    protected FluidStack fluid = FluidStack.EMPTY;
    protected int capacity;

    public CustomTank(int capacity) {
        this.capacity = capacity;
    }

    public CustomTank setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return true;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    @Nonnull
    public FluidStack getFluid() {
        return fluid;
    }

    @Override
    public int getFluidAmount() {
        return fluid.getAmount();
    }

    public CustomTank readFromNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        FluidStack fluid = FluidStack.parse(provider, nbt).orElse(FluidStack.EMPTY);
        setFluid(fluid);
        return this;
    }

    public CompoundTag writeToNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        fluid.save(provider, nbt);
        return nbt;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !isFluidValid(resource)) {
            return 0;
        }
        if (action.simulate()) {
            if (fluid.isEmpty()) {
                return Math.min(capacity, resource.getAmount());
            }
            if (!fluid.isFluidEqual(resource)) {
                return 0;
            }
            return Math.min(capacity - fluid.getAmount(), resource.getAmount());
        }
        if (fluid.isEmpty()) {
            onContentsChanged();
            fluid = new FluidStack(resource.getFluidHolder(), Math.min(capacity, resource.getAmount()), resource.getComponentsPatch());
            return fluid.getAmount();
        }
        if (!fluid.isFluidEqual(resource)) {
            return 0;
        }
        int filled = capacity - fluid.getAmount();

        if (resource.getAmount() < filled) {
            onContentsChanged();
            fluid.grow(resource.getAmount());
            filled = resource.getAmount();
        } else {
            onContentsChanged();
            fluid.setAmount(capacity);
        }
        return filled;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !resource.isFluidEqual(fluid)) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        int drained = maxDrain;
        if (fluid.getAmount() < drained) {
            drained = fluid.getAmount();
        }
        FluidStack stack = new FluidStack(fluid.getFluidHolder(), drained, fluid.getComponentsPatch());
        if (action.execute()) {
            onContentsChanged();
            fluid.shrink(drained);
        }
        if (fluid.getAmount() <= 0) {
            fluid = FluidStack.EMPTY;
        }
        return stack;
    }

    protected void onContentsChanged() {

    }

    public void setFluid(@Nonnull FluidStack stack) {
        this.fluid = stack;
    }

    public boolean isEmpty() {
        return fluid.isEmpty();
    }

    public int getSpace() {
        return Math.max(0, capacity - fluid.getAmount());
    }

}