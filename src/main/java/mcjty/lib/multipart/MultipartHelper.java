package mcjty.lib.multipart;

public class MultipartHelper {

    // @todo multipart
//    public static BlockEntity getTileEntity(BlockGetter access, BlockPos pos, PartSlot slot) {
//        BlockEntity te = access.getBlockEntity(pos);
//        if (te instanceof MultipartTE) {
//            MultipartTE.Part part = ((MultipartTE) te).getParts().get(slot);
//            if (part != null) {
//                return part.getTileEntity();
//            }
//        }
//        return null;
//    }
//
//    public static BlockEntity getTileEntity(BlockGetter access, PartPos pos) {
//        BlockEntity te = access.getBlockEntity(pos.pos());
//        if (te instanceof MultipartTE) {
//            MultipartTE.Part part = ((MultipartTE) te).getParts().get(pos.slot());
//            if (part != null) {
//                return part.getTileEntity();
//            }
//        }
//        return null;
//    }
//
//    public static BlockState getBlockState(BlockGetter access, BlockPos pos, PartSlot slot) {
//        BlockEntity te = access.getBlockEntity(pos);
//        if (te instanceof MultipartTE) {
//            MultipartTE.Part part = ((MultipartTE) te).getParts().get(slot);
//            if (part != null) {
//                return part.getState();
//            }
//        }
//        return null;
//    }
//
//    // Return true if there are no more parts left
//    public static boolean removePart(MultipartTE multipartTE, BlockState state, Player player, Vec3 hitVec) {
//        BlockPos pos = multipartTE.getBlockPos();
//        MultipartTE.Part hitPart = Registration.MULTIPART_BLOCK.getHitPart(state, multipartTE.getLevel(), pos, getPlayerEyes(player), hitVec);
//        if (hitPart == null) {
//            return false;
//        }
//
//        BlockState hitState = hitPart.getState();
//        BlockEntity hitTile = hitPart.getTileEntity();
//
//        ItemStack stack = new ItemStack(hitState.getBlock().asItem());
//        if (hitTile instanceof GenericTileEntity) {
//            CompoundTag tagCompound = new CompoundTag();
//            // @todo how to fix the restorable parts from NBT?
////            ((GenericTileEntity) hitTile).writeRestorableToNBT(tagCompound);
//            ((GenericTileEntity) hitTile).onReplaced(multipartTE.getLevel(), multipartTE.getBlockPos(), hitState, hitState); // @todo check?
//            stack.setTag(tagCompound);
//        }
//        Containers.dropItemStack(multipartTE.getLevel(), pos.getX(), pos.getY(), pos.getZ(), stack);
//
//        multipartTE.removePart(hitState);
//
//        return multipartTE.getParts().isEmpty();
//    }
//
//    private static HitResult getMovingObjectPositionFromPlayer(Level worldIn, Player playerIn, boolean useLiquids) {
//        float pitch = playerIn.getXRot();
//        float yaw = playerIn.getYRot();
//        Vec3 vec3 = getPlayerEyes(playerIn);
//        float f2 = Mth.cos(-yaw * 0.017453292F - (float) Math.PI);
//        float f3 = Mth.sin(-yaw * 0.017453292F - (float)Math.PI);
//        float f4 = -Mth.cos(-pitch * 0.017453292F);
//        float f5 = Mth.sin(-pitch * 0.017453292F);
//        float f6 = f3 * f4;
//        float f7 = f2 * f4;
//        double reach = 5.0D;
//        if (playerIn instanceof ServerPlayer) {
//            // @todo 1.14
////            reach = ((ServerPlayerEntity)playerIn).interactionManager.getBlockReachDistance();
//        }
//        Vec3 vec31 = vec3.add(f6 * reach, f5 * reach, f7 * reach);
//        ClipContext context = new ClipContext(vec3, vec31, ClipContext.Block.COLLIDER, useLiquids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, playerIn);
//        return worldIn.clip(context);
//    }
//
//    public static Vec3 getPlayerEyes(Player playerIn) {
//        double x = playerIn.getX();
//        double y = playerIn.getY() + playerIn.getEyeHeight();
//        double z = playerIn.getZ();
//        return new Vec3(x, y, z);
//    }
}
