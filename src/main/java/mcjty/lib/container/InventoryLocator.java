package mcjty.lib.container;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import mcjty.lib.varia.InventoryTools;

import javax.annotation.Nonnull;
import org.jspecify.annotations.Nullable;

/**
 * This inventory locator serves as a cache and a way to quickly items in an adjacent
 * inventory. If the inventory is full it will throw out the items in the world.
 */
public class InventoryLocator {

    private BlockPos inventoryCoordinate = null;
    private Direction inventorySide = null;

    @Nonnull
    private ResourceHandler<ItemResource> getItemHandlerAtDirection(Level worldObj, BlockPos thisCoordinate, Direction direction) {
        if (direction == null) {
            if (inventoryCoordinate != null) {
                return getItemHandlerAtCoordinate(worldObj, inventoryCoordinate, inventorySide);
            }
            return null;
        }
        if (mcjty.lib.varia.CapabilityTools.itemHandler(worldObj.getCapability(Capabilities.Item.BLOCK, thisCoordinate, direction.getOpposite())) != null) {
            // Remember in inventoryCoordinate (acts as a cache)
            inventoryCoordinate = thisCoordinate.relative(direction);
            inventorySide = direction.getOpposite();
            return getItemHandlerAtCoordinate(worldObj, inventoryCoordinate, inventorySide);
        } else {
            return null;
        }
    }

    @Nullable
    private ResourceHandler<ItemResource> getItemHandlerAtCoordinate(Level worldObj, BlockPos c, Direction direction) {
        return mcjty.lib.varia.CapabilityTools.itemHandler(worldObj.getCapability(Capabilities.Item.BLOCK, c, direction));
    }


    public void ejectStack(Level worldObj, BlockPos pos, ItemStack stack, BlockPos thisCoordinate, Direction[] directions) {
        for (Direction dir : directions) {
            if (stack.isEmpty()) {
                break;
            }
            ResourceHandler<ItemResource> itemHandler = getItemHandlerAtDirection(worldObj, thisCoordinate, dir);
            if (itemHandler != null) {
                stack = InventoryTools.insertItem(itemHandler, stack, false);
            }
        }

        if (!stack.isEmpty()) {
            ItemEntity entityItem = new ItemEntity(worldObj, pos.getX(), pos.getY(), pos.getZ(), stack);
            worldObj.addFreshEntity(entityItem);
        }
    }


    public Direction getInventorySide() {
        return inventorySide;
    }

}
