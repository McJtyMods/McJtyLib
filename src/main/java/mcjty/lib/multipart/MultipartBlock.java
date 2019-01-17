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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class MultipartBlock extends Block implements ITileEntityProvider {

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
