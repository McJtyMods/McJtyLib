package mcjty.lib.multipart;


import mcjty.lib.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack itemstack = player.getHeldItem(hand);
        int meta = this.getMetadata(itemstack.getMetadata());
        IBlockState toPlace = block.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, player, hand);
        if (onItemUseHelper(player, world, pos, toPlace)) {
            return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        } else {
            return EnumActionResult.FAIL;
        }
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!world.isRemote) {
            Block block1 = CommonProxy.multipartBlock;
            boolean placed = false;
            if (world.getBlockState(pos).getBlock() != block1) {
                placed = true;
                if (!world.setBlockState(pos, block1.getDefaultState(), 3)) {
                    return false;
                }
            }

            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() == block1) {
                if (placed) {
                    block1.onBlockPlacedBy(world, pos, state, player, stack);
                }

//                addCable(world, pos, side, hitX, hitY, hitZ);
            }
        }

        return true;
    }


    private boolean onItemUseHelper(EntityPlayer player, World world, BlockPos pos, IBlockState toPlace) {
        if (!world.isRemote) {
            Block block = world.getBlockState(pos).getBlock();
            if (block == CommonProxy.multipartBlock) {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof MultipartTE) {
                    MultipartTE multipartTE = (MultipartTE) te;
                    if (multipartTE.testIntersect(toPlace)) {
                        // There is a collision. We can't place. Fallback to the normal method of placing adjacent to this block
                        return true;
                    } else {
                        multipartTE.addPart(toPlace);
                    }
                }
            }
        }
        return true;
    }

    private RayTraceResult getMovingObjectPositionFromPlayer(World worldIn, EntityPlayer playerIn, boolean useLiquids) {
        float pitch = playerIn.rotationPitch;
        float yaw = playerIn.rotationYaw;
        double x = playerIn.posX;
        double y = playerIn.posY + playerIn.getEyeHeight();
        double z = playerIn.posZ;
        Vec3d vec3 = new Vec3d(x, y, z);
        float f2 = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f3 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f4 = -MathHelper.cos(-pitch * 0.017453292F);
        float f5 = MathHelper.sin(-pitch * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double reach = 5.0D;
        if (playerIn instanceof EntityPlayerMP) {
            reach = ((EntityPlayerMP)playerIn).interactionManager.getBlockReachDistance();
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

            vector = new Vec3d(pos.getX()+.5f, pos.getY()+.5f, pos.getZ()+.5f);
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
    }}
