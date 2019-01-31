package mcjty.lib.multipart;

import mcjty.lib.proxy.CommonProxy;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.BlockTools;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class MultipartHelper {

    public static TileEntity getTileEntity(IBlockAccess access, BlockPos pos, PartSlot slot) {
        TileEntity te = access.getTileEntity(pos);
        if (te instanceof MultipartTE) {
            MultipartTE.Part part = ((MultipartTE) te).getParts().get(slot);
            if (part != null) {
                return part.getTileEntity();
            }
        }
        return null;
    }

    // Return true if there are no more parts left
    public static boolean removePart(MultipartTE multipartTE, IBlockState state, EntityPlayer player, Vec3d hitVec) {
        BlockPos pos = multipartTE.getPos();
        MultipartTE.Part hitPart = CommonProxy.multipartBlock.getHitPart(state, multipartTE.getWorld(), pos, getPlayerEyes(player), hitVec);
        if (hitPart == null) {
            return false;
        }

        IBlockState hitState = hitPart.getState();
        TileEntity hitTile = hitPart.getTileEntity();

        ItemStack stack = new ItemStack(Item.getItemFromBlock(hitState.getBlock()));
        if (hitTile instanceof GenericTileEntity) {
            NBTTagCompound tagCompound = new NBTTagCompound();
            ((GenericTileEntity) hitTile).writeRestorableToNBT(tagCompound);
            stack.setTagCompound(tagCompound);
        }
        BlockTools.spawnItemStack(multipartTE.getWorld(), pos.getX(), pos.getY(), pos.getZ(), stack);

        multipartTE.removePart(hitState);

        return multipartTE.getParts().isEmpty();
    }

    private static RayTraceResult getMovingObjectPositionFromPlayer(World worldIn, EntityPlayer playerIn, boolean useLiquids) {
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
        if (playerIn instanceof net.minecraft.entity.player.EntityPlayerMP) {
            reach = ((EntityPlayerMP)playerIn).interactionManager.getBlockReachDistance();
        }
        Vec3d vec31 = vec3.addVector(f6 * reach, f5 * reach, f7 * reach);
        return worldIn.rayTraceBlocks(vec3, vec31, useLiquids, !useLiquids, false);
    }

    public static Vec3d getPlayerEyes(EntityPlayer playerIn) {
        double x = playerIn.posX;
        double y = playerIn.posY + playerIn.getEyeHeight();
        double z = playerIn.posZ;
        return new Vec3d(x, y, z);
    }


}
