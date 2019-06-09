package mcjty.lib.varia;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockTools {
    private static final Random random = new Random();

    public static void emptyInventoryInWorld(World world, int x, int y, int z, Block block, IInventory inventory) {
        for (int i = 0; i < inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = inventory.getStackInSlot(i);
            spawnItemStack(world, x, y, z, itemstack);
            inventory.setInventorySlotContents(i, ItemStack.EMPTY);
        }
        //TODO: What was this?
        //world.func_147453_f(x, y, z, block);
    }

    public static void spawnItemStack(World world, int x, int y, int z, ItemStack itemstack) {
        if (!itemstack.isEmpty()) {
            EntityItem entityItem = new EntityItem(world, x, y, z, itemstack);
            world.spawnEntity(entityItem);
        }
    }

    public static Block getBlock(ItemStack stack) {
        if (stack.getItem() instanceof ItemBlock) {
            return ((ItemBlock) stack.getItem()).getBlock();
        } else {
            return null;
        }
    }

    public static String getModid(ItemStack stack) {
        if (!stack.isEmpty()) {
            return stack.getItem().getRegistryName().getResourceDomain();
        } else {
            return "";
        }
    }

    public static String getModidForBlock(Block block) {
        ResourceLocation nameForObject = block.getRegistryName();
        if (nameForObject == null) {
            return "?";
        }
        return nameForObject.getResourceDomain();
    }

    public static String getReadableName(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return getReadableName(state.getBlock().getItem(world, pos, state));
    }

    public static String getReadableName(ItemStack stack) {
        return stack.getDisplayName();
    }

    public static BlockState placeStackAt(PlayerEntity player, ItemStack blockStack, World world, BlockPos pos, @Nullable BlockState origState) {
        if (blockStack.getItem() instanceof ItemBlock) {
            ItemBlock itemBlock = (ItemBlock) blockStack.getItem();
            if (origState == null) {
                origState = itemBlock.getBlock().getStateForPlacement(world, pos, Direction.UP, 0, 0, 0, blockStack.getItem().getMetadata(blockStack), player, Hand.MAIN_HAND);
            }
            if (itemBlock.placeBlockAt(blockStack, player, world, pos, Direction.UP, 0, 0, 0, origState)) {
                blockStack.shrink(1);
            }
            return origState;
        } else {
            player.setHeldItem(Hand.MAIN_HAND, blockStack);
            player.setPosition(pos.getX()+.5, pos.getY()+1.5, pos.getZ()+.5);
            blockStack.getItem().onItemUse(player, world, pos.down(), Hand.MAIN_HAND, Direction.UP, 0, 0, 0);
            return world.getBlockState(pos);
        }
    }

}
