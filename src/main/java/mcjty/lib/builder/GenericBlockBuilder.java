package mcjty.lib.builder;

import mcjty.lib.api.IModuleSupport;
import mcjty.lib.base.ModBase;
import mcjty.lib.container.*;
import mcjty.lib.entity.GenericTileEntity;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Build blocks using this class
 */
public class GenericBlockBuilder<T extends GenericTileEntity, C extends Container> extends BaseBlockBuilder<GenericBlockBuilder<T, C>> {

    private Class<T> tileEntityClass;
    private Class<C> containerClass;
    private ContainerFactory containerFactory;

    private IModuleSupport moduleSupport;

    private int guiId = -1;

    public GenericBlockBuilder(ModBase mod, String registryName) {
        super(mod, registryName);
    }

    public GenericBlockBuilder<T, C> tileEntityClass(Class<T> tileEntityClass) {
        this.tileEntityClass = tileEntityClass;
        return this;
    }

    public GenericBlockBuilder<T, C> container(Class<C> containerClass, ContainerFactory containerFactory) {
        this.containerClass = containerClass;
        this.containerFactory = containerFactory;
        return this;
    }

    public GenericBlockBuilder<T, C> emptyContainer(Class<C> containerClass) {
        this.containerClass = containerClass;
        this.containerFactory = EmptyContainerFactory.getInstance();
        return this;
    }

    public GenericBlockBuilder<T, C> moduleSupport(IModuleSupport moduleSupport) {
        this.moduleSupport = moduleSupport;
        return this;
    }

    public GenericBlockBuilder<T, C> guiId(int id) {
        this.guiId = id;
        return this;
    }


    @Override
    public GenericBlock<T, C> build() {
        IProperty<?>[] properties = calculateProperties();
        boolean needsRedstoneCheck = flags.contains(BlockFlags.REDSTONE_CHECK);
        boolean hasRedstoneOutput = flags.contains(BlockFlags.REDSTONE_OUTPUT);
        IRedstoneGetter getter = getRedstoneGetter(hasRedstoneOutput);
        ICanRenderInLayer canRenderInLayer = getCanRenderInLayer();

        GenericBlock<T, C> block = new GenericBlock<T, C>(mod, material, tileEntityClass, containerClass,
                (player, tileEntity) -> {
                    GenericContainer c = new GenericContainer(containerFactory);
                    if (tileEntity instanceof IInventory) {
                        c.addInventory(ContainerFactory.CONTAINER_CONTAINER, (IInventory) tileEntity);
                    }
                    c.addInventory(ContainerFactory.CONTAINER_PLAYER, player.inventory);
                    c.generateSlots();
                    return (C) c;
                },
                itemBlockClass, registryName, true) {
            @Override
            public int getGuiID() {
                return guiId;
            }

            @SideOnly(Side.CLIENT)
            @Override
            public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag flags) {
                intAddInformation(stack, tooltip);
                InformationString i = informationString;
                if ((Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) && informationStringWithShift != null) {
                    i = informationStringWithShift;
                }
                addLocalizedInformation(i, stack, tooltip);
            }

            @Override
            public boolean needsRedstoneCheck() {
                return needsRedstoneCheck;
            }

            @Override
            public boolean hasRedstoneOutput() {
                return hasRedstoneOutput;
            }

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
            protected IModuleSupport getModuleSupport() {
                return moduleSupport;
            }
        };
        setupBlock(block);
        return block;
    }

    private IRedstoneGetter getRedstoneGetter(boolean hasRedstoneOutput) {
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
            getter = (state, world, pos, side) -> {
                return -1;
            };
        }
        return getter;
    }

    private static void test() {
        GenericBlock<GenericTileEntity, Container> b = new GenericBlockBuilder<>(null, "X")
                .information("test")
                .build();
    }
}
