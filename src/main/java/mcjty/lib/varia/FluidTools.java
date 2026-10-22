package mcjty.lib.varia;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import mcjty.lib.varia.FluidStack;
import mcjty.lib.varia.IFluidHandler;
import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class FluidTools {

    public static ItemStack convertFluidToBucket(FluidStack stack) {
        return fillContainer(stack,new ItemStack(Items.BUCKET));
    }

    public static FluidStack convertBucketToFluid(ItemStack stack) {
        var handler=FluidContainers.handler(stack);
        if(handler!=null)for(int i=0;i<handler.getTanks();i++) {
            var fluid=handler.getFluidInTank(i);
            if(!fluid.isEmpty())return fluid;
        }
        return FluidStack.EMPTY;
    }

    public static boolean isEmptyContainer(ItemStack stack) {
        var h=FluidContainers.handler(stack);
        if(h==null)return false;
        for(int i=0;i<h.getTanks();i++)if(h.getTankCapacity(i)>0&&h.getFluidInTank(i).isEmpty())return true;
        return false;
    }

    public static boolean isFilledContainer(ItemStack stack) {
        var h=FluidContainers.handler(stack);
        if(h==null||h.getTanks()==0)return false;
        for(int i=0;i<h.getTanks();i++)if(h.getFluidInTank(i).getAmount()<h.getTankCapacity(i))return false;
        return true;
    }

    public static ItemStack drainContainer(ItemStack stack) {
        return FluidContainers.drain(stack);
    }

    public static ItemStack fillContainer(FluidStack fluid,ItemStack stack) {
        return FluidContainers.fill(fluid,stack);
    }

    @Nonnull
    public static FluidStack pickupFluidBlock(Level world, BlockPos pos, @Nonnull Predicate<FluidStack> action, @Nonnull Runnable clearBlock) {

        BlockState blockstate = world.getBlockState(pos);

        FluidState fluidstate = world.getFluidState(pos);

        Fluid fluid = fluidstate.getType();

        if (blockstate.getBlock() instanceof LiquidBlock) {

            FluidStack stack = new FluidStack(fluid, 1000);

            if (action.test(stack)) {

                clearBlock.run();

            }

            return stack;

        }
        else if (blockstate.getBlock() instanceof BucketPickup && fluid != Fluids.EMPTY) {

            FluidStack stack = new FluidStack(fluid, 1000);

            if (action.test(stack)) {

                ItemStack fluidBucket = ((BucketPickup) blockstate.getBlock()).pickupBlock(null, world, pos, blockstate);

                return convertBucketToFluid(fluidBucket);

            }

            return stack;

        }
        else if (blockstate.getBlock() instanceof BucketPickup) {
            // @todo 1.20 right?
            FluidStack stack = new FluidStack(Fluids.WATER, 1000);

            if (action.test(stack)) {

                BlockEntity tileentity = blockstate.getBlock() instanceof EntityBlock ? world.getBlockEntity(pos) : null;

                Block.dropResources(blockstate, world, pos, tileentity);

                clearBlock.run();

            }

            return stack;

        }

        return FluidStack.EMPTY;

    }

}
