package mcjty.lib.blocks;

import mcjty.lib.McJtyLib;
import mcjty.lib.McJtyRegister;
import mcjty.lib.base.ModBase;
import mcjty.lib.builder.InformationString;
import mcjty.lib.compat.theoneprobe.TOPInfoProvider;
import mcjty.lib.compat.waila.WailaInfoProvider;
import mcjty.lib.multipart.IPartBlock;
import mcjty.lib.multipart.PartSlot;
import mcjty.lib.varia.OrientationTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

//@Optional.InterfaceList({
//        @Optional.Interface(iface = "mcjty.lib.compat.waila.WailaInfoProvider", modid = "waila"),
//        @Optional.Interface(iface = "mcjty.lib.compat.theoneprobe.TOPInfoProvider", modid = "theoneprobe")
//})
public class BaseBlock extends Block implements WailaInfoProvider, TOPInfoProvider, IPartBlock {

    public static final IProperty<?>[] NONE_PROPERTIES = new IProperty[0];
    protected ModBase modBase;
    private boolean creative;

    // The vanilla Block constructor sets this.fullBlock based on isOpaqueCube. We want our blocks to be opaque by default.
    // If this were "opaque = true", our constructor wouldn't set it to true until after Block's constructor finished,
    // so fullBlock would be wrong. Since booleans default to false, we need to invert this one as "nonopaque = false", so
    // that it will be correct while we're still in Block's constructor.
    private boolean nonopaque = false;
    private boolean nonfullcube = false;

    private InformationString informationString;
    private InformationString informationStringWithShift;

    public static final DirectionProperty FACING_HORIZ = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);

    public static final IProperty<?>[] HORIZ_PROPERTIES = new IProperty[]{FACING_HORIZ};
    public static final DirectionProperty FACING = DirectionProperty.create("facing");
    public static final IProperty<?>[] ROTATING_PROPERTIES = new IProperty[]{FACING};

    public BaseBlock(ModBase mod,
                        Material material,
                        String name,
                        Function<Block, BlockItem> itemBlockFactory) {
        super(Properties.create(material)
            .hardnessAndResistance(2.0f)
            .sound(SoundType.METAL));
        this.modBase = mod;
        this.creative = false;
        setRegistryName(mod.getModId(), name);
        // @todo 1.14
//        setHarvestLevel("pickaxe", 0);
        McJtyRegister.registerLater(this, mod, itemBlockFactory);
    }

    public BaseBlock setCreative(boolean creative) {
        this.creative = creative;
        return this;
    }

    public boolean isCreative() {
        return creative;
    }

    public static Collection<IProperty<?>> getPropertyKeys(BlockState state) {
        return state.getProperties();
    }

    public static boolean activateBlock(Block block, World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        return block.onBlockActivated(state, world, pos, player, hand, result);
    }

    @Nonnull
    @Override
    public PartSlot getSlotFromState(World world, BlockPos pos, BlockState newState) {
        return PartSlot.NONE;
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
    public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        InformationString i = informationString;
        // @todo 1.14
//        if ((Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
//            i = informationStringWithShift;
//        }
        if (i != null) {
            addLocalizedInformation(i, stack, tooltip);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        PlayerEntity placer = context.getPlayer();
        BlockPos pos = context.getPos();
        BlockState state = super.getStateForPlacement(context);
        switch (getRotationType()) {
            case HORIZROTATION:
                return state.with(FACING_HORIZ, placer.getHorizontalFacing().getOpposite());
            case ROTATION:
                return state.with(FACING, OrientationTools.getFacingFromEntity(pos, placer));
            default:
                return state;
        }
    }

    protected Direction getOrientation(BlockPos pos, MobEntity MobEntity) {
        switch (getRotationType()) {
            case HORIZROTATION:
                return OrientationTools.determineOrientationHoriz(MobEntity);
            case ROTATION:
                return OrientationTools.determineOrientation(pos, MobEntity);
            default:
                return null;
        }
    }

    public Direction getFrontDirection(BlockState state) {
        switch (getRotationType()) {
            case HORIZROTATION:
                return state.get(FACING_HORIZ);
            case ROTATION:
                return state.get(FACING);
            default:
                return Direction.NORTH;
        }
    }

    @Override
    public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation rot) {
        switch (getRotationType()) {
            case HORIZROTATION:
                return state.with(FACING_HORIZ, rot.rotate(state.get(FACING_HORIZ)));
            case ROTATION:
                return state.with(FACING, rot.rotate(state.get(FACING)));
            case NONE:
                return state;
        }
        return state;
    }

    public Direction getRightDirection(BlockState state) {
        return getFrontDirection(state).rotateYCCW();
    }

    public Direction getLeftDirection(BlockState state) {
        return getFrontDirection(state).rotateY();
    }

    public static Direction getFrontDirection(RotationType metaUsage, BlockState state) {
        switch (metaUsage) {
            case HORIZROTATION:
                return OrientationTools.getOrientationHoriz(state);
            case ROTATION:
                return OrientationTools.getOrientation(state);
            default:
                return Direction.SOUTH;
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
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        for (IProperty<?> property : getProperties()) {
            builder.add(property);
        }
    }

    // @todo 1.14
//    @Override
//    public boolean isOpaqueCube(BlockState state) {
//        return !nonopaque;
//    }
//
//    @Override
//    public boolean isFullCube(BlockState state) {
//        return !nonfullcube;
//    }

    public void setOpaqueCube(boolean opaque) {
        this.nonopaque = !opaque;
        //@todo 1.14
//        this.fullBlock = this.getDefaultState().isOpaqueCube();
//        this.lightOpacity = this.fullBlock ? 255 : 0;
//        this.translucent = !blockMaterial.blocksLight();
    }

    public void setFullcube(boolean fullcube) {
        this.nonfullcube = !fullcube;
    }

    public void initModel() {
        McJtyLib.proxy.initStandardItemModel(this);
    }

    // @todo 1.14
//    @Override
//    @Optional.Method(modid = "theoneprobe")
//    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
//    }
//
//    @Override
//    @Optional.Method(modid = "waila")
//    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
//        return currenttip;
//    }
//
    private static final Pattern COMPILE = Pattern.compile("@", Pattern.LITERAL);

    public static void addLocalizedInformation(InformationString informationString, ItemStack stack, List<ITextComponent> tooltip) {
        if (informationString != null) {
            Object[] parameters = new Object[informationString.getInformationStringParameters().size()];
            for (int i = 0 ; i < parameters.length ; i++) {
                parameters[i] = informationString.getInformationStringParameters().get(i).apply(stack);
            }
            String translated = I18n.format(informationString.getString(), parameters);
            translated = COMPILE.matcher(translated).replaceAll("\u00a7");
            String[] split = StringUtils.split(translated, "\n");
            for (String s : split) {
                tooltip.add(new StringTextComponent(s));
            }
        }
    }
}
