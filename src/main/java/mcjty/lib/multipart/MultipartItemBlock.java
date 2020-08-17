package mcjty.lib.multipart;


import mcjty.lib.setup.Registration;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class MultipartItemBlock extends BlockItem {

    public MultipartItemBlock(Block block) {
        super(block, new Properties());
    }   // @todo 1.14

    @Override
    protected boolean canPlace(BlockItemUseContext context, BlockState state) {
        // Return true to make this work all the time.
        return true;
    }


    @Override
    public ActionResultType tryPlace(BlockItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        PlayerEntity player = context.getPlayer();

        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        ItemStack itemstack = context.getItem();
        if (itemstack.isEmpty()) {
            return ActionResultType.FAIL;
        }

        BlockState toPlace = this.getBlock().getStateForPlacement(context);
        PartSlot slot = PartSlot.NONE;
        if (this.getBlock() instanceof IPartBlock) {
            slot = ((IPartBlock) this.getBlock()).getSlotFromState(world, pos, toPlace);
        }

        if (!block.isReplaceable(toPlace, context) && !canFitInside(block, world, pos, slot)) {
            pos = pos.offset(context.getFace());
            state = world.getBlockState(pos);
            block = state.getBlock();
        }

        context = BlockItemUseContext.func_221536_a(context, pos, context.getFace());

        if (player.canPlayerEdit(pos, context.getFace(), itemstack)) {
            // We have to call getStateForPlacement again to be sure it is ok for this position as well
            toPlace = this.getBlock().getStateForPlacement(context);
            if (this.getBlock() instanceof IPartBlock) {
                slot = ((IPartBlock) this.getBlock()).getSlotFromState(world, pos, toPlace);
            }

            if (canFitInside(block, world, pos, slot)) {
                if (placeBlockAtInternal(itemstack, player, world, pos, toPlace, slot)) {
                    SoundType soundtype = toPlace.getBlock().getSoundType(toPlace, world, pos, player);
                    world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                    itemstack.shrink(1);
                }
                return ActionResultType.SUCCESS;
            } else if (true /* @todo 1.14 world.mayPlace(this.getBlock(), pos, false, facing, null)*/) {
                if (placeBlockAtInternal(itemstack, player, world, pos, toPlace, slot)) {
                    SoundType soundtype = toPlace.getBlock().getSoundType(toPlace, world, pos, player);
                    world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                    itemstack.shrink(1);
                }

                return ActionResultType.SUCCESS;
            } else {
                return ActionResultType.FAIL;
            }
        } else {
            return ActionResultType.FAIL;
        }
    }

    private boolean canFitInside(Block block, World world, BlockPos pos, PartSlot slot) {
        if (block != Registration.MULTIPART_BLOCK) {
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
//    public EnumActionResult onItemUse(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
//        ItemStack itemstack = player.getHeldItem(hand);
//        int meta = this.getMetadata(itemstack.getMetadata());
//        BlockState toPlace = block.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, player, hand);
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

    private TileEntity createTileEntity(World world, BlockState state) {
        return state.getBlock().createTileEntity(state, world);
    }

    @Override
    protected boolean placeBlock(BlockItemUseContext context, BlockState state) {
        // Not implemented
        return false;
    }

    private boolean placeBlockAtInternal(ItemStack stack, PlayerEntity player, World world, BlockPos pos, BlockState newState,
                                         @Nonnull PartSlot slot) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof MultipartTE) {
            TileEntity tileEntity = createTileEntity(world, newState);
            if (tileEntity instanceof GenericTileEntity && stack.getTag() != null) {
                // @todo how to do this?
//                ((GenericTileEntity) tileEntity).readRestorableFromNBT(stack.getTag());
            }
            ((MultipartTE) te).addPart(slot, newState, tileEntity);
            return true;
        }

        BlockState multiState = Registration.MULTIPART_BLOCK.getDefaultState();
        if (!world.setBlockState(pos, multiState, 11)) {
            return false;
        }

        BlockState state = world.getBlockState(pos);
        if (state.getBlock() == Registration.MULTIPART_BLOCK) {
            setTileEntityNBT(world, player, pos, stack);

            te = world.getTileEntity(pos);
            if (te instanceof MultipartTE) {
                TileEntity tileEntity = createTileEntity(world, newState);
                if (tileEntity instanceof GenericTileEntity && stack.hasTag()) {
                    // @todo how to do this?
//                    ((GenericTileEntity) tileEntity).readRestorableFromNBT(stack.getTag());
                }
                ((MultipartTE) te).addPart(slot, newState, tileEntity);
                return true;
            }

            newState.getBlock().onBlockPlacedBy(world, pos, newState, player, stack);

            if (player instanceof ServerPlayerEntity) {
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) player, pos, stack);
            }
        }

        return true;
    }


//    @Override
//    public boolean placeBlockAt(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, BlockState newState) {
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
//            BlockState state = world.getBlockState(pos);
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


    private RayTraceResult getMovingObjectPositionFromPlayer(World worldIn, PlayerEntity playerIn, boolean useLiquids) {
        float pitch = playerIn.rotationPitch;
        float yaw = playerIn.rotationYaw;
        double x = playerIn.getPosX();
        double y = playerIn.getPosY() + playerIn.getEyeHeight();
        double z = playerIn.getPosZ();
        Vector3d vec3 = new Vector3d(x, y, z);
        float f2 = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f3 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f4 = -MathHelper.cos(-pitch * 0.017453292F);
        float f5 = MathHelper.sin(-pitch * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double reach = 5.0D;
        if (playerIn instanceof ServerPlayerEntity) {
            // @todo 1.14
//            reach = ((ServerPlayerEntity) playerIn).interactionManager.getBlockReachDistance();
        }
        Vector3d vec31 = vec3.add(f6 * reach, f5 * reach, f7 * reach);
        RayTraceContext context = new RayTraceContext(vec3, vec31, RayTraceContext.BlockMode.COLLIDER, useLiquids ? RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE, playerIn);
        return worldIn.rayTraceBlocks(context);
    }

}
