package mcjty.lib.multipart;

import mcjty.lib.McJtyLib;
import mcjty.lib.McJtyRegister;
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
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;

public class MultipartBlock extends Block implements WailaInfoProvider, TOPInfoProvider, ITileEntityProvider {

    public static final AxisAlignedBB AABB_EMPTY = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    public static final AxisAlignedBB AABB_CENTER = new AxisAlignedBB(.4, .4, .4, .6, .6, .6);

    public static final PartsProperty PARTS = new PartsProperty("parts");

    public MultipartBlock() {
        super(Block.Properties.create(Material.IRON)
            .hardnessAndResistance(2.0f)
            .sound(SoundType.METAL));
        setRegistryName(McJtyLib.MODID, "multipart");
        McJtyRegister.registerLater(this, McJtyLib.instance, MultipartItemBlock::new);
//        setHarvestLevel("pickaxe", 0);    // @todo 1.14
        // @todo TEMPORARY!
//        setCreativeTab(CreativeTabs.MISC);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new MultipartTE();
    }

    public void initModel() {
        McJtyLib.proxy.initStandardItemModel(this);
        McJtyLib.proxy.initStateMapper(this, MultipartBakedModel.MODEL);
    }


    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        // @todo 1.14
//        IProperty[] listedProperties = new IProperty[0]; // no listed properties
//        IUnlistedProperty[] unlistedProperties = new IUnlistedProperty[] { PARTS };
//        return new ExtendedBlockState(this, listedProperties, unlistedProperties);
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
    public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer) {
        return true; // delegated to GenericCableBakedModel#getQuads
    }

    // @todo 1.14
//    @Override
//    public void addCollisionBoxToList(BlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
//        TileEntity te = world.getTileEntity(pos);
//        if (te instanceof MultipartTE) {
//            MultipartTE multipartTE = (MultipartTE) te;
//            for (Map.Entry<PartSlot, MultipartTE.Part> entry : multipartTE.getParts().entrySet()) {
//                MultipartTE.Part part = entry.getValue();
//                addCollisionBoxToList(pos, entityBox, collidingBoxes, part.getState().getCollisionBoundingBox(world, pos));
//            }
//        }
//    }
//    @Override
//    public AxisAlignedBB getSelectedBoundingBox(BlockState state, World worldIn, BlockPos pos) {
//        return AABB_EMPTY;
//    }
//    @Override
//    public boolean shouldSideBeRendered(BlockState blockState, IBlockAccess blockAccess, BlockPos pos, Direction side) {
//        return true;
//    }


    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
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
        return false;
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
    @Override
    public RayTraceResult collisionRayTrace(BlockState blockState, World world, BlockPos pos, Vec3d start, Vec3d end) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof MultipartTE) {
            MultipartTE multipartTE = (MultipartTE) te;
            for (Map.Entry<PartSlot, MultipartTE.Part> entry : multipartTE.getParts().entrySet()) {
                MultipartTE.Part part = entry.getValue();
                if (!(part.getState().getBlock() instanceof MultipartBlock)) {     // @todo safety
                    RayTraceResult result = part.getState().collisionRayTrace(world, pos, start, end);
                    if (result != null) {
                        return result;
                    }
                }
            }
            return null;
        } else {
            return super.collisionRayTrace(blockState, world, pos, start, end);
        }
    }

    @Nullable
    public MultipartTE.Part getHitPart(BlockState blockState, IBlockReader world, BlockPos pos, Vec3d start, Vec3d end) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof MultipartTE) {
            MultipartTE multipartTE = (MultipartTE) te;
            for (Map.Entry<PartSlot, MultipartTE.Part> entry : multipartTE.getParts().entrySet()) {
                MultipartTE.Part part = entry.getValue();
                if (!(part.getState().getBlock() instanceof MultipartBlock)) {     // @todo safety
                    RayTraceResult result = part.getState().collisionRayTrace(world, pos, start, end);
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

    private RayTraceResult checkIntersect(BlockPos pos, Vec3d vec3d, Vec3d vec3d1, AxisAlignedBB boundingBox) {
        RayTraceResult raytraceresult = boundingBox.calculateIntercept(vec3d, vec3d1);
        return raytraceresult == null ? null : new RayTraceResult(raytraceresult.hitVec.addVector(pos.getX(), pos.getY(), pos.getZ()), raytraceresult.sideHit, pos);
    }

    @Override
    public boolean isBlockNormalCube(BlockState blockState) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(BlockState blockState) {
        return false;
    }

    @Override
    public boolean isFullCube(BlockState state) {
        return false;
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


    @Override
    public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos) {
//        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
//
//        TileEntity te = world.getTileEntity(pos);
//        if (te instanceof MultipartTE) {
//            MultipartTE multipartTE = (MultipartTE) te;
//            return extendedBlockState.withProperty(PARTS, multipartTE.getParts());
//        }
//        return extendedBlockState;
        // @todo 1.14
        return state;
    }
}
