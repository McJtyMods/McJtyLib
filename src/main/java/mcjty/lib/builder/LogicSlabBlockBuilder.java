package mcjty.lib.builder;

import mcjty.lib.api.IModuleSupport;
import mcjty.lib.base.ModBase;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.GenericBlock;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.EmptyContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.LogicTileEntity;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import static mcjty.lib.blocks.LogicSlabBlock.LOGIC_FACING;
import static mcjty.lib.blocks.LogicSlabBlock.META_INTERMEDIATE;

/**
 * Build blocks using this class
 */
public class LogicSlabBlockBuilder<T extends LogicTileEntity> extends BaseBlockBuilder<LogicSlabBlockBuilder<T>> {

    public static IProperty<?>[] ADDITIONAL_PROPERTIES = null;;
    private Class<T> tileEntityClass;
    private ContainerFactory containerFactory;

    private IModuleSupport moduleSupport;

    private int guiId = -1;
    private boolean infusable = false;

    public LogicSlabBlockBuilder(ModBase mod, String registryName) {
        super(mod, registryName);
        rotationType(BaseBlock.RotationType.NONE);
    }

    public LogicSlabBlockBuilder<T> tileEntityClass(Class<T> tileEntityClass) {
        this.tileEntityClass = tileEntityClass;
        return this;
    }

    public LogicSlabBlockBuilder<T> container(ContainerFactory containerFactory) {
        this.containerFactory = containerFactory;
        return this;
    }

    public LogicSlabBlockBuilder<T> emptyContainer() {
        this.containerFactory = EmptyContainerFactory.getInstance();
        return this;
    }

    public LogicSlabBlockBuilder<T> infusable() {
        this.infusable = true;
        return this;
    }

    public LogicSlabBlockBuilder<T> moduleSupport(IModuleSupport moduleSupport) {
        this.moduleSupport = moduleSupport;
        return this;
    }

    public LogicSlabBlockBuilder<T> guiId(int id) {
        this.guiId = id;
        return this;
    }

    @Override
    protected IProperty<?>[] getAdditionalProperties() {
        if (ADDITIONAL_PROPERTIES == null) {
            ADDITIONAL_PROPERTIES = new IProperty<?>[]{LOGIC_FACING, META_INTERMEDIATE};
        }
        return ADDITIONAL_PROPERTIES;
    }

    @Override
    public GenericBlock<T, GenericContainer> build() {
        IProperty<?>[] properties = calculateProperties();
        IRedstoneGetter getter = GenericBlockBuilder.getRedstoneGetter(flags.contains(BlockFlags.REDSTONE_OUTPUT));
        ICanRenderInLayer canRenderInLayer = getCanRenderInLayer();
        IGetLightValue getLightValue = getGetLightValue();

        GenericBlock<T, GenericContainer> block = new LogicSlabBlock<T, GenericContainer>(mod, material, tileEntityClass,
                (player, inventory) -> {
                    GenericContainer c = new GenericContainer(containerFactory);
                    if (inventory != null) {
                        c.addInventory(ContainerFactory.CONTAINER_CONTAINER, inventory);
                    }
                    c.addInventory(ContainerFactory.CONTAINER_PLAYER, player.inventory);
                    c.generateSlots();
                    return c;
                },
                itemBlockFactory, registryName, true) {

            @Override
            public RotationType getRotationType() {
                return rotationType;
            }

            @Override
            protected void checkRedstone(World world, BlockPos pos) {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof LogicTileEntity) {
                    LogicTileEntity logicTileEntity = (LogicTileEntity)te;
                    logicTileEntity.checkRedstone(world, pos);
                }
            }

            @Override
            protected int getRedstoneOutput(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
                return getter.getRedstoneOutput(state, world, pos, side);
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
            protected BlockStateContainer createBlockState() {
                return new BlockStateContainer(this, getProperties());
            }
        };
        setupBlock(block);
        return block;
    }

    @Override
    protected void setupBlock(BaseBlock block) {
        super.setupBlock(block);
        GenericBlock<?, ?> b = (GenericBlock<?, ?>) block;
        b.setGuiId(guiId);
        b.setNeedsRedstoneCheck(flags.contains(BlockFlags.REDSTONE_CHECK));
        b.setHasRedstoneOutput(flags.contains(BlockFlags.REDSTONE_OUTPUT));
        b.setModuleSupport(moduleSupport);
        b.setInfusable(infusable);
    }
}
