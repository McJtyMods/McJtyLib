package mcjty.lib.container;

import mcjty.lib.McJtyRegister;
import mcjty.lib.base.ModBase;
import mcjty.lib.varia.OrientationTools;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;

public class BaseBlock extends Block {

    protected ModBase modBase;
    private boolean creative;

    public static final PropertyDirection FACING_HORIZ = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyDirection FACING = PropertyDirection.create("facing");

    public BaseBlock(ModBase mod,
                        Material material,
                        String name,
                        Class<? extends ItemBlock> itemBlockClass) {
        super(material);
        this.modBase = mod;
        this.creative = false;
        setHardness(2.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 0);
        setUnlocalizedName(mod.getModId() + "." + name);
        setRegistryName(name);
        McJtyRegister.registerLater(this, mod, itemBlockClass);
    }

    public void setCreative(boolean creative) {
        this.creative = creative;
    }

    public boolean isCreative() {
        return creative;
    }

    public static Collection<IProperty<?>> getPropertyKeys(IBlockState state) {
        return state.getPropertyKeys();
    }

    public static boolean activateBlock(Block block, World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return block.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }

    public enum RotationType {
        HORIZROTATION,
        ROTATION,
        NONE
    }

    public RotationType getRotationType() {
        return RotationType.ROTATION;
    }



    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        switch (getRotationType()) {
            case HORIZROTATION:
                world.setBlockState(pos, state.withProperty(FACING_HORIZ, placer.getHorizontalFacing().getOpposite()), 2);
                break;
            case ROTATION:
                world.setBlockState(pos, state.withProperty(FACING, OrientationTools.getFacingFromEntity(pos, placer)), 2);
                break;
            case NONE:
                break;
        }
    }


    protected EnumFacing getOrientation(BlockPos pos, EntityLivingBase entityLivingBase) {
        switch (getRotationType()) {
            case HORIZROTATION:
                return OrientationTools.determineOrientationHoriz(entityLivingBase);
            case ROTATION:
                return OrientationTools.determineOrientation(pos, entityLivingBase);
            case NONE:
                return null;
        }
        return null;
    }

    public EnumFacing getFrontDirection(IBlockState state) {
        switch (getRotationType()) {
            case HORIZROTATION:
                return state.getValue(FACING_HORIZ);
            case ROTATION:
                return state.getValue(FACING);
            case NONE:
                return EnumFacing.NORTH;
        }
        return EnumFacing.NORTH;
    }

    public EnumFacing getRightDirection(IBlockState state) {
        return getFrontDirection(state).rotateYCCW();
    }

    public EnumFacing getLeftDirection(IBlockState state) {
        return getFrontDirection(state).rotateY();
    }

    public static EnumFacing getFrontDirection(RotationType metaUsage, IBlockState state) {
        EnumFacing orientation;
        switch (metaUsage) {
            case HORIZROTATION:
                orientation = OrientationTools.getOrientationHoriz(state);
                break;
            case ROTATION:
                orientation = OrientationTools.getOrientation(state);
                break;
            case NONE:
            default:
                orientation = EnumFacing.SOUTH;
                break;
        }
        return orientation;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        switch (getRotationType()) {
            case HORIZROTATION:
                return getDefaultState().withProperty(FACING_HORIZ, EnumFacing.VALUES[meta + 2]);
            case ROTATION:
                return getDefaultState().withProperty(FACING, EnumFacing.VALUES[meta & 7]);
            case NONE:
                return getDefaultState();
        }
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        switch (getRotationType()) {
            case HORIZROTATION:
                return state.getValue(FACING_HORIZ).getIndex()-2;
            case ROTATION:
                return state.getValue(FACING).getIndex();
            case NONE:
                return 0;
        }
        return 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        switch (getRotationType()) {
            case HORIZROTATION:
                return new BlockStateContainer(this, FACING_HORIZ);
            case ROTATION:
                return new BlockStateContainer(this, FACING);
            case NONE:
                return super.createBlockState();
        }
        return super.createBlockState();
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

}
