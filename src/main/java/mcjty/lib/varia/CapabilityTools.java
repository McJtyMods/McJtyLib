package mcjty.lib.varia;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import team.reborn.energy.api.EnergyStorage;
import mcjty.lib.varia.IFluidHandler;
import mcjty.lib.container.IItemHandler;
import org.jspecify.annotations.Nullable;

public class CapabilityTools {

    @Nullable
    public static IItemHandler getItemCapabilitySafe(BlockEntity tileEntity) {
        if (tileEntity != null) {
            try {
                return mcjty.lib.varia.CapabilityTools.itemHandler(ItemStorage.SIDED.find(tileEntity.getLevel(), tileEntity.getBlockPos(), null));
            } catch (RuntimeException e) {
                reportWrongBlock(tileEntity, e);
            }
        }
        return null;
    }

    @Nullable
    public static IFluidHandler getFluidCapabilitySafe(BlockEntity tileEntity) {
        if (tileEntity != null) {
            try {
                return mcjty.lib.varia.CapabilityTools.fluidHandler(FluidStorage.SIDED.find(tileEntity.getLevel(), tileEntity.getBlockPos(), null));
            } catch (RuntimeException e) {
                reportWrongBlock(tileEntity, e);
            }
        }
        return null;
    }

    private static void reportWrongBlock(BlockEntity tileEntity, Exception e) {
        if (tileEntity != null) {
            Identifier name = Tools.getId(tileEntity.getLevel().getBlockState(tileEntity.getBlockPos()));
            Logging.logError("Block " + name.toString() + " at " + BlockPosTools.toString(tileEntity.getBlockPos()) + " does not respect the capability API and crashes on null side.");
            Logging.logError("Please report to the corresponding mod. This is not a bug in RFTools!");
        }
        if (e != null) {
            Logging.logError("Exception", e);
        }
    }
    public static IItemHandler itemHandler(net.fabricmc.fabric.api.transfer.v1.storage.Storage<net.fabricmc.fabric.api.transfer.v1.item.ItemVariant> storage) {
        return storage == null ? null : new ItemStorageAdapter(storage);
    }
    public static IEnergyStorage energyHandler(team.reborn.energy.api.EnergyStorage storage) {
        if(storage==null)return null;
        return new IEnergyStorage(){
            public int receiveEnergy(int n,boolean simulate){try(var tx=net.fabricmc.fabric.api.transfer.v1.transaction.Transaction.openOuter()){int moved=(int)storage.insert(n,tx);if(!simulate)tx.commit();return moved;}}
            public int extractEnergy(int n,boolean simulate){try(var tx=net.fabricmc.fabric.api.transfer.v1.transaction.Transaction.openOuter()){int moved=(int)storage.extract(n,tx);if(!simulate)tx.commit();return moved;}}
            public int getEnergyStored(){return EnergyTools.unsignedClampToInt(storage.getAmount());}
            public int getMaxEnergyStored(){return EnergyTools.unsignedClampToInt(storage.getCapacity());}
            public boolean canReceive(){return storage.supportsInsertion();}public boolean canExtract(){return storage.supportsExtraction();}
        };
    }
    public static IFluidHandler fluidHandler(net.fabricmc.fabric.api.transfer.v1.storage.Storage<net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant> storage) {
        return storage==null?null:new FluidStorageAdapter(storage);
    }
    /** A transactional context for component changes on an existing item stack. */
    public static net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext itemContext(net.minecraft.world.item.ItemStack stack) {
        var slot = new net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage() {
            private net.minecraft.world.item.ItemStack current = stack.copy();
            @Override protected net.minecraft.world.item.ItemStack getStack() { return current; }
            @Override protected void setStack(net.minecraft.world.item.ItemStack value) { current = value; }
            @Override protected boolean canInsert(net.fabricmc.fabric.api.transfer.v1.item.ItemVariant variant) {
                return variant.getItem() == stack.getItem();
            }
            @Override protected void onFinalCommit() {
                for (var type : java.util.List.copyOf(stack.getComponents().keySet())) {
                    if (!current.has(type)) stack.remove(type);
                }
                stack.applyComponents(current.getComponents());
                stack.setCount(current.getCount());
            }
        };
        return net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext.ofSingleSlot(slot);
    }

}
