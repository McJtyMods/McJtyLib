package mcjty.lib.multipart;

import mcjty.lib.setup.Registration;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.BlockTools;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class MultipartHelper {

    public static TileEntity getTileEntity(IBlockReader access, BlockPos pos, PartSlot slot) {
        TileEntity te = access.getTileEntity(pos);
        if (te instanceof MultipartTE) {
            MultipartTE.Part part = ((MultipartTE) te).getParts().get(slot);
            if (part != null) {
                return part.getTileEntity();
            }
        }
        return null;
    }

    public static TileEntity getTileEntity(IBlockReader access, PartPos pos) {
        TileEntity te = access.getTileEntity(pos.getPos());
        if (te instanceof MultipartTE) {
            MultipartTE.Part part = ((MultipartTE) te).getParts().get(pos.getSlot());
            if (part != null) {
                return part.getTileEntity();
            }
        }
        return null;
    }

    public static BlockState getBlockState(IBlockReader access, BlockPos pos, PartSlot slot) {
        TileEntity te = access.getTileEntity(pos);
        if (te instanceof MultipartTE) {
            MultipartTE.Part part = ((MultipartTE) te).getParts().get(slot);
            if (part != null) {
                return part.getState();
            }
        }
        return null;
    }

    // Return true if there are no more parts left
    public static boolean removePart(MultipartTE multipartTE, BlockState state, PlayerEntity player, Vec3d hitVec) {
        BlockPos pos = multipartTE.getPos();
        MultipartTE.Part hitPart = Registration.multipartBlock.getHitPart(state, multipartTE.getWorld(), pos, getPlayerEyes(player), hitVec);
        if (hitPart == null) {
            return false;
        }

        BlockState hitState = hitPart.getState();
        TileEntity hitTile = hitPart.getTileEntity();

        ItemStack stack = new ItemStack(Item.getItemFromBlock(hitState.getBlock()));
        if (hitTile instanceof GenericTileEntity) {
            CompoundNBT tagCompound = new CompoundNBT();
            ((GenericTileEntity) hitTile).writeRestorableToNBT(tagCompound);
            ((GenericTileEntity) hitTile).onReplaced(multipartTE.getWorld(), multipartTE.getPos(), hitState);
            stack.setTag(tagCompound);
        }
        BlockTools.spawnItemStack(multipartTE.getWorld(), pos.getX(), pos.getY(), pos.getZ(), stack);

        multipartTE.removePart(hitState);

        return multipartTE.getParts().isEmpty();
    }

    private static RayTraceResult getMovingObjectPositionFromPlayer(World worldIn, PlayerEntity playerIn, boolean useLiquids) {
        float pitch = playerIn.rotationPitch;
        float yaw = playerIn.rotationYaw;
        Vec3d vec3 = getPlayerEyes(playerIn);
        float f2 = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f3 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f4 = -MathHelper.cos(-pitch * 0.017453292F);
        float f5 = MathHelper.sin(-pitch * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double reach = 5.0D;
        if (playerIn instanceof ServerPlayerEntity) {
            // @todo 1.14
//            reach = ((ServerPlayerEntity)playerIn).interactionManager.getBlockReachDistance();
        }
        Vec3d vec31 = vec3.add(f6 * reach, f5 * reach, f7 * reach);
        RayTraceContext context = new RayTraceContext(vec3, vec31, RayTraceContext.BlockMode.COLLIDER, useLiquids ? RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE, playerIn);
        return worldIn.rayTraceBlocks(context);
    }

    public static Vec3d getPlayerEyes(PlayerEntity playerIn) {
        double x = playerIn.posX;
        double y = playerIn.posY + playerIn.getEyeHeight();
        double z = playerIn.posZ;
        return new Vec3d(x, y, z);
    }


}
