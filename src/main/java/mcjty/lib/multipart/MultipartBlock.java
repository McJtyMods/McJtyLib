package mcjty.lib.multipart;

import mcjty.lib.McJtyLib;
import mcjty.lib.McJtyRegister;
import mcjty.lib.compat.theoneprobe.TOPInfoProvider;
import mcjty.lib.compat.waila.WailaInfoProvider;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class MultipartBlock extends Block implements WailaInfoProvider, TOPInfoProvider, ITileEntityProvider {

    public static final AxisAlignedBB AABB_EMPTY = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    public static final AxisAlignedBB AABB_CENTER = new AxisAlignedBB(.4, .4, .4, .6, .6, .6);

    public static final PartsProperty PARTS = new PartsProperty("parts");

    public MultipartBlock() {
        super(Material.IRON);
        setUnlocalizedName(McJtyLib.PROVIDES + "." + "multipart");
        setRegistryName("multipart");
        McJtyRegister.registerLater(this, McJtyLib.instance, MultipartItemBlock::new);
        setHardness(2.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 0);
        // @todo TEMPORARY!
        setCreativeTab(CreativeTabs.MISC);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new MultipartTE();
    }

    public void initModel() {
        McJtyLib.proxy.initStandardItemModel(this);
        McJtyLib.proxy.initStateMapper(this, MultipartBakedModel.MODEL);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        IProperty[] listedProperties = new IProperty[0]; // no listed properties
        IUnlistedProperty[] unlistedProperties = new IUnlistedProperty[] { PARTS };
        return new ExtendedBlockState(this, listedProperties, unlistedProperties);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return true; // delegated to GenericCableBakedModel#getQuads
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof MultipartTE) {
            MultipartTE multipartTE = (MultipartTE) te;
            for (Map.Entry<PartSlot, MultipartTE.Part> entry : multipartTE.getParts().entrySet()) {
                MultipartTE.Part part = entry.getValue();
                addCollisionBoxToList(pos, entityBox, collidingBoxes, part.getState().getCollisionBoundingBox(world, pos));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return AABB_EMPTY;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true;
    }


    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        MultipartTE.Part part = getHitPart(state, world, pos, MultipartHelper.getPlayerEyes(player), new Vec3d(pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ));
        if (part != null) {
            if (part.getTileEntity() instanceof GenericTileEntity) {
                return ((GenericTileEntity) part.getTileEntity()).onBlockActivated(part.getState(), player, hand, facing, hitX, hitY, hitZ);
            } else {
                return part.getState().getBlock().onBlockActivated(world, pos, part.getState(), player, hand, facing, hitX, hitY, hitZ);
            }
        }
        return false;
    }

    @Nullable
    public IBlockState getHitState(IBlockState blockState, World world, BlockPos pos, Vec3d start, Vec3d end) {
        MultipartTE.Part part = getHitPart(blockState, world, pos, start, end);
        if (part != null) {
            return part.getState();
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public RayTraceResult collisionRayTrace(IBlockState blockState, World world, BlockPos pos, Vec3d start, Vec3d end) {
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
    public MultipartTE.Part getHitPart(IBlockState blockState, World world, BlockPos pos, Vec3d start, Vec3d end) {
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
    public boolean isBlockNormalCube(IBlockState blockState) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @Optional.Method(modid = "theoneprobe")
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        MultipartTE.Part part = getHitPart(blockState, world, data.getPos(), MultipartHelper.getPlayerEyes(player), data.getHitVec());
        if (part != null) {
            if (part.getTileEntity() instanceof GenericTileEntity) {
                ((GenericTileEntity) part.getTileEntity()).addProbeInfo(mode, probeInfo, player, world, blockState, data);
            } else if (part.getState().getBlock() instanceof TOPInfoProvider) {
                ((TOPInfoProvider) part.getState().getBlock()).addProbeInfo(mode, probeInfo, player, world, blockState, data);
            }
        }
    }

    @Override
    @Optional.Method(modid = "waila")
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        MultipartTE.Part part = getHitPart(accessor.getBlockState(), accessor.getWorld(), accessor.getPosition(),
                MultipartHelper.getPlayerEyes(accessor.getPlayer()), accessor.getRenderingPosition());
        if (part != null) {
            if (part.getTileEntity() instanceof GenericTileEntity) {
                ((GenericTileEntity) part.getTileEntity()).addWailaBody(itemStack, currenttip, accessor, config);
            } else if (part.getState().getBlock() instanceof WailaInfoProvider) {
                return ((WailaInfoProvider) part.getState().getBlock()).getWailaBody(itemStack, currenttip, accessor, config);
            }
        }
        return currenttip;
    }


    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof MultipartTE) {
            MultipartTE multipartTE = (MultipartTE) te;
            return extendedBlockState.withProperty(PARTS, multipartTE.getParts());
        }
        return extendedBlockState;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }
}
