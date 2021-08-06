package mcjty.lib.multipart;

import mcjty.lib.McJtyLib;
import mcjty.lib.compat.theoneprobe.McJtyLibTOPDriver;
import mcjty.lib.compat.theoneprobe.TOPDriver;
import mcjty.lib.compat.theoneprobe.TOPInfoProvider;
import mcjty.lib.compat.waila.WailaInfoProvider;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;

public class MultipartBlock extends Block implements WailaInfoProvider, TOPInfoProvider, EntityBlock {

    public static final AABB AABB_EMPTY = new AABB(0, 0, 0, 0, 0, 0);
    public static final AABB AABB_CENTER = new AABB(.4, .4, .4, .6, .6, .6);

    public MultipartBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL)
                .strength(2.0f)
                .noOcclusion()
                .harvestLevel(0)
                .harvestTool(ToolType.PICKAXE)
                .sound(SoundType.METAL));
        setRegistryName(McJtyLib.MODID, "multipart");
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new MultipartTE(blockPos, blockState);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        MultipartTE.Part part = getHitPart(state, world, pos, player.getEyePosition(0), target.getLocation());
        if (part != null) {
            return new ItemStack(part.getState().getBlock().asItem());
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter worldIn, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return combinePartShapes(world, pos, s -> s.getShape(world, pos, context));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return combinePartShapes(world, pos, s -> s.getCollisionShape(world, pos, context));
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return combinePartShapes(world, pos, s -> s.getVisualShape(world, pos, CollisionContext.empty()));
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return combinePartShapes(world, pos, s -> s.getOcclusionShape(world, pos));
    }

    private VoxelShape combinePartShapes(BlockGetter world, BlockPos pos, Function<BlockState, VoxelShape> shapeGetter) {
        VoxelShape combined = Shapes.empty();
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof MultipartTE) {
            MultipartTE multipartTE = (MultipartTE) te;
            for (Map.Entry<PartSlot, MultipartTE.Part> entry : multipartTE.getParts().entrySet()) {
                MultipartTE.Part part = entry.getValue();
                VoxelShape shape = shapeGetter.apply(part.getState());
                if (combined.isEmpty()) {
                    combined = shape;
                } else {
                    combined = Shapes.join(combined, shape, BooleanOp.OR);
                }
            }
        }
        return combined;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        Vec3 hit = result.getLocation();
        Direction facing = result.getDirection();
        Vec3 start = MultipartHelper.getPlayerEyes(player);
        Vec3 end = new Vec3(start.x + (pos.getX() + hit.x - start.x) * 3, start.y + (pos.getY() + hit.y - start.y) * 3, start.z + (pos.getZ() + hit.z - start.z) * 3);
        MultipartTE.Part part = getHitPart(state, world, pos, start, end);
        if (part != null) {
            if (part.getTileEntity() instanceof GenericTileEntity) {
                return ((GenericTileEntity) part.getTileEntity()).onBlockActivated(part.getState(), player, hand, result);
            } else {
                return part.getState().getBlock().use(part.getState(), world, pos, player, hand, result);
            }
        }
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof MultipartTE) {
            ((MultipartTE) te).dump();
        }
        return InteractionResult.CONSUME;
    }

    @Nullable
    public BlockState getHitState(BlockState blockState, Level world, BlockPos pos, Vec3 start, Vec3 end) {
        MultipartTE.Part part = getHitPart(blockState, world, pos, start, end);
        if (part != null) {
            return part.getState();
        } else {
            return null;
        }
    }

    @Nullable
    public static MultipartTE.Part getHitPart(BlockState blockState, BlockGetter world, BlockPos pos, Vec3 start, Vec3 end) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof MultipartTE) {
            MultipartTE multipartTE = (MultipartTE) te;
            for (Map.Entry<PartSlot, MultipartTE.Part> entry : multipartTE.getParts().entrySet()) {
                MultipartTE.Part part = entry.getValue();
                if (!(part.getState().getBlock() instanceof MultipartBlock)) {     // @todo safety
                    // @todo 1.14
                    BlockHitResult result = part.getState().getVisualShape(world, pos, CollisionContext.empty()).clip(start, end, pos);
                    if (result != null) {
                        return part;
                    }
                }
            }
            return null;
        } else {
            return null;
        }
    }

    @Override
    public TOPDriver getProbeDriver() {
        return McJtyLibTOPDriver.DRIVER;
    }


    // @todo 1.14
//    @Override
//    @Optional.Method(modid = "theoneprobe")
//    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
//        MultipartTE.Part part = getHitPart(blockState, world, data.getPos(), MultipartHelper.getPlayerEyes(player), data.getHitVec());
//        if (part != null) {
//            if (part.getTileEntity() instanceof GenericTileEntity) {
//                ((GenericTileEntity) part.getTileEntity()).addProbeInfo(mode, probeInfo, player, world, blockState, data);
//            } else if (part.getState().getBlock() instanceof TOPInfoProvider) {
//                ((TOPInfoProvider) part.getState().getBlock()).addProbeInfo(mode, probeInfo, player, world, blockState, data);
//            }
//        }
//    }
//
//    @Override
//    @Optional.Method(modid = "waila")
//    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
//        MultipartTE.Part part = getHitPart(accessor.getBlockState(), accessor.getWorld(), accessor.getPosition(),
//                MultipartHelper.getPlayerEyes(accessor.getPlayer()), accessor.getRenderingPosition());
//        if (part != null) {
//            if (part.getTileEntity() instanceof GenericTileEntity) {
//                ((GenericTileEntity) part.getTileEntity()).addWailaBody(itemStack, currenttip, accessor, config);
//            } else if (part.getState().getBlock() instanceof WailaInfoProvider) {
//                return ((WailaInfoProvider) part.getState().getBlock()).getWailaBody(itemStack, currenttip, accessor, config);
//            }
//        }
//        return currenttip;
//    }


    // @todo 1.16
//    @Override
//    public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos) {
//        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
//
//        TileEntity te = world.getTileEntity(pos);
//        if (te instanceof MultipartTE) {
//            MultipartTE multipartTE = (MultipartTE) te;
//            return extendedBlockState.withProperty(PARTS, multipartTE.getParts());
//        }
//        return extendedBlockState;
        // @todo 1.14
//        return state;
//    }
}
