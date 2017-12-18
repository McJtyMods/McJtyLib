package mcjty.lib.varia;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

import static net.minecraft.util.EnumFacing.*;

public class BlockTools {
    private static final Random random = new Random();

    // Use these flags if you want to support a single redstone signal and 3 bits for orientation.
    public static final int MASK_ORIENTATION = 0x7;
    public static final int MASK_REDSTONE = 0x8;

    // Use these flags if you want to support both redstone in and output and only 2 bits for orientation.
    public static final int MASK_ORIENTATION_HORIZONTAL = 0x3;          // Only two bits for orientation
    public static final int MASK_REDSTONE_IN = 0x8;                     // Redstone in
    public static final int MASK_REDSTONE_OUT = 0x4;                    // Redstone out
    public static final int MASK_STATE = 0xc;                           // If redstone is not used: state

    public static EnumFacing getOrientation(int metadata) {
        return EnumFacing.VALUES[(metadata & MASK_ORIENTATION)];
    }

    public static EnumFacing getOrientation(IBlockState state) {
        int meta = state.getBlock().getMetaFromState(state);
        return getOrientation(meta);
    }

    // Given the metavalue of a block, reorient the world direction to the internal block direction
    // so that the front side will be SOUTH.
    public static EnumFacing reorient(EnumFacing side, int meta) {
        return reorient(side, getOrientation(meta));
    }
    public static EnumFacing reorient(EnumFacing side, IBlockState state) {
        return reorient(side, getOrientation(state));
    }

    // Given the metavalue of a block, reorient the world direction to the internal block direction
    // so that the front side will be SOUTH.
    public static EnumFacing reorientHoriz(EnumFacing side, int meta) {
        return reorient(side, getOrientationHoriz(meta));
    }
    public static EnumFacing reorientHoriz(EnumFacing side, IBlockState state) {
        return reorient(side, getOrientationHoriz(state));
    }

    public static EnumFacing reorient(EnumFacing side, EnumFacing blockDirection) {
        switch (blockDirection) {
            case DOWN:
                switch (side) {
                    case DOWN: return SOUTH;
                    case UP: return NORTH;
                    case NORTH: return UP;
                    case SOUTH: return DOWN;
                    case WEST: return EAST;
                    case EAST: return WEST;
                    default: return side;
                }
            case UP:
                switch (side) {
                    case DOWN: return NORTH;
                    case UP: return SOUTH;
                    case NORTH: return UP;
                    case SOUTH: return DOWN;
                    case WEST: return WEST;
                    case EAST: return EAST;
                    default: return side;
                }
            case NORTH:
                if (side == DOWN || side == UP) {
                    return side;
                }
                return side.getOpposite();
            case SOUTH:
                return side;
            case WEST:
                if (side == DOWN || side == UP) {
                    return side;
                } else if (side == WEST) {
                    return SOUTH;
                } else if (side == NORTH) {
                    return WEST;
                } else if (side == EAST) {
                    return NORTH;
                } else {
                    return EAST;
                }
            case EAST:
                if (side == DOWN || side == UP) {
                    return side;
                } else if (side == WEST) {
                    return NORTH;
                } else if (side == NORTH) {
                    return EAST;
                } else if (side == EAST) {
                    return SOUTH;
                } else {
                    return WEST;
                }
            default:
                return side;
        }
        //return side;
    }

    public static EnumFacing getTopDirection(EnumFacing direction) {
        switch(direction) {
            case DOWN:
                return EnumFacing.SOUTH;
            case UP:
                return EnumFacing.NORTH;
            default:
                return EnumFacing.UP;
        }
    }

    public static EnumFacing getBottomDirection(EnumFacing direction) {
        switch(direction) {
            case DOWN:
                return EnumFacing.NORTH;
            case UP:
                return EnumFacing.SOUTH;
            default:
                return DOWN;
        }
    }

    public static int setOrientation(int metadata, EnumFacing orientation) {
        return (metadata & ~MASK_ORIENTATION) | orientation.ordinal();
    }

    public static EnumFacing getOrientationHoriz(int metadata) {
        return EnumFacing.VALUES[(metadata & MASK_ORIENTATION_HORIZONTAL)+2];
    }

