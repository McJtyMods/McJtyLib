package mcjty.lib.multipart;


import mcjty.lib.proxy.CommonProxy;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

public class MultipartItemBlock extends ItemBlock {

    public MultipartItemBlock(Block block) {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
        // Return true to make this work all the time.
        return true;
    }


    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState iblockstate = world.getBlockState(pos);
        Block block = iblockstate.getBlock();

        ItemStack itemstack = player.getHeldItem(hand);
        if (itemstack.isEmpty()) {
            return EnumActionResult.FAIL;
        }

        int meta = this.getMetadata(itemstack.getMetadata());
        IBlockState toPlace = this.block.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, player, hand);
        PartSlot slot = PartSlot.NONE;
        if (this.block instanceof IPartBlock) {
            slot = ((IPartBlock) this.block).getSlotFromState(world, pos, toPlace);
        }

        if (!block.isReplaceable(world, pos) && !canFitInside(block, world, pos, slot)) {
            pos = pos.offset(facing);
            iblockstate = world.getBlockState(pos);
            block = iblockstate.getBlock();
        }

        if (player.canPlayerEdit(pos, facing, itemstack)) {
            // We have to call getStateForPlacement again to be sure it is ok for this position as well
            toPlace = this.block.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, player, hand);
            if (this.block instanceof IPartBlock) {
                slot = ((IPartBlock) this.block).getSlotFromState(world, pos, toPlace);
            }

            if (canFitInside(block, world, pos, slot)) {
                if (placeBlockAtInternal(itemstack, player, world, pos, facing, hitX, hitY, hitZ, toPlace, slot)) {
                    SoundType soundtype = toPlace.getBlock().getSoundType(toPlace, world, pos, player);
                    world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                    itemstack.shrink(1);
                }
                return EnumActionResult.SUCCESS;
            } else if (world.mayPlace(this.block, pos, false, facing, null)) {
                if (placeBlockAtInternal(itemstack, player, world, pos, facing, hitX, hitY, hitZ, toPlace, slot)) {
                    SoundType soundtype = toPlace.getBlock().getSoundType(toPlace, world, pos, player);
                    world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                    itemstack.shrink(1);
                }

                return EnumActionResult.SUCCESS;
            } else {
                return EnumActionResult.FAIL;
            }
        } else {
            return EnumActionResult.FAIL;
        }
    }

    private boolean canFitInside(Block block, World world, BlockPos pos, PartSlot slot) {
        if (block != CommonProxy.multipartBlock) {
            return false;
        }
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof MultipartTE) {
            MultipartTE.Part part = ((MultipartTE) te).getParts().get(slot);
            return part == null;
        } else {
            return false;
        }
    }

//
//    @Override
//    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
//        ItemStack itemstack = player.getHeldItem(hand);
//        int meta = this.getMetadata(itemstack.getMetadata());
//        IBlockState toPlace = block.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, player, hand);
//        boolean result = onItemUseHelper(player, world, pos, toPlace);
//
//
//
//        if (result) {
//            return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
//        } else {
//            return EnumActionResult.FAIL;
//        }
//    }

    private TileEntity createTileEntity(World world, IBlockState state) {
        return state.getBlock().createTileEntity(world, state);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        // Not implemented
        return false;
    }

    private boolean placeBlockAtInternal(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState,
                                         @Nonnull PartSlot slot) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof MultipartTE) {
            ((MultipartTE) te).addPart(slot, newState, createTileEntity(world, newState));
            return true;
        }

        IBlockState multiState = CommonProxy.multipartBlock.getDefaultState();
        if (!world.setBlockState(pos, multiState, 11)) {
            return false;
        }

        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == CommonProxy.multipartBlock) {
            setTileEntityNBT(world, player, pos, stack);

            te = world.getTileEntity(pos);
            if (te instanceof MultipartTE) {
                ((MultipartTE) te).addPart(slot, newState, createTileEntity(world, newState));
                return true;
            }

            newState.getBlock().onBlockPlacedBy(world, pos, newState, player, stack);

            if (player instanceof EntityPlayerMP) {
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, stack);
            }
        }

        return true;
    }


//    @Override
//    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
//        if (!world.isRemote) {
//            Block block1 = CommonProxy.multipartBlock;
//            boolean placed = false;
//            if (world.getBlockState(pos).getBlock() != block1) {
//                placed = true;
//                if (!world.setBlockState(pos, block1.getDefaultState(), 3)) {
//                    return false;
//                }
//            }
//
//            IBlockState state = world.getBlockState(pos);
//            if (state.getBlock() == block1) {
//                if (placed) {
//                    block1.onBlockPlacedBy(world, pos, state, player, stack);
//                }
//
////                addCable(world, pos, side, hitX, hitY, hitZ);
//            }
//        }
//
//        return true;
//    }


    private RayTraceResult getMovingObjectPositionFromPlayer(World worldIn, EntityPlayer playerIn, boolean useLiquids) {
        float pitch = playerIn.rotationPitch;
        float yaw = playerIn.rotationYaw;
        double x = playerIn.posX;
        double y = playerIn.posY + playerIn.getEyeHeight();
        double z = playerIn.posZ;
        Vec3d vec3 = new Vec3d(x, y, z);
        float f2 = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f3 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f4 = -MathHelper.cos(-pitch * 0.017453292F);
        float f5 = MathHelper.sin(-pitch * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double reach = 5.0D;
        if (playerIn instanceof EntityPlayerMP) {
            reach = ((EntityPlayerMP) playerIn).interactionManager.getBlockReachDistance();
        }
        Vec3d vec31 = vec3.addVector(f6 * reach, f5 * reach, f7 * reach);
        return worldIn.rayTraceBlocks(vec3, vec31, useLiquids, !useLiquids, false);
    }


    /*
     * Add a new cable to a bundle adjacent to the given coordinate.
     */
    private void addCable(World world, BlockPos pos, EnumFacing directionHit, float hitX, float hitY, float hitZ) {
        BlockPos adjacentC = pos.offset(directionHit.getOpposite());
        Vec3d vector;
        if (world.isSideSolid(adjacentC, directionHit)) {
            vector = new Vec3d(adjacentC.getX() + hitX + directionHit.getDirectionVec().getX() / 10.0f, adjacentC.getY() + hitY + directionHit.getDirectionVec().getY() / 10.0f, adjacentC.getZ() + hitZ + directionHit.getDirectionVec().getZ() / 10.0f);
        } else {
            Set<Integer> excluded = Collections.emptySet();

            vector = new Vec3d(pos.getX() + .5f, pos.getY() + .5f, pos.getZ() + .5f);
//            Optional<BundleTE> bundleTE = BlockTools.getTE(BundleTE.class, world, adjacentC);
//            if (bundleTE.isPresent()) {
//                CableSection connectableSection = bundleTE.get().findConnectableSection(type, subType, excluded);
//                if (connectableSection != null) {
//                    vector = connectableSection.getVector().addVector(directionHit.getDirectionVec().getX(), directionHit.getDirectionVec().getY(), directionHit.getDirectionVec().getZ());
//                }
//            }
        }

        final Vec3d finalVector = vector;
//        BlockTools.getTE(BundleTE.class, world, pos).ifPresent(p -> p.addCableToNetwork(type, subType, finalVector));
    }
}
