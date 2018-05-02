package mcjty.lib.builder;

import mcjty.lib.base.ModBase;
import mcjty.lib.container.BaseBlock;
import mcjty.lib.container.GenericItemBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Build blocks using this class
 */
public class BaseBlockBuilder<T extends BaseBlockBuilder> {

    private static final Pattern COMPILE = Pattern.compile("@", Pattern.LITERAL);

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

    public T information(String informationString, Function<ItemStack, String>... parameters) {
        this.informationString = new InformationString(informationString, parameters);
        return (T) this;
    }

    public T information(String informationString) {
        this.informationString = new InformationString(informationString);
        return (T) this;
    }

    public T informationShift(String informationString, Function<ItemStack, String>... parameters) {
        this.informationStringWithShift = new InformationString(informationString, parameters);
        return (T) this;
    }

    public T informationShift(String informationString) {
        this.informationStringWithShift = new InformationString(informationString);
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
        final boolean opaque = !flags.contains(BlockFlags.NON_OPAQUE);

        BaseBlock block = new BaseBlock(mod, material, registryName, itemBlockClass) {
            @Override
            @SideOnly(Side.CLIENT)
            public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag flags) {
                InformationString i = informationString;
                if ((Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) && informationStringWithShift != null) {
                    i = informationStringWithShift;
                }
                addLocalizedInformation(i, stack, tooltip);
            }

            @Override
            public RotationType getRotationType() {
                return rotationType;
            }

            @Override
            protected IProperty<?>[] getProperties() {
                return properties;
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
            public boolean isOpaqueCube(IBlockState state) {
                return opaque;
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
        if (!extraProperties.isEmpty()) {
            List<IProperty<?>> newProperties = new ArrayList<>();
            Collections.addAll(newProperties, properties);
            for (IProperty<?> property : extraProperties) {
                newProperties.add(property);
            }
            properties = newProperties.toArray(new IProperty[newProperties.size()]);
        }
        return properties;
    }

    protected void setupBlock(BaseBlock block) {
        block.setCreative(flags.contains(BlockFlags.CREATIVE));
        if (creativeTabs != null) {
            block.setCreativeTab(creativeTabs);
        }
    }

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
