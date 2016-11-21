package mcjty.lib.container;

import mcjty.lib.tools.ItemStackTools;
import mcjty.lib.tools.WorldTools;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * This inventory locator serves as a cache and a way to quickly items in an adjacent
 * inventory. If the inventory is full it will throw out the items in the world.
 */
public class InventoryLocator {

    private BlockPos inventoryCoordinate = null;
    private EnumFacing inventorySide = null;

    private IItemHandler getItemHandlerAtDirection(World worldObj, BlockPos thisCoordinate, EnumFacing direction) {
        if (direction == null) {
            if (inventoryCoordinate != null) {
                return getItemHandlerAtCoordinate(worldObj, inventoryCoordinate, inventorySide);
            }
            return null;
        }
        TileEntity te = worldObj.getTileEntity(thisCoordinate);
        if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite())) {
            // Remember in inventoryCoordinate (acts as a cache)
            inventoryCoordinate = thisCoordinate.offset(direction);
            inventorySide = direction.getOpposite();
            return getItemHandlerAtCoordinate(worldObj, inventoryCoordinate, inventorySide);
        }
        return null;
    }

    private IItemHandler getItemHandlerAtCoordinate(World worldObj, BlockPos c, EnumFacing direction) {
        TileEntity te = worldObj.getTileEntity(c);
        if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction)) {
            return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction);
        }
        return null;
    }


    private IInventory getInventoryAtDirection(World worldObj, BlockPos thisCoordinate, EnumFacing direction) {
        if (direction == null) {
            if (inventoryCoordinate != null) {
                return getInventoryAtCoordinate(worldObj, inventoryCoordinate);
            }
            return null;
        }
        // Remember in inventoryCoordinate (acts as a cache)
        inventoryCoordinate = thisCoordinate.offset(direction);
        inventorySide = direction.getOpposite();
        return getInventoryAtCoordinate(worldObj, inventoryCoordinate);
    }

    private IInventory getInventoryAtCoordinate(World worldObj, BlockPos c) {
        TileEntity te = worldObj.getTileEntity(c);
        if (te instanceof IInventory) {
            return (IInventory) te;
        }
        return null;
    }

    public void ejectStack(World worldObj, BlockPos pos, ItemStack stack, BlockPos thisCoordinate, EnumFacing[] directions) {
        for (EnumFacing dir : directions) {
            IItemHandler handler = getItemHandlerAtDirection(worldObj, thisCoordinate, dir);
            if (stack == null) {
                break;
            }

            if (handler != null) {
                stack = ItemHandlerHelper.insertItem(handler, stack, false);
            } else {
                IInventory inventory = getInventoryAtDirection(worldObj, thisCoordinate, dir);
                if (inventory != null) {
                    int amount = InventoryHelper.mergeItemStackSafe(inventory, false, getInventorySide(), stack, 0, inventory.getSizeInventory(), null);
                    if (amount == 0) {
                        stack = ItemStackTools.getEmptyStack();
                    } else {
                        ItemStackTools.setStackSize(stack, amount);
                    }
                }
            }
        }

        if (stack != null) {
            EntityItem entityItem = new EntityItem(worldObj, pos.getX(), pos.getY(), pos.getZ(), stack);
            WorldTools.spawnEntity(worldObj, entityItem);
        }
    }


    public EnumFacing getInventorySide() {
        return inventorySide;
    }

}
