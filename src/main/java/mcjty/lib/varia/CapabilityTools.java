package mcjty.lib.varia;

import mcjty.lib.container.IItemHandler;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import team.reborn.energy.api.EnergyStorage;

import java.util.List;

import javax.annotation.Nullable;

public class CapabilityTools {

    @Nullable
    public static IItemHandler getItemCapabilitySafe(BlockEntity tileEntity) {
        if (tileEntity != null) {
            try {
                return mcjty.lib.varia.CapabilityTools.itemHandler(
                        ItemStorage.SIDED.find(
                                tileEntity.getLevel(), tileEntity.getBlockPos(), null));
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
                return mcjty.lib.varia.CapabilityTools.fluidHandler(
                        FluidStorage.SIDED.find(
                                tileEntity.getLevel(), tileEntity.getBlockPos(), null));
            } catch (RuntimeException e) {
                reportWrongBlock(tileEntity, e);
            }
        }
        return null;
    }

    private static void reportWrongBlock(BlockEntity tileEntity, Exception e) {
        if (tileEntity != null) {
            ResourceLocation name =
                    Tools.getId(tileEntity.getLevel().getBlockState(tileEntity.getBlockPos()));
            Logging.logError(
                    "Block "
                            + name.toString()
                            + " at "
                            + BlockPosTools.toString(tileEntity.getBlockPos())
                            + " does not respect the capability API and crashes on null side.");
            Logging.logError(
                    "Please report to the corresponding mod. This is not a bug in RFTools!");
        }
        if (e != null) {
            Logging.logError("Exception", e);
        }
    }

    public static IItemHandler itemHandler(Storage<ItemVariant> storage) {
        return storage == null ? null : new ItemStorageAdapter(storage);
    }

    public static IEnergyStorage energyHandler(EnergyStorage storage) {
        if (storage == null) return null;
        return new IEnergyStorage() {
            public int receiveEnergy(int n, boolean simulate) {
                try (var tx = Transaction.openOuter()) {
                    int moved = (int) storage.insert(n, tx);
                    if (!simulate) tx.commit();
                    return moved;
                }
            }

            public int extractEnergy(int n, boolean simulate) {
                try (var tx = Transaction.openOuter()) {
                    int moved = (int) storage.extract(n, tx);
                    if (!simulate) tx.commit();
                    return moved;
                }
            }

            public int getEnergyStored() {
                return EnergyTools.unsignedClampToInt(storage.getAmount());
            }

            public int getMaxEnergyStored() {
                return EnergyTools.unsignedClampToInt(storage.getCapacity());
            }

            public boolean canReceive() {
                return storage.supportsInsertion();
            }

            public boolean canExtract() {
                return storage.supportsExtraction();
            }
        };
    }

    public static IFluidHandler fluidHandler(Storage<FluidVariant> storage) {
        return storage == null ? null : new FluidStorageAdapter(storage);
    }

    /** A transactional context for component changes on an existing item stack. */
    public static ContainerItemContext itemContext(ItemStack stack) {
        var slot =
                new SingleStackStorage() {
                    private ItemStack current = stack.copy();

                    @Override
                    protected ItemStack getStack() {
                        return current;
                    }

                    @Override
                    protected void setStack(ItemStack value) {
                        current = value;
                    }

                    @Override
                    protected boolean canInsert(ItemVariant variant) {
                        return variant.getItem() == stack.getItem();
                    }

                    @Override
                    protected void onFinalCommit() {
                        for (var type : List.copyOf(stack.getComponents().keySet())) {
                            if (!current.has(type)) stack.remove(type);
                        }
                        stack.applyComponents(current.getComponents());
                        stack.setCount(current.getCount());
                    }
                };
        return ContainerItemContext.ofSingleSlot(slot);
    }
}
