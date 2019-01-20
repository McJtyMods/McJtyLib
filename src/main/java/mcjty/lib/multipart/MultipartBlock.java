package mcjty.lib.multipart;

import mcjty.lib.McJtyLib;
import mcjty.lib.McJtyRegister;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class MultipartBlock extends Block implements ITileEntityProvider {

    public static final AxisAlignedBB AABB_EMPTY = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    public static final AxisAlignedBB AABB_CENTER = new AxisAlignedBB(.4, .4, .4, .6, .6, .6);

    public static final PartsProperty PARTS = new PartsProperty("parts");

    public MultipartBlock() {
        super(Material.IRON);
        setUnlocalizedName(McJtyLib.PROVIDES + "." + "multipart");
        setRegistryName("multipart");
        McJtyRegister.registerLater(this, McJtyLib.instance, MultipartItemBlock::new);
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
            for (PartBlockId part : multipartTE.getParts()) {
                addCollisionBoxToList(pos, entityBox, collidingBoxes, part.getBlockState().getCollisionBoundingBox(world, pos));
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


    @Nullable
    @Override
    public RayTraceResult collisionRayTrace(IBlockState blockState, World world, BlockPos pos, Vec3d start, Vec3d end) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof MultipartTE) {
            MultipartTE multipartTE = (MultipartTE) te;
            for (PartBlockId part : multipartTE.getParts()) {
                RayTraceResult result = part.getBlockState().collisionRayTrace(world, pos, start, end);
                if (result != null) {
                    return result;
                }
            }
            return null;
        } else {
            return super.collisionRayTrace(blockState, world, pos, start, end);
        }


//        Vec3d vec3d = start.subtract(pos.getX(), pos.getY(), pos.getZ());
//        Vec3d vec3d1 = end.subtract(pos.getX(), pos.getY(), pos.getZ());
//
//
//
//        RayTraceResult rc = checkIntersect(pos, vec3d, vec3d1, AABB_CENTER);
//        if (rc != null) {
//            return rc;
//        }
//
//
//        return super.collisionRayTrace(blockState, world, pos, start, end);
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
