package mcjty.lib.builder;

import mcjty.lib.base.ModBase;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.GenericItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

import static net.minecraft.block.Block.FULL_BLOCK_AABB;

/**
 * Build blocks using this class
 */
public class BaseBlockBuilder<T extends BaseBlockBuilder<T>> {

    private static final Pattern COMPILE = Pattern.compile("@", Pattern.LITERAL);
    public static final IProperty<?>[] EMPTY_PROPERTIES = new IProperty<?>[0];

    protected final ModBase mod;
    protected final String registryName;

    protected CreativeTabs creativeTabs;

    protected Material material = Material.IRON;
    protected Function<Block, ItemBlock> itemBlockFactory = GenericItemBlock::new;

    protected List<IProperty<?>> extraProperties = new ArrayList<>();

    protected IActivateAction action = (world, pos, player, hand, side, hitX, hitY, hitZ) -> false;
    protected IClickAction clickAction = (world, pos, player) -> {};
    protected IGetBoundingBox boundingBox = (state, source, pos) -> FULL_BLOCK_AABB;
    protected IAddCollisionBoxToList cdBoxToList = null;

    protected InformationString informationString;
    protected InformationString informationStringWithShift;

    protected Set<BlockFlags> flags = new HashSet<>();
    protected int lightValue = -1;

    protected BaseBlock.RotationType rotationType = BaseBlock.RotationType.ROTATION;

    public BaseBlockBuilder(ModBase mod, String registryName) {
        this.mod = mod;
        this.registryName = registryName;
    }

    public T creativeTabs(CreativeTabs creativeTabs) {
        this.creativeTabs = creativeTabs;
        return (T) this;
    }

    public T material(Material material) {
        this.material = material;
        return (T) this;
    }

    public T itemBlockFactory(Function<Block, ItemBlock> itemBlockFactory) {
        this.itemBlockFactory = itemBlockFactory;
        return (T) this;
    }

    public T info(String informationString) {
        this.informationString = new InformationString(informationString);
        return (T) this;
    }

    public T infoParameter(Function<ItemStack, String> parameter) {
        this.informationString.addParameter(parameter);
        return (T) this;
    }

    public T infoExtended(String informationString) {
        this.informationStringWithShift = new InformationString(informationString);
        return (T) this;
    }

    public T infoExtendedParameter(Function<ItemStack, String> parameter) {
        this.informationStringWithShift.addParameter(parameter);
        return (T) this;
    }

    public T property(IProperty<?> property) {
        extraProperties.add(property);
        return (T) this;
    }

    public T flags(BlockFlags... flags) {
        Collections.addAll(this.flags, flags);
        return (T) this;
    }

    public T rotationType(BaseBlock.RotationType rotationType) {
        this.rotationType = rotationType;
        return (T) this;
    }

    public T lightValue(int v) {
        this.lightValue = v;
        return (T) this;
    }

    public T clickAction(IClickAction action) {
        this.clickAction = action;
        return (T) this;
    }

    public T activateAction(IActivateAction action) {
        this.action = action;
        return (T) this;
    }

    public T addCollisionBoxToList(IAddCollisionBoxToList list) {
        this.cdBoxToList = list;
        return (T) this;
    }

    public T boundingBox(IGetBoundingBox boundingBox) {
        this.boundingBox = boundingBox;
        return (T) this;
    }

    public BaseBlock build() {
        IProperty<?>[] properties = calculateProperties();
        ICanRenderInLayer canRenderInLayer = getCanRenderInLayer();
        IGetLightValue getLightValue = getGetLightValue();
        ISideRenderControl renderControl = getSideRenderControl();
        IAddCollisionBoxToList boxToList = getAddCollisionBoxToList();

        BaseBlock block = new BaseBlock(mod, material, registryName, itemBlockFactory) {
            @Override
            protected IProperty<?>[] getProperties() {
                return properties;
            }

            @Override
            public RotationType getRotationType() {
                return rotationType;
            }

            @Override
            public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
                return canRenderInLayer.canRenderInLayer(state, layer);
            }

            @Override
            public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
                return getLightValue.getLightValue(state, world, pos);
            }

