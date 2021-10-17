package mcjty.lib.varia;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ModuleTools {

    public static boolean hasModuleTarget(ItemStack stack) {
        if (!stack.hasTag()) {
            return false;
        }
        return stack.getTag().contains("monitorx");
    }

    public static void setPositionInModule(ItemStack stack, RegistryKey<World> dimension, BlockPos pos, String name) {
        CompoundNBT tag = stack.getOrCreateTag();
        if (dimension != null) {
            tag.putString("monitordim", dimension.location().toString());
        }
        if (name != null) {
            tag.putString("monitorname", name);
        }
        tag.putInt("monitorx", pos.getX());
        tag.putInt("monitory", pos.getY());
        tag.putInt("monitorz", pos.getZ());
    }

    public static void clearPositionInModule(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.remove("monitordim");
        tag.remove("monitorx");
        tag.remove("monitory");
        tag.remove("monitorz");
        tag.remove("monitorname");
    }

    public static BlockPos getPositionFromModule(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTag();
        int monitorx = tag.getInt("monitorx");
        int monitory = tag.getInt("monitory");
        int monitorz = tag.getInt("monitorz");
        return new BlockPos(monitorx, monitory, monitorz);
    }

    public static RegistryKey<World> getDimensionFromModule(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTag();
        if (tag.contains("monitordim")) {
            return LevelTools.getId(tag.getString("monitordim"));
        } else {
            return null;
        }
    }

    public static String getTargetString(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null) {
            if (tag.contains("monitorx")) {
                int monitorx = tag.getInt("monitorx");
                int monitory = tag.getInt("monitory");
                int monitorz = tag.getInt("monitorz");
                String monitorname = tag.getString("monitorname");
                String monitordim = tag.getString("monitordim");
                if (!monitordim.isEmpty()) {
                    return monitorname + " (at " + monitorx + "," + monitory + "," + monitorz + ", " + monitordim + ")";
                } else {
                    return monitorname + " (at " + monitorx + "," + monitory + "," + monitorz + ")";
                }
            }
        }
        return "<unset>";
    }

    /**
     * Inject a module that the player is holding into the appropriate slots (slots are from start to stop inclusive both ends)
     *
     * @return true if successful
     */
    public static boolean installModule(PlayerEntity player, ItemStack heldItem, Hand hand, BlockPos pos, int start, int stop) {
        World world = player.getCommandSenderWorld();
        TileEntity te = world.getBlockEntity(pos);
        if (te == null) {
            return false;
        }
        return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(inventory -> {
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
                        player.displayClientMessage(new StringTextComponent("Installed module"), false);
                    }
                    return true;
                }
            }
            return false;
        }).orElse(false);
    }
}
