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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;
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
        ISideRenderControl renderControl = getSideRenderControl();

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
            protected int getRedstoneOutput(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
                return getter.getRedstoneOutput(state, world, pos, side);
            }

            @Override
            protected IProperty<?>[] getProperties() {
                return properties;
            }

            @Override
            public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer) {
                return canRenderInLayer.canRenderInLayer(state, layer);
            }

            @Override
            public int getLightValue(BlockState state, IEnviromentBlockReader world, BlockPos pos) {
                return getLightValue.getLightValue(state, world, pos);
            }

            @Override
            protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
                super.fillStateContainer(builder);
                builder.add(properties);
            }

            @Override
            public boolean doesSideBlockRendering(BlockState state, IEnviromentBlockReader world, BlockPos pos, Direction face) {
                return renderControl.doesSideBlockRendering(state, world, pos, face);
            }

            @Override
            public void onBlockClicked(BlockState state, World world, BlockPos pos, PlayerEntity player) {
                clickAction.doClick(world, pos, player);
            }

            @Override
            public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
                if (!action.doActivate(world, pos, player, hand, result)) {
                    return super.onBlockActivated(state, world, pos, player, hand, result);
                } else {
                    return true;
                }
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
