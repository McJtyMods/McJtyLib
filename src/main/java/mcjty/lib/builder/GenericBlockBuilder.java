package mcjty.lib.builder;

import mcjty.lib.api.IModuleSupport;
import mcjty.lib.base.ModBase;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.GenericBlock;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.EmptyContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Build blocks using this class
 */
public class GenericBlockBuilder<T extends GenericTileEntity> extends BaseBlockBuilder<GenericBlockBuilder<T>> {

    private Class<T> tileEntityClass;
    private ContainerFactory containerFactory;

    private IModuleSupport moduleSupport;

    private int guiId = -1;
    private boolean infusable = false;

    public GenericBlockBuilder(ModBase mod, String registryName) {
        super(mod, registryName);
    }

    public GenericBlockBuilder<T> tileEntityClass(Class<T> tileEntityClass) {
        this.tileEntityClass = tileEntityClass;
        return this;
    }

    public GenericBlockBuilder<T> container(ContainerFactory containerFactory) {
        this.containerFactory = containerFactory;
        return this;
    }

    public GenericBlockBuilder<T> emptyContainer() {
        this.containerFactory = EmptyContainerFactory.getInstance();
        return this;
    }

    public GenericBlockBuilder<T> infusable() {
        this.infusable = true;
        return this;
    }

    public GenericBlockBuilder<T> moduleSupport(IModuleSupport moduleSupport) {
        this.moduleSupport = moduleSupport;
        return this;
    }

    public GenericBlockBuilder<T> guiId(int id) {
        this.guiId = id;
        return this;
    }

    @Override
    public GenericBlock<T, GenericContainer> build() {
        IProperty<?>[] properties = calculateProperties();
        IRedstoneGetter getter = getRedstoneGetter(flags.contains(BlockFlags.REDSTONE_OUTPUT));
        ICanRenderInLayer canRenderInLayer = getCanRenderInLayer();
        IGetLightValue getLightValue = getGetLightValue();

        GenericBlock<T, GenericContainer> block = new GenericBlock<T, GenericContainer>(mod, material, tileEntityClass,
                (player, tileEntity) -> {
                    GenericContainer c = new GenericContainer(containerFactory);
                    if (tileEntity != null) {
                        c.addInventory(ContainerFactory.CONTAINER_CONTAINER, tileEntity);
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
        };
        setupBlock(block);
        return block;
    }

    public static IRedstoneGetter getRedstoneGetter(boolean hasRedstoneOutput) {
        IRedstoneGetter getter;
        if (hasRedstoneOutput) {
            getter = (state, world, pos, side) -> {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof GenericTileEntity) {
                    return ((GenericTileEntity) te).getRedstoneOutput(state, world, pos, side);
                }
                return -1;
            };
        } else {
            getter = (state, world, pos, side) -> -1;
        }
        return getter;
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
