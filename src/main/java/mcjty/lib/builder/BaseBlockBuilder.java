package mcjty.lib.builder;

import mcjty.lib.base.ModBase;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.GenericItemBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Build blocks using this class
 */
public class BaseBlockBuilder<T extends BaseBlockBuilder> {

    private static final Pattern COMPILE = Pattern.compile("@", Pattern.LITERAL);
    public static final IProperty<?>[] EMPTY_PROPERTIES = new IProperty<?>[0];

    protected final ModBase mod;
    protected final String registryName;

    protected CreativeTabs creativeTabs;

    protected Material material = Material.IRON;
    protected Class<? extends ItemBlock> itemBlockClass = GenericItemBlock.class;

    protected List<IProperty<?>> extraProperties = new ArrayList<>();

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

    public T itemBlockClass(Class<? extends ItemBlock> itemBlockClass) {
        this.itemBlockClass = itemBlockClass;
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

    public BaseBlock build() {
        IProperty<?>[] properties = calculateProperties();
        ICanRenderInLayer canRenderInLayer = getCanRenderInLayer();
        IGetLightValue getLightValue = getGetLightValue();

        BaseBlock block = new BaseBlock(mod, material, registryName, itemBlockClass) {
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
        block.setInformationString(informationString);
        if (informationStringWithShift != null) {
            block.setInformationStringWithShift(informationStringWithShift);
        } else {
            block.setInformationStringWithShift(informationString);
        }
    }
}
