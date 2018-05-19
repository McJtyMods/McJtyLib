package mcjty.lib.blocks;

import crazypants.enderio.api.redstone.IRedstoneConnectable;
import mcjty.lib.McJtyLib;
import mcjty.lib.McJtyRegister;
import mcjty.lib.api.IModuleSupport;
import mcjty.lib.api.Infusable;
import mcjty.lib.api.smartwrench.SmartWrench;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.base.ModBase;
import mcjty.lib.compat.CofhApiItemCompatibility;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.WrenchChecker;
import mcjty.lib.varia.WrenchUsage;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@Optional.InterfaceList({
        @Optional.Interface(iface = "crazypants.enderio.api.redstone.IRedstoneConnectable", modid = "enderio"),
})
public abstract class GenericBlock<T extends GenericTileEntity, C extends Container> extends BaseBlock
        implements ITileEntityProvider, IRedstoneConnectable {

    protected final Class<? extends T> tileEntityClass;
    private final BiFunction<EntityPlayer, IInventory, C> containerFactory;

    private boolean needsRedstoneCheck = false;
    private boolean hasRedstoneOutput = false;
    private IModuleSupport moduleSupport = null;

    @SideOnly(Side.CLIENT)
    private Class<? extends GenericGuiContainer<? super T>> guiClass;

    private int guiId = -1;

    public GenericBlock(ModBase mod,
                           Material material,
                           Class<? extends T> tileEntityClass,
                           BiFunction<EntityPlayer, IInventory, C> containerFactory,
                           String name, boolean isContainer) {
        this(mod, material, tileEntityClass, containerFactory, GenericItemBlock::new, name, isContainer);
    }

    @Deprecated
    public GenericBlock(ModBase mod,
                        Material material,
                        Class<? extends T> tileEntityClass,
                        BiFunction<EntityPlayer, IInventory, C> containerFactory,
                        Class<? extends ItemBlock> itemBlockClass,
                        String name, boolean isContainer) {
        this(mod, material, tileEntityClass, containerFactory, block -> {
            try {
                return itemBlockClass.getConstructor(Block.class).newInstance(block);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }, name, isContainer);
    }

    public GenericBlock(ModBase mod,
                        Material material,
                        Class<? extends T> tileEntityClass,
                        BiFunction<EntityPlayer, IInventory, C> containerFactory,
                        Function<Block, ItemBlock> itemBlockFactory,
                        String name, boolean isContainer) {
        super(mod, material, name, itemBlockFactory);
        this.hasTileEntity = isContainer;
        this.tileEntityClass = tileEntityClass;
        this.containerFactory = containerFactory;
        McJtyRegister.registerLater(this, tileEntityClass);
    }

    public boolean needsRedstoneCheck() {
        return needsRedstoneCheck;
    }

    public boolean hasRedstoneOutput() {
        return hasRedstoneOutput;
    }

    public void setNeedsRedstoneCheck(boolean needsRedstoneCheck) {
        this.needsRedstoneCheck = needsRedstoneCheck;
    }

    public void setHasRedstoneOutput(boolean hasRedstoneOutput) {
        this.hasRedstoneOutput = hasRedstoneOutput;
    }

    /**
     * @deprecated override {@link #shouldRedstoneConduitConnect(World, BlockPos, EnumFacing)} instead
     */
    @Deprecated
    public boolean shouldRedstoneConduitConnect(World world, int x, int y, int z, EnumFacing from) {
        return needsRedstoneCheck() || hasRedstoneOutput();
    }

    @Override
    @Optional.Method(modid = "enderio")
    public boolean shouldRedstoneConduitConnect(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing from) {
        return shouldRedstoneConduitConnect(world, pos.getX(), pos.getY(), pos.getZ(), from);
    }

    protected int getRedstoneOutput(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return -1;
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return hasRedstoneOutput();
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return getRedstoneOutput(state, world, pos, side);
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return getRedstoneOutput(state, world, pos, side);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof GenericTileEntity) {
            if (!world.isRemote) {
                GenericTileEntity genericTileEntity = (GenericTileEntity) te;
                genericTileEntity.onBlockBreak(world, pos, state);
            }
        }

        super.breakBlock(world, pos, state);
    }


    @Override
    @Optional.Method(modid = "theoneprobe")
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        BlockPos pos = data.getPos();
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof GenericTileEntity) {
            GenericTileEntity genericTileEntity = (GenericTileEntity) te;
            genericTileEntity.addProbeInfo(mode, probeInfo, player, world, blockState, data);

        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    @Optional.Method(modid = "waila")
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        currenttip = super.getWailaBody(itemStack, currenttip, accessor, config);
        TileEntity tileEntity = accessor.getTileEntity();
        if (tileEntity instanceof GenericTileEntity) {
            GenericTileEntity genericTileEntity = (GenericTileEntity) tileEntity;
            genericTileEntity.addWailaBody(itemStack, currenttip, accessor, config);
        }
        return currenttip;
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flags) {
        intAddInformation(stack, tooltip);
        super.addInformation(stack, world, tooltip, flags);
    }

    protected void intAddInformation(ItemStack itemStack, List<String> list) {
        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {
            if (tagCompound.hasKey("Energy")) {
                int energy = tagCompound.getInteger("Energy");
                list.add(TextFormatting.GREEN + "Energy: " + energy + " rf");
            }
            if (this instanceof Infusable) {
                int infused = tagCompound.getInteger("infused");
                int pct = infused * 100 / GeneralConfig.maxInfuse;
                list.add(TextFormatting.YELLOW + "Infused: " + pct + "%");
            }

            if (GeneralConfig.manageOwnership && tagCompound.hasKey("owner")) {
                String owner = tagCompound.getString("owner");
                int securityChannel = -1;
                if (tagCompound.hasKey("secChannel")) {
                    securityChannel = tagCompound.getInteger("secChannel");
                }

                if (securityChannel == -1) {
                    list.add(TextFormatting.YELLOW + "Owned by: " + owner);
                } else {
                    list.add(TextFormatting.YELLOW + "Owned by: " + owner + " (channel " + securityChannel + ")");
                }

                if (!tagCompound.hasKey("idM")) {
                    list.add(TextFormatting.RED + "Warning! Ownership not correctly set! Please place block again!");
                }
            }
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (needsRedstoneCheck()) {
            checkRedstone(world, pos);
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> result, IBlockAccess world, BlockPos pos, IBlockState metadata, int fortune) {
        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity instanceof GenericTileEntity) {
            ItemStack stack = new ItemStack(Item.getItemFromBlock(this));
            NBTTagCompound tagCompound = new NBTTagCompound();
            ((GenericTileEntity)tileEntity).writeRestorableToNBT(tagCompound);

            stack.setTagCompound(tagCompound);
            result.add(stack);

            ((GenericTileEntity) tileEntity).getDrops(result, world, pos, metadata, fortune);
        } else {
            super.getDrops(result, world, pos, metadata, fortune);
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (willHarvest) {
            return true; // If it will harvest, delay deletion of the block until after getDrops
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
        super.harvestBlock(world, player, pos, state, te, stack);
        world.setBlockToAir(pos);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return null;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState metadata) {
        try {
            return tileEntityClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // This if this block was activated with a wrench
    private WrenchUsage testWrenchUsage(BlockPos pos, EntityPlayer player) {
        ItemStack itemStack = player.getHeldItem(EnumHand.MAIN_HAND);
        WrenchUsage wrenchUsed = WrenchUsage.NOT;
        if (!itemStack.isEmpty()) {
            Item item = itemStack.getItem();
            if (item != null) {
                wrenchUsed = getWrenchUsage(pos, player, itemStack, wrenchUsed, item);
            }
        }
        if (wrenchUsed == WrenchUsage.NORMAL && player.isSneaking()) {
            wrenchUsed = WrenchUsage.SNEAKING;
        }
        return wrenchUsed;
    }

    protected WrenchUsage getWrenchUsage(BlockPos pos, EntityPlayer player, ItemStack itemStack, WrenchUsage wrenchUsed, Item item) {
        if (item instanceof SmartWrench) {
            switch(((SmartWrench)item).getMode(itemStack)) {
                case MODE_WRENCH: return WrenchUsage.NORMAL;
                case MODE_SELECT: return player.isSneaking() ? WrenchUsage.SNEAK_SELECT : WrenchUsage.SELECT;
                default:          throw new RuntimeException("SmartWrench in unknown mode!");
            }
        } else if (McJtyLib.cofhapiitem && CofhApiItemCompatibility.isToolHammer(item)) {
            return CofhApiItemCompatibility.getWrenchUsage(item, itemStack, player, pos);
        } else if (WrenchChecker.isAWrench(item)) {
            return WrenchUsage.NORMAL;
        }
        return wrenchUsed;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);
        if (handleModule(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ)) {
            return true;
        }
        WrenchUsage wrenchUsed = testWrenchUsage(pos, player);
        switch (wrenchUsed) {
            case NOT:          return openGui(world, pos.getX(), pos.getY(), pos.getZ(), player);
            case NORMAL:       return wrenchUse(world, pos, side,player);
            case SNEAKING:     return wrenchSneak(world, pos, player);
            case DISABLED:     return wrenchDisabled(world, pos, player);
            case SELECT:       return wrenchSelect(world, pos, player);
            case SNEAK_SELECT: return wrenchSneakSelect(world, pos, player);
        }
        return false;
    }

    protected IModuleSupport getModuleSupport() {
        return moduleSupport;
    }

    public void setModuleSupport(IModuleSupport moduleSupport) {
        this.moduleSupport = moduleSupport;
    }

    public boolean handleModule(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!heldItem.isEmpty()) {
            IModuleSupport support = getModuleSupport();
            if (support != null) {
                if (support.isModule(heldItem)) {
                    if (InventoryHelper.installModule(player, heldItem, hand, pos, support.getFirstSlot(), support.getLastSlot())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        boolean rc = super.rotateBlock(world, pos, axis);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof GenericTileEntity) {
            ((GenericTileEntity) tileEntity).rotateBlock(axis);
        }
        return rc;
    }

    protected boolean wrenchUse(World world, BlockPos pos, EnumFacing side, EntityPlayer player) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof GenericTileEntity) {
            if (!((GenericTileEntity) tileEntity).wrenchUse(world, pos, side, player)) {
                rotateBlock(world, pos, EnumFacing.UP);
            }
        } else {
            rotateBlock(world, pos, EnumFacing.UP);
        }
        return true;
    }

    protected boolean wrenchSneak(World world, BlockPos pos, EntityPlayer player) {
        breakAndRemember(world, player, pos);
        return true;
    }

    protected boolean wrenchDisabled(World world, BlockPos pos, EntityPlayer player) {
        return false;
    }

    protected boolean wrenchSelect(World world, BlockPos pos, EntityPlayer player) {
        return false;
    }

    protected boolean wrenchSneakSelect(World world, BlockPos pos, EntityPlayer player) {
        return false;
    }

    protected boolean openGui(World world, int x, int y, int z, EntityPlayer player) {
        if (getGuiID() != -1) {
            if (world.isRemote) {
                return true;
            }
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
//            if (isBlockContainer && !tileEntityClass.isInstance(te)) {
//                return false;
//            }
            if (checkAccess(world, player, te)) {
                return true;
            }
            player.openGui(modBase, getGuiID(), world, x, y, z);
            return true;
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        restoreBlockFromNBT(world, pos, stack);
        if (!world.isRemote && GeneralConfig.manageOwnership) {
            setOwner(world, pos, placer);
        }

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof GenericTileEntity) {
            GenericTileEntity genericTileEntity = (GenericTileEntity) te;
            genericTileEntity.onBlockPlacedBy(world, pos, state, placer, stack);
        }

        if (needsRedstoneCheck()) {
            checkRedstone(world, pos);
        }
    }

    protected void setOwner(World world, BlockPos pos, EntityLivingBase entityLivingBase) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof GenericTileEntity && entityLivingBase instanceof EntityPlayer) {
            GenericTileEntity genericTileEntity = (GenericTileEntity) te;
            EntityPlayer player = (EntityPlayer) entityLivingBase;
            genericTileEntity.setOwner(player);
        }
    }

    @Override
    public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    protected void checkRedstone(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof GenericTileEntity) {
            ((GenericTileEntity) te).checkRedstone(world, pos);
        }
    }

    /**
     * Break a block in the world, convert it to an entity and remember all the settings
     * for this block in the itemstack.
     */
    protected void breakAndRemember(World world, EntityPlayer player, BlockPos pos) {
        if (!world.isRemote) {
            harvestBlock(world, player, pos, world.getBlockState(pos), world.getTileEntity(pos), ItemStack.EMPTY);
//            world.setBlockToAir(x, y, z);
        }
    }

    /**
     * Restore a block from an itemstack (with NBT).
     * @param world
     * @param pos
     * @param itemStack
     */
    protected void restoreBlockFromNBT(World world, BlockPos pos, ItemStack itemStack) {
        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof GenericTileEntity) {
                ((GenericTileEntity)te).readRestorableFromNBT(tagCompound);
            }
        }
    }

    /**
     * Return the id of the gui to use for this block.
     */
    public int getGuiID() {
        return guiId;
    }

    public void setGuiId(int guiId) {
        this.guiId = guiId;
    }

    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
        if (hasTileEntity) {
            super.eventReceived(state, worldIn, pos, id, param);
            TileEntity tileentity = worldIn.getTileEntity(pos);
            return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
        } else {
            return super.eventReceived(state, worldIn, pos, id, param);
        }
    }

    @SideOnly(Side.CLIENT)
    public Class<? extends GenericGuiContainer<? super T>> getGuiClass() {
        return guiClass;
    }

    @SideOnly(Side.CLIENT)
    public void setGuiClass(Class<? extends GenericGuiContainer<? super T>> guiClass) {
        this.guiClass = guiClass;
    }

    @SideOnly(Side.CLIENT)
    public GuiContainer createClientGui(EntityPlayer entityPlayer, TileEntity tileEntity) {
        IInventory inventory = tileEntity instanceof IInventory ? (IInventory) tileEntity : null;
        C container;
        GenericGuiContainer<? super T> gui;
        try {
            container = containerFactory.apply(entityPlayer, inventory);
            Constructor<? extends GenericGuiContainer<? super T>> guiConstructor = getGuiClass().getConstructor(tileEntityClass, container.getClass());
            gui = guiConstructor.newInstance(tileEntity, container);
            return gui;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            Logging.logError("Severe exception during creation of gui!");
            throw new RuntimeException(e);
        }
    }

    public Container createServerContainer(EntityPlayer entityPlayer, TileEntity tileEntity) {
        if (tileEntity instanceof IInventory) {
            return containerFactory.apply(entityPlayer, (IInventory) tileEntity);
        } else {
            return containerFactory.apply(entityPlayer, null);
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity te = world instanceof ChunkCache ? ((ChunkCache)world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) : world.getTileEntity(pos);
        if (te instanceof GenericTileEntity) {
            return ((GenericTileEntity) te).getActualState(state);
        }
        return super.getActualState(state, world, pos);
    }

    protected boolean checkAccess(World world, EntityPlayer player, TileEntity te) {
        if (te instanceof GenericTileEntity) {
            ((GenericTileEntity) te).checkAccess(player);
        }
        return false;
    }
}