            @Override
            public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
                return renderControl.doesSideBlockRendering(state, world, pos, face);
            }

            @Override
            public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
                clickAction.doClick(worldIn, pos, playerIn);
            }

            @Override
            public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
                if (!action.doActivate(worldIn, pos, playerIn, hand, facing, hitX, hitY, hitZ)) {
                    return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
                } else {
                    return true;
                }
            }

            @Override
            public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
                return boundingBox.getBoundingBox(state, source, pos);
            }

            @Override
            public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
                if (!boxToList.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState)) {
                    super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
                }
            }
        };
        setupBlock(block);
        return block;
    }

    protected IGetLightValue getGetLightValue() {
        if (lightValue == -1) {
            return (state, world, pos) -> state.getLightValue();
        } else {
            return (state, world, pos) -> lightValue;
        }
    }

    protected IAddCollisionBoxToList getAddCollisionBoxToList() {
        if (cdBoxToList != null) {
            return cdBoxToList;
        } else if (flags.contains(BlockFlags.NO_COLLISION)) {
            return (state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState) -> true;
        } else {
            return (state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState) -> false;
        }
    }

    protected ISideRenderControl getSideRenderControl() {
        if (flags.contains(BlockFlags.RENDER_NOSIDES)) {
            return (state, world, pos, face) -> world.getBlockState(pos.offset(face)).equals(state);
        } else {
            return (state, world, pos, face) -> state.isOpaqueCube();
        }
    }

    protected ICanRenderInLayer getCanRenderInLayer() {
        BlockRenderLayer layer = null;
        if (flags.contains(BlockFlags.RENDER_CUTOUT)) {
            layer = BlockRenderLayer.CUTOUT;
        } else if (flags.contains(BlockFlags.RENDER_TRANSLUCENT)) {
            layer = BlockRenderLayer.TRANSLUCENT;
        }
        ICanRenderInLayer canRenderInLayer;
        if (layer != null) {
            BlockRenderLayer finalLayer = layer;
            if (flags.contains(BlockFlags.RENDER_SOLID)) {
                canRenderInLayer = (state, layer1) -> layer1 == BlockRenderLayer.SOLID || layer1 == finalLayer;
            } else {
                canRenderInLayer = (state, layer1) -> layer1 == finalLayer;
            }
        } else {
            canRenderInLayer = (state, layer1) -> layer1 == BlockRenderLayer.SOLID;
        }
        return canRenderInLayer;
    }

    protected IProperty<?>[] calculateProperties() {
        IProperty<?>[] properties = BaseBlock.getProperties(rotationType);
        IProperty<?>[] additionalProperties = getAdditionalProperties();

        if (!extraProperties.isEmpty() || additionalProperties.length > 0) {
            List<IProperty<?>> newProperties = new ArrayList<>();
            Collections.addAll(newProperties, properties);
            Collections.addAll(newProperties, additionalProperties);
            for (IProperty<?> property : extraProperties) {
                newProperties.add(property);
            }
            properties = newProperties.toArray(new IProperty[newProperties.size()]);
        }
        return properties;
    }

    protected IProperty<?>[] getAdditionalProperties() {
        return EMPTY_PROPERTIES;
    }

    protected void setupBlock(BaseBlock block) {
        block.setCreative(flags.contains(BlockFlags.CREATIVE));
        if (creativeTabs != null) {
            block.setCreativeTab(creativeTabs);
        }
        final boolean opaque = !flags.contains(BlockFlags.NON_OPAQUE);
        block.setOpaqueCube(opaque);

        final boolean full = !flags.contains(BlockFlags.NON_FULLCUBE);
        block.setFullcube(full);

        block.setInformationString(informationString);
        if (informationStringWithShift != null) {
            block.setInformationStringWithShift(informationStringWithShift);
        } else {
            block.setInformationStringWithShift(informationString);
        }
    }
}
