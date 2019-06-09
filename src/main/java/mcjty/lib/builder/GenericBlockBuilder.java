package mcjty.lib.builder;

import mcjty.lib.api.IModuleSupport;
import mcjty.lib.base.ModBase;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.GenericBlock;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.EmptyContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.multipart.PartSlot;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

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
            protected int getRedstoneOutput(BlockState state, IBlockAccess world, BlockPos pos, Direction side) {
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
            public int getLightValue(BlockState state, IBlockAccess world, BlockPos pos) {
                return getLightValue.getLightValue(state, world, pos);
            }

            @Override
            public boolean doesSideBlockRendering(BlockState state, IBlockAccess world, BlockPos pos, Direction face) {
                return renderControl.doesSideBlockRendering(state, world, pos, face);
            }

            @Override
            public void onBlockClicked(World worldIn, BlockPos pos, PlayerEntity playerIn) {
                clickAction.doClick(worldIn, pos, playerIn);
            }

            @Nonnull
            @Override
            public PartSlot getSlotFromState(World world, BlockPos pos, BlockState newState) {
                return slotGetter.getSlotFromState(world, pos, newState);
            }

            @Override
            public BlockState getStateForPlacement(World worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, MobEntity placer) {
                BlockState state = placementGetter.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
                if (state != null) {
                    return state;
                }
                return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
            }

            @Override
            public boolean onBlockActivated(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
                if (!action.doActivate(worldIn, pos, playerIn, hand, facing, hitX, hitY, hitZ)) {
                    return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
                } else {
                    return true;
                }
            }

            @Override
            public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
                return boundingBox.getBoundingBox(state, source, pos);
            }

            @Override
            public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
                if (!boxToList.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState)) {
                    super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
                }
            }

            @Nullable
            @Override
            public PathNodeType getAiPathNodeType(BlockState state, IBlockAccess world, BlockPos pos) {
                PathNodeType type = getAIPathNodeType.getAiPathNodeType(state, world, pos);
                if (type == null) {
                    return super.getAiPathNodeType(state, world, pos);
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
