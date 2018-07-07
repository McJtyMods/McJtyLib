package mcjty.lib.blocks;

import mcjty.lib.McJtyRegister;
import mcjty.lib.base.ModBase;
import mcjty.lib.builder.InformationString;
import mcjty.lib.compat.theoneprobe.TOPInfoProvider;
import mcjty.lib.compat.waila.WailaInfoProvider;
import mcjty.lib.varia.OrientationTools;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

@Optional.InterfaceList({
        @Optional.Interface(iface = "mcjty.lib.compat.waila.WailaInfoProvider", modid = "waila"),
        @Optional.Interface(iface = "mcjty.lib.compat.theoneprobe.TOPInfoProvider", modid = "theoneprobe")
})
public class BaseBlock extends Block implements WailaInfoProvider, TOPInfoProvider {

    public static final IProperty<?>[] NONE_PROPERTIES = new IProperty[0];
    protected ModBase modBase;
    private boolean creative;

    // The vanilla Block constructor sets this.fullBlock based on isOpaqueCube. We want our blocks to be opaque by default.
    // If this were "opaque = true", our constructor wouldn't set it to true until after Block's constructor finished,
    // so fullBlock would be wrong. Since booleans default to false, we need to invert this one as "nonopaque = false", so
    // that it will be correct while we're still in Block's constructor.
    private boolean nonopaque = false;

    private InformationString informationString;
    private InformationString informationStringWithShift;

    public static final PropertyDirection FACING_HORIZ = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    public static final IProperty<?>[] HORIZ_PROPERTIES = new IProperty[]{FACING_HORIZ};
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final IProperty<?>[] ROTATING_PROPERTIES = new IProperty[]{FACING};

    public BaseBlock(ModBase mod,
                        Material material,
                        String name,
                        Function<Block, ItemBlock> itemBlockFactory) {
        super(material);
        this.modBase = mod;
        this.creative = false;
        setHardness(2.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 0);
        setUnlocalizedName(mod.getModId() + "." + name);
        setRegistryName(name);
        McJtyRegister.registerLater(this, mod, itemBlockFactory);
    }

    public BaseBlock setCreative(boolean creative) {
        this.creative = creative;
        return this;
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

    public void setInformationString(InformationString informationString) {
        this.informationString = informationString;
    }

    public void setInformationStringWithShift(InformationString informationStringWithShift) {
        this.informationStringWithShift = informationStringWithShift;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        InformationString i = informationString;
        if ((Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
            i = informationStringWithShift;
        }
        if (i != null) {
            addLocalizedInformation(i, stack, tooltip);
        }
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState state = super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
        switch (getRotationType()) {
            case HORIZROTATION:
                return state.withProperty(FACING_HORIZ, placer.getHorizontalFacing().getOpposite());
            case ROTATION:
                return state.withProperty(FACING, OrientationTools.getFacingFromEntity(pos, placer));
            default:
                return state;
        }
    }

    protected EnumFacing getOrientation(BlockPos pos, EntityLivingBase entityLivingBase) {
        switch (getRotationType()) {
            case HORIZROTATION:
                return OrientationTools.determineOrientationHoriz(entityLivingBase);
            case ROTATION:
                return OrientationTools.determineOrientation(pos, entityLivingBase);
            default:
                return null;
        }
    }

    public EnumFacing getFrontDirection(IBlockState state) {
        switch (getRotationType()) {
            case HORIZROTATION:
                return state.getValue(FACING_HORIZ);
            case ROTATION:
                return state.getValue(FACING);
            default:
                return EnumFacing.NORTH;
        }
    }

    public EnumFacing getRightDirection(IBlockState state) {
        return getFrontDirection(state).rotateYCCW();
    }

    public EnumFacing getLeftDirection(IBlockState state) {
        return getFrontDirection(state).rotateY();
    }

    public static EnumFacing getFrontDirection(RotationType metaUsage, IBlockState state) {
        switch (metaUsage) {
            case HORIZROTATION:
                return OrientationTools.getOrientationHoriz(state);
            case ROTATION:
                return OrientationTools.getOrientation(state);
            default:
                return EnumFacing.SOUTH;
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        switch (getRotationType()) {
            case HORIZROTATION:
                return getDefaultState().withProperty(FACING_HORIZ, EnumFacing.VALUES[meta + 2]);
            case ROTATION:
                return getDefaultState().withProperty(FACING, EnumFacing.VALUES[meta & 7]);
            default:
                return getDefaultState();
        }
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        switch (getRotationType()) {
            case HORIZROTATION:
                return state.getValue(FACING_HORIZ).getIndex()-2;
            case ROTATION:
                return state.getValue(FACING).getIndex();
            default:
                return 0;
        }
    }

    protected IProperty<?>[] getProperties() {
        return getProperties(getRotationType());
    }

    public static IProperty<?>[] getProperties(RotationType rotationType) {
        switch (rotationType) {
            case HORIZROTATION:
                return HORIZ_PROPERTIES;
            case ROTATION:
                return ROTATING_PROPERTIES;
            case NONE:
            default:
                return NONE_PROPERTIES;
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getProperties());
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return !nonopaque;
    }

    public void setOpaqueCube(boolean opaque) {
        this.nonopaque = !opaque;
        this.fullBlock = this.getDefaultState().isOpaqueCube();
        this.lightOpacity = this.fullBlock ? 255 : 0;
        this.translucent = !blockMaterial.blocksLight();
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    @Optional.Method(modid = "theoneprobe")
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
    }

    @Override
    @SideOnly(Side.CLIENT)
    @Optional.Method(modid = "waila")
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    private static final Pattern COMPILE = Pattern.compile("@", Pattern.LITERAL);

    @SideOnly(Side.CLIENT)
    public static void addLocalizedInformation(InformationString informationString, ItemStack stack, List<String> tooltip) {
        if (informationString != null) {
            Object[] parameters = new Object[informationString.getInformationStringParameters().size()];
            for (int i = 0 ; i < parameters.length ; i++) {
                parameters[i] = informationString.getInformationStringParameters().get(i).apply(stack);
            }
            String translated = I18n.format(informationString.getString(), parameters);
            translated = COMPILE.matcher(translated).replaceAll("\u00a7");
            Collections.addAll(tooltip, StringUtils.split(translated, "\n"));
        }
    }
}
