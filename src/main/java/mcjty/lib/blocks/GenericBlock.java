package mcjty.lib.blocks;

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
import mcjty.lib.varia.WrenchChecker;
import mcjty.lib.varia.WrenchUsage;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

//@Optional.InterfaceList({
//        @Optional.Interface(iface = "crazypants.enderio.api.redstone.IRedstoneConnectable", modid = "enderio"),
//})
public abstract class GenericBlock<T extends GenericTileEntity, C extends Container> extends BaseBlock
        implements ITileEntityProvider /*, IRedstoneConnectable*/ {

    protected final Class<? extends T> tileEntityClass;
    private final BiFunction<PlayerEntity, IInventory, C> containerFactory;

    private boolean needsRedstoneCheck = false;
    private boolean hasRedstoneOutput = false;
    private boolean infusable = this instanceof Infusable;
    private IModuleSupport moduleSupport = null;

    // Client side
    private BiFunction<T, C, GenericGuiContainer<? super T>> guiFactory;

    private int guiId = -1;

    public GenericBlock(ModBase mod,
                           Material material,
                           Class<? extends T> tileEntityClass,
                           BiFunction<PlayerEntity, IInventory, C> containerFactory,
                           String name, boolean isContainer) {
        this(mod, material, tileEntityClass, containerFactory, GenericItemBlock::new, name, isContainer);
    }

    public GenericBlock(ModBase mod,
                        Material material,
                        Class<? extends T> tileEntityClass,
                        BiFunction<PlayerEntity, IInventory, C> containerFactory,
                        Function<Block, BlockItem> itemBlockFactory,
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

    public void setInfusable(boolean infusable) {
        this.infusable = infusable;
    }

    public boolean isInfusable() {
        return infusable;
    }

    public void setNeedsRedstoneCheck(boolean needsRedstoneCheck) {
        this.needsRedstoneCheck = needsRedstoneCheck;
    }

    public void setHasRedstoneOutput(boolean hasRedstoneOutput) {
        this.hasRedstoneOutput = hasRedstoneOutput;
    }

    @Deprecated
    public boolean shouldRedstoneConduitConnect(World world, int x, int y, int z, Direction from) {
        throw new AbstractMethodError();
    }

    @Override
    @Optional.Method(modid = "enderio")
    public boolean shouldRedstoneConduitConnect(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Direction from) {
        return needsRedstoneCheck() || hasRedstoneOutput();
    }

    protected int getRedstoneOutput(BlockState state, IBlockAccess world, BlockPos pos, Direction side) {
        return -1;
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return hasRedstoneOutput();
    }

    @Override
    public int getWeakPower(BlockState state, IBlockAccess world, BlockPos pos, Direction side) {
        return getRedstoneOutput(state, world, pos, side);
    }

    @Override
    public int getStrongPower(BlockState state, IBlockAccess world, BlockPos pos, Direction side) {
        return getRedstoneOutput(state, world, pos, side);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, BlockState state) {
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
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        BlockPos pos = data.getPos();
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof GenericTileEntity) {
            GenericTileEntity genericTileEntity = (GenericTileEntity) te;
            genericTileEntity.addProbeInfo(mode, probeInfo, player, world, blockState, data);

        }
    }

    @Override
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
        CompoundNBT tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {
            if (tagCompound.hasKey("Energy")) {
                long energy = tagCompound.getLong("Energy");
                list.add(TextFormatting.GREEN + "Energy: " + energy + " rf");
            }
            if (isInfusable()) {
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
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (needsRedstoneCheck()) {
            checkRedstone(world, pos);
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> result, IBlockAccess world, BlockPos pos, BlockState metadata, int fortune) {
        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity instanceof GenericTileEntity) {
            ItemStack stack = new ItemStack(Item.getItemFromBlock(this));
            CompoundNBT tagCompound = new CompoundNBT();
            ((GenericTileEntity)tileEntity).writeRestorableToNBT(tagCompound);

            stack.setTagCompound(tagCompound);
            result.add(stack);

            ((GenericTileEntity) tileEntity).getDrops(result, world, pos, metadata, fortune);
        } else {
            super.getDrops(result, world, pos, metadata, fortune);
        }
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest) {
        if (willHarvest) {
            return true; // If it will harvest, delay deletion of the block until after getDrops
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack stack) {
        super.harvestBlock(world, player, pos, state, te, stack);
        world.setBlockToAir(pos);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return null;
    }

    @Override
    public TileEntity createTileEntity(World world, BlockState metadata) {
        try {
            return tileEntityClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // This if this block was activated with a wrench
    private WrenchUsage testWrenchUsage(BlockPos pos, PlayerEntity player) {
        ItemStack itemStack = player.getHeldItem(Hand.MAIN_HAND);
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

    protected WrenchUsage getWrenchUsage(BlockPos pos, PlayerEntity player, ItemStack itemStack, WrenchUsage wrenchUsed, Item item) {
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
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof GenericTileEntity) {
            if (((GenericTileEntity) te).onBlockActivated(state, player, hand, side, hitX, hitY, hitZ)) {
                return true;
            }
        }
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

    public boolean handleModule(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, ItemStack heldItem, Direction side, float hitX, float hitY, float hitZ) {
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
    public boolean rotateBlock(World world, BlockPos pos, Direction axis) {
        boolean rc = super.rotateBlock(world, pos, axis);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof GenericTileEntity) {
            ((GenericTileEntity) tileEntity).rotateBlock(axis);
        }
        return rc;
    }

    protected boolean wrenchUse(World world, BlockPos pos, Direction side, PlayerEntity player) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof GenericTileEntity) {
            if (!((GenericTileEntity) tileEntity).wrenchUse(world, pos, side, player)) {
                rotateBlock(world, pos, Direction.UP);
            }
        } else {
            rotateBlock(world, pos, Direction.UP);
        }
        return true;
    }

    protected boolean wrenchSneak(World world, BlockPos pos, PlayerEntity player) {
        breakAndRemember(world, player, pos);
        return true;
    }

    protected boolean wrenchDisabled(World world, BlockPos pos, PlayerEntity player) {
        return false;
    }

    protected boolean wrenchSelect(World world, BlockPos pos, PlayerEntity player) {
        return false;
    }

    protected boolean wrenchSneakSelect(World world, BlockPos pos, PlayerEntity player) {
        return false;
    }

    protected boolean openGui(World world, int x, int y, int z, PlayerEntity player) {
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
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, MobEntity placer, ItemStack stack) {
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

    protected void setOwner(World world, BlockPos pos, MobEntity MobEntity) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof GenericTileEntity && MobEntity instanceof PlayerEntity) {
            GenericTileEntity genericTileEntity = (GenericTileEntity) te;
            PlayerEntity player = (PlayerEntity) MobEntity;
            genericTileEntity.setOwner(player);
        }
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, IBlockAccess world, BlockPos pos, Direction side) {
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
    protected void breakAndRemember(World world, PlayerEntity player, BlockPos pos) {
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
        CompoundNBT tagCompound = itemStack.getTagCompound();
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
    public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
        if (hasTileEntity) {
            super.eventReceived(state, worldIn, pos, id, param);
            TileEntity tileentity = worldIn.getTileEntity(pos);
            return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
        } else {
            return super.eventReceived(state, worldIn, pos, id, param);
        }
    }

    @SideOnly(Side.CLIENT)
    public BiFunction<T, C, GenericGuiContainer<? super T>> getGuiFactory() {
        return guiFactory;
    }

    @SideOnly(Side.CLIENT)
    public void setGuiFactory(BiFunction<T, C, GenericGuiContainer<? super T>> guiFactory) {
        this.guiFactory = guiFactory;
    }

    @SideOnly(Side.CLIENT)
    public GuiContainer createClientGui(PlayerEntity PlayerEntity, TileEntity tileEntity) {
        IInventory inventory = tileEntity instanceof IInventory ? (IInventory) tileEntity : null;
        C container = containerFactory.apply(PlayerEntity, inventory);
        return getGuiFactory().apply((T) tileEntity, container);
    }

    public Container createServerContainer(PlayerEntity PlayerEntity, TileEntity tileEntity) {
        if (tileEntity instanceof IInventory) {
            return containerFactory.apply(PlayerEntity, (IInventory) tileEntity);
        } else {
            return containerFactory.apply(PlayerEntity, null);
        }
    }

    @Override
    public BlockState getActualState(BlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity te = world instanceof ChunkCache ? ((ChunkCache)world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) : world.getTileEntity(pos);
        if (te instanceof GenericTileEntity) {
            return ((GenericTileEntity) te).getActualState(state);
        }
        return super.getActualState(state, world, pos);
    }

    protected boolean checkAccess(World world, PlayerEntity player, TileEntity te) {
        if (te instanceof GenericTileEntity) {
            ((GenericTileEntity) te).checkAccess(player);
        }
        return false;
    }

    public ItemStack getItem(World world, BlockPos pos, BlockState state) {
        ItemStack stack = super.getItem(world, pos, state);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof GenericTileEntity) {
            CompoundNBT tagCompound = new CompoundNBT();
            ((GenericTileEntity)te).writeRestorableToNBT(tagCompound);
            stack.setTagCompound(tagCompound);
        }
        return stack;
    }
}
