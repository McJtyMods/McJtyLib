package mcjty.lib.multipart;

import mcjty.lib.proxy.CommonProxy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
    public static boolean hit(MultipartTE multipartTE, IBlockState state, EntityPlayer player, Vec3d hitVec) {
//        RayTraceResult result = getMovingObjectPositionFromPlayer(multipartTE.getWorld(), player, false);
//        if (result == null) {
//            return;
//        }
        IBlockState hitPart = CommonProxy.multipartBlock.getHitPart(state, multipartTE.getWorld(), multipartTE.getPos(), getPlayerEyes(player), hitVec);
        if (hitPart == null) {
            return false;
        }
        multipartTE.removePart(hitPart);
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

    private static Vec3d getPlayerEyes(EntityPlayer playerIn) {
        double x = playerIn.posX;
        double y = playerIn.posY + playerIn.getEyeHeight();
        double z = playerIn.posZ;
        return new Vec3d(x, y, z);
    }


}
