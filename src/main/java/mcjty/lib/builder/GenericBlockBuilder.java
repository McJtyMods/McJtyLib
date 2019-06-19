package mcjty.lib.builder;

import mcjty.lib.api.IModuleSupport;
import mcjty.lib.base.ModBase;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.GenericBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.EmptyContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.multipart.PartSlot;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
        ISideRenderControl renderControl = getSideRenderControl();
        IAddCollisionBoxToList boxToList = getAddCollisionBoxToList();

        GenericBlock<T, GenericContainer> block = new GenericBlock<T, GenericContainer>(mod, material, tileEntityClass,
                (player, tileEntity) -> {
//                    GenericContainer c = new GenericContainer(containerFactory);
                    // @todo REMOVE THIS ENTIRE CLASS
//                    if (tileEntity != null) {
//                        c.addInventory(ContainerFactory.CONTAINER_CONTAINER, tileEntity);
//                    }
//                    c.addInventory(ContainerFactory.CONTAINER_PLAYER, player.inventory);
//                    c.generateSlots();
                    return null;
                },
                itemBlockFactory, registryName, true) {

            @Override
            public RotationType getRotationType() {
                return rotationType;
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
            public boolean doesSideBlockRendering(BlockState state, IEnviromentBlockReader world, BlockPos pos, Direction face) {
                return renderControl.doesSideBlockRendering(state, world, pos, face);
            }

            @Override
            public void onBlockClicked(BlockState state, World world, BlockPos pos, PlayerEntity player) {
                clickAction.doClick(world, pos, player);
            }

            @Nonnull
            @Override
            public PartSlot getSlotFromState(World world, BlockPos pos, BlockState newState) {
                return slotGetter.getSlotFromState(world, pos, newState);
            }

            @Nullable
            @Override
            public BlockState getStateForPlacement(BlockItemUseContext context) {
                BlockState state = placementGetter.getStateForPlacement(context);
                if (state != null) {
                    return state;
                }
                return super.getStateForPlacement(context);
            }

            @Override
            public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
                if (!action.doActivate(world, pos, player, hand, result)) {
                    return super.onBlockActivated(state, world, pos, player, hand, result);
                } else {
                    return true;
                }
            }

            // @todo 1.14
//            @Override
//            public AxisAlignedBB getBoundingBox(BlockState state, IBlockReader source, BlockPos pos) {
//                return boundingBox.getBoundingBox(state, source, pos);
//            }
//
//            @Override
//            public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
//                if (!boxToList.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState)) {
//                    super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
//                }
//            }


            @Nullable
            @Override
            public PathNodeType getAiPathNodeType(BlockState state, IBlockReader world, BlockPos pos, @Nullable MobEntity entity) {
                PathNodeType type = getAIPathNodeType.getAiPathNodeType(state, world, pos);
                if (type == null) {
                    return super.getAiPathNodeType(state, world, pos, entity);
                }
                return type;
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
