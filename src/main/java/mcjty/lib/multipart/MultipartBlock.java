package mcjty.lib.multipart;

import mcjty.lib.McJtyLib;
import mcjty.lib.compat.theoneprobe.McJtyLibTOPDriver;
import mcjty.lib.compat.theoneprobe.TOPDriver;
import mcjty.lib.compat.theoneprobe.TOPInfoProvider;
import mcjty.lib.compat.waila.WailaInfoProvider;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;

public class MultipartBlock extends Block implements WailaInfoProvider, TOPInfoProvider, ITileEntityProvider {

    public static final AxisAlignedBB AABB_EMPTY = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    public static final AxisAlignedBB AABB_CENTER = new AxisAlignedBB(.4, .4, .4, .6, .6, .6);

    public MultipartBlock() {
        super(Block.Properties.create(Material.IRON)
                .hardnessAndResistance(2.0f)
                .notSolid()
                .harvestLevel(0)
                .harvestTool(ToolType.PICKAXE)
                .sound(SoundType.METAL));
        setRegistryName(McJtyLib.MODID, "multipart");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new MultipartTE();
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        MultipartTE.Part part = getHitPart(state, world, pos, player.getEyePosition(0), target.getHitVec());
        if (part != null) {
            return new ItemStack(Item.getItemFromBlock(part.getState().getBlock()));
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return combinePartShapes(world, pos, s -> s.getCollisionShape(world, pos, context));
    }

    @Override
    public VoxelShape getRaytraceShape(BlockState state, IBlockReader world, BlockPos pos) {
        return combinePartShapes(world, pos, s -> s.getRaytraceShape(world, pos));
    }

    @Override
    public VoxelShape getRenderShape(BlockState state, IBlockReader world, BlockPos pos) {
        return combinePartShapes(world, pos, s -> s.getRenderShape(world, pos));
    }

    private VoxelShape combinePartShapes(IBlockReader world, BlockPos pos, Function<BlockState, VoxelShape> shapeGetter) {
        VoxelShape combined = VoxelShapes.empty();
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof MultipartTE) {
            MultipartTE multipartTE = (MultipartTE) te;
            for (Map.Entry<PartSlot, MultipartTE.Part> entry : multipartTE.getParts().entrySet()) {
                MultipartTE.Part part = entry.getValue();
                VoxelShape shape = shapeGetter.apply(part.getState());
                if (combined.isEmpty()) {
                    combined = shape;
                } else {
                    combined = VoxelShapes.combineAndSimplify(combined, shape, IBooleanFunction.OR);
                }
            }
        }
        return combined;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        Vec3d hit = result.getHitVec();
        Direction facing = result.getFace();
        Vec3d start = MultipartHelper.getPlayerEyes(player);
        Vec3d end = new Vec3d(start.x + (pos.getX() + hit.x - start.x) * 3, start.y + (pos.getY() + hit.y - start.y) * 3, start.z + (pos.getZ() + hit.z - start.z) * 3);
        MultipartTE.Part part = getHitPart(state, world, pos, start, end);
        if (part != null) {
            if (part.getTileEntity() instanceof GenericTileEntity) {
                return ((GenericTileEntity) part.getTileEntity()).onBlockActivated(part.getState(), player, hand, result);
            } else {
                return part.getState().getBlock().onBlockActivated(part.getState(), world, pos, player, hand, result);
            }
        }
        return ActionResultType.PASS;
    }

    @Nullable
    public BlockState getHitState(BlockState blockState, World world, BlockPos pos, Vec3d start, Vec3d end) {
        MultipartTE.Part part = getHitPart(blockState, world, pos, start, end);
        if (part != null) {
            return part.getState();
        } else {
            return null;
        }
    }

    @Nullable
    public static MultipartTE.Part getHitPart(BlockState blockState, IBlockReader world, BlockPos pos, Vec3d start, Vec3d end) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof MultipartTE) {
            MultipartTE multipartTE = (MultipartTE) te;
            for (Map.Entry<PartSlot, MultipartTE.Part> entry : multipartTE.getParts().entrySet()) {
                MultipartTE.Part part = entry.getValue();
                if (!(part.getState().getBlock() instanceof MultipartBlock)) {     // @todo safety
                    // @todo 1.14
                    BlockRayTraceResult result = part.getState().getRaytraceShape(world, pos).rayTrace(start, end, pos);
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
}
