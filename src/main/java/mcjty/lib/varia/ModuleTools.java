package mcjty.lib.varia;

import mcjty.lib.api.modules.ItemModule;
import mcjty.lib.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ModuleTools {

    public static boolean hasModuleTarget(ItemStack stack) {
        return stack.get(Registration.ITEM_MODULE) != null;
    }

    public static void setPositionInModule(ItemStack stack, ResourceKey<Level> dimension, BlockPos pos, String name) {
        stack.set(Registration.ITEM_MODULE, new ItemModule(GlobalPos.of(dimension, pos), name));
    }

    public static void clearPositionInModule(ItemStack stack) {
        stack.remove(Registration.ITEM_MODULE);
    }

    @Nonnull
    public static BlockPos getPositionFromModule(ItemStack stack) {
        ItemModule module = stack.get(Registration.ITEM_MODULE);
        if (module != null) {
            return module.pos().pos();
        } else {
            return BlockPos.ZERO;
        }
    }

    @Nullable
    public static ResourceKey<Level> getDimensionFromModule(ItemStack stack) {
        ItemModule module = stack.get(Registration.ITEM_MODULE);
        if (module != null) {
            return module.pos().dimension();
        } else {
            return null;
        }
    }

    public static String getTargetString(ItemStack stack) {
        ItemModule module = stack.get(Registration.ITEM_MODULE);
        if (module != null) {
            String name = module.name();
            GlobalPos pos = module.pos();
            return name + " (at " + pos.pos().getX() + "," + pos.pos().getY() + "," + pos.pos().getZ() + ", " + pos.dimension().location() + ")";
        } else {
            return "<unset>";
        }
    }

    /**
     * Inject a module that the player is holding into the appropriate slots (slots are from start to stop inclusive both ends)
     *
     * @return true if successful
     */
    public static boolean installModule(Player player, ItemStack heldItem, InteractionHand hand, BlockPos pos, int start, int stop) {
        Level world = player.getCommandSenderWorld();
        BlockEntity te = world.getBlockEntity(pos);
        if (te == null) {
            return false;
        }
        IItemHandler inventory = world.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
        if (inventory != null) {
            for (int i = start; i <= stop; i++) {
                if (inventory.getStackInSlot(i).isEmpty()) {
                    ItemStack copy = heldItem.copy();
                    copy.setCount(1);
                    if (inventory instanceof IItemHandlerModifiable) {
                        ((IItemHandlerModifiable) inventory).setStackInSlot(i, copy);
                    } else {
                        throw new IllegalStateException("Not an IItemHandlerModifiable!");
                    }
                    heldItem.shrink(1);
                    if (heldItem.isEmpty()) {
                        player.setItemInHand(hand, ItemStack.EMPTY);
                    }
                    if (world.isClientSide) {
                        player.displayClientMessage(ComponentFactory.literal("Installed module"), false);
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