    public static EnumFacing getOrientationHoriz(IBlockState state) {
        int metadata = state.getBlock().getMetaFromState(state);
        return getOrientationHoriz(metadata);
    }


    public static int setOrientationHoriz(int metadata, EnumFacing orientation) {
        return (metadata & ~MASK_ORIENTATION_HORIZONTAL) | (orientation.ordinal()-2);
    }

    public static int setState(int metadata, int value) {
        return (metadata & ~MASK_STATE) | (value << 2);
    }

    public static int getState(int metadata) {
        return (metadata & MASK_STATE) >> 2;
    }

    public static EnumFacing determineOrientation(BlockPos pos, EntityLivingBase entityLivingBase) {
        return determineOrientation(pos.getX(), pos.getY(), pos.getZ(), entityLivingBase);
    }

    public static EnumFacing determineOrientation(int x, int y, int z, EntityLivingBase entityLivingBase) {
        if (MathHelper.abs((float) entityLivingBase.posX - x) < 2.0F && MathHelper.abs((float)entityLivingBase.posZ - z) < 2.0F) {
            double d0 = entityLivingBase.posY + 1.82D - entityLivingBase.getYOffset();

            if (d0 - y > 2.0D) {
                return EnumFacing.UP;
            }

            if (y - d0 > 0.0D) {
                return DOWN;
            }
        }
        int l = MathTools.floor((entityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return l == 0 ? EnumFacing.NORTH : (l == 1 ? EnumFacing.EAST : (l == 2 ? EnumFacing.SOUTH : (l == 3 ? EnumFacing.WEST : DOWN)));
    }

    public static EnumFacing determineOrientationHoriz(EntityLivingBase entityLivingBase) {
        int l = MathTools.floor((entityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return l == 0 ? EnumFacing.NORTH : (l == 1 ? EnumFacing.EAST : (l == 2 ? EnumFacing.SOUTH : (l == 3 ? EnumFacing.WEST : DOWN)));
    }


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
            float f = random.nextFloat() * 0.8F + 0.1F;
            float f1 = random.nextFloat() * 0.8F + 0.1F;
            EntityItem entityitem;

            float f2 = random.nextFloat() * 0.8F + 0.1F;
            while (itemstack.getCount() > 0) {
                int j = random.nextInt(21) + 10;

                if (j > itemstack.getCount()) {
                    j = itemstack.getCount();
                }

                int amount = -j;
                itemstack.grow(amount);
                entityitem = new EntityItem(world, (x + f), (y + f1), (z + f2), new ItemStack(itemstack.getItem(), j, itemstack.getItemDamage()));
                float f3 = 0.05F;
                entityitem.motionX = ((float)random.nextGaussian() * f3);
                entityitem.motionY = ((float)random.nextGaussian() * f3 + 0.2F);
                entityitem.motionZ = ((float)random.nextGaussian() * f3);

                if (itemstack.hasTagCompound()) {
                    entityitem.getItem().setTagCompound(itemstack.getTagCompound().copy());
                }
                world.spawnEntity(entityitem);
            }
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

    public static void placeStackAt(EntityPlayer player, ItemStack blockStack, World world, BlockPos pos) {
        if (blockStack.getItem() instanceof ItemBlock) {
            Block block = ((ItemBlock) blockStack.getItem()).getBlock();
            IBlockState stateForPlacement = block.getStateForPlacement(world, pos, EnumFacing.UP, 0, 0, 0, blockStack.getItem().getMetadata(blockStack), player, EnumHand.MAIN_HAND);
            world.setBlockState(pos, stateForPlacement, 3);
            SoundTools.playSound(world, block.getSoundType().getPlaceSound(), pos.getX(), pos.getY(), pos.getZ(), 1.0f, 1.0f);
        } else {
            player.setPosition(pos.getX()+.5, pos.getY()+1.5, pos.getZ()+.5);
            blockStack.getItem().onItemUse(player, world, pos, EnumHand.MAIN_HAND, EnumFacing.UP, 0, 0, 0);
        }
    }
}
