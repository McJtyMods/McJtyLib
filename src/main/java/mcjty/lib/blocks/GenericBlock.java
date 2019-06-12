package mcjty.lib.blocks;

import mcjty.lib.McJtyLib;
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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

//@Optional.InterfaceList({
//        @Optional.Interface(iface = "crazypants.enderio.api.redstone.IRedstoneConnectable", modid = "enderio"),
//})
public abstract class GenericBlock<T extends GenericTileEntity, C extends Container> extends BaseBlock
        /*, IRedstoneConnectable*/ {

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
        this.tileEntityClass = tileEntityClass;
        this.containerFactory = containerFactory;
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

//    @Override
//    @Optional.Method(modid = "enderio")
//    public boolean shouldRedstoneConduitConnect(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Direction from) {
//        return needsRedstoneCheck() || hasRedstoneOutput();
//    }

    protected int getRedstoneOutput(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        return -1;
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return hasRedstoneOutput();
    }

    @Override
    public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        return getRedstoneOutput(state, world, pos, side);
    }

    @Override
    public int getStrongPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        return getRedstoneOutput(state, world, pos, side);
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newstate, boolean isMoving) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof GenericTileEntity) {
            if (!world.isRemote) {
                GenericTileEntity genericTileEntity = (GenericTileEntity) te;
                genericTileEntity.onReplaced(world, pos, state);
            }
        }

        super.onReplaced(state, world, pos, newstate, isMoving);
    }

// @todo 1.14
//    @Override
//    @Optional.Method(modid = "theoneprobe")
//    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
//        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
//        BlockPos pos = data.getPos();
//        TileEntity te = world.getTileEntity(pos);
//        if (te instanceof GenericTileEntity) {
//            GenericTileEntity genericTileEntity = (GenericTileEntity) te;
//            genericTileEntity.addProbeInfo(mode, probeInfo, player, world, blockState, data);
//
//        }
//    }
//
//    @Override
//    @Optional.Method(modid = "waila")
//    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
//        currenttip = super.getWailaBody(itemStack, currenttip, accessor, config);
//        TileEntity tileEntity = accessor.getTileEntity();
//        if (tileEntity instanceof GenericTileEntity) {
//            GenericTileEntity genericTileEntity = (GenericTileEntity) tileEntity;
//            genericTileEntity.addWailaBody(itemStack, currenttip, accessor, config);
//        }
//        return currenttip;
//    }


    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        intAddInformation(stack, tooltip);
        super.addInformation(stack, world, tooltip, advanced);
    }

    protected void intAddInformation(ItemStack itemStack, List<ITextComponent> list) {
        CompoundNBT tagCompound = itemStack.getTag();
        if (tagCompound != null) {
            if (tagCompound.contains("Energy")) {
                long energy = tagCompound.getLong("Energy");
                list.add(new StringTextComponent(TextFormatting.GREEN + "Energy: " + energy + " rf"));
            }
            if (isInfusable()) {
                int infused = tagCompound.getInt("infused");
                int pct = infused * 100 / GeneralConfig.maxInfuse.get();
                list.add(new StringTextComponent(TextFormatting.YELLOW + "Infused: " + pct + "%"));
            }

            if (GeneralConfig.manageOwnership.get() && tagCompound.contains("owner")) {
                String owner = tagCompound.getString("owner");
                int securityChannel = -1;
                if (tagCompound.contains("secChannel")) {
                    securityChannel = tagCompound.getInt("secChannel");
                }

                if (securityChannel == -1) {
                    list.add(new StringTextComponent(TextFormatting.YELLOW + "Owned by: " + owner));
                } else {
                    list.add(new StringTextComponent(TextFormatting.YELLOW + "Owned by: " + owner + " (channel " + securityChannel + ")"));
                }

                if (!tagCompound.contains("idM")) {
                    list.add(new StringTextComponent(TextFormatting.RED + "Warning! Ownership not correctly set! Please place block again!"));
                }
            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean noIdea) {
        if (needsRedstoneCheck()) {
            checkRedstone(world, pos);
        }
    }

    // @todo 1.14
//    @Override
//    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
//        ServerWorld world = builder.func_216018_a();
//        TileEntity tileEntity = world.getTileEntity(builder.pos);
//
//        if (tileEntity instanceof GenericTileEntity) {
//            ItemStack stack = new ItemStack(Item.getItemFromBlock(this));
//            CompoundNBT tagCompound = new CompoundNBT();
//            ((GenericTileEntity)tileEntity).writeRestorableToNBT(tagCompound);
//
//            stack.setTagCompound(tagCompound);
//            result.add(stack);
//
//            ((GenericTileEntity) tileEntity).getDrops(result, world, pos, metadata, fortune);
//        } else {
//            super.getDrops(result, world, pos, metadata, fortune);
//        }
//    }


    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        if (willHarvest) {
            return true; // If it will harvest, delay deletion of the block until after getDrops
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    @Override
    public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack stack) {
        super.harvestBlock(world, player, pos, state, te, stack);
        world.setBlockState(pos, Blocks.AIR.getDefaultState());
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
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
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof GenericTileEntity) {
            if (((GenericTileEntity) te).onBlockActivated(state, player, hand, result)) {
                return true;
            }
        }
        ItemStack heldItem = player.getHeldItem(hand);
        if (handleModule(world, pos, state, player, hand, heldItem, result)) {
            return true;
        }
        WrenchUsage wrenchUsed = testWrenchUsage(pos, player);
        switch (wrenchUsed) {
            case NOT:          return openGui(world, pos.getX(), pos.getY(), pos.getZ(), player);
            case NORMAL:       return wrenchUse(world, pos, result.getFace(), player);
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

    public boolean handleModule(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, ItemStack heldItem, BlockRayTraceResult result) {
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
    public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation rot) {
        BlockState rotated = super.rotate(state, world, pos, rot);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof GenericTileEntity) {
            ((GenericTileEntity) tileEntity).rotateBlock(rot);
        }
        return rotated;
    }

    protected boolean wrenchUse(World world, BlockPos pos, Direction side, PlayerEntity player) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof GenericTileEntity) {
            if (!((GenericTileEntity) tileEntity).wrenchUse(world, pos, side, player)) {
                rotate(world.getBlockState(pos), world, pos, Rotation.CLOCKWISE_90);
            }
        } else {
            rotate(world.getBlockState(pos), world, pos, Rotation.CLOCKWISE_90);
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
            if (!(te instanceof INamedContainerProvider)) {
                return true;
            }
            if (checkAccess(world, player, te)) {
                return true;
            }
            NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) te, te.getPos());
            // @todo 1.14
            // Use ScreenManager on client
//            player.openGui(modBase, getGuiID(), world, x, y, z);
            return true;
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        restoreBlockFromNBT(world, pos, stack);
        if (!world.isRemote && GeneralConfig.manageOwnership.get()) {
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

    protected void setOwner(World world, BlockPos pos, LivingEntity MobEntity) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof GenericTileEntity && MobEntity instanceof PlayerEntity) {
            GenericTileEntity genericTileEntity = (GenericTileEntity) te;
            PlayerEntity player = (PlayerEntity) MobEntity;
            genericTileEntity.setOwner(player);
        }
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side) {
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
        CompoundNBT tagCompound = itemStack.getTag();
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
        if (hasTileEntity(state)) {
            super.eventReceived(state, worldIn, pos, id, param);
            TileEntity tileentity = worldIn.getTileEntity(pos);
            return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
        } else {
            return super.eventReceived(state, worldIn, pos, id, param);
        }
    }

    // Client side
    public BiFunction<T, C, GenericGuiContainer<? super T>> getGuiFactory() {
        return guiFactory;
    }

    // Client side
    public void setGuiFactory(BiFunction<T, C, GenericGuiContainer<? super T>> guiFactory) {
        this.guiFactory = guiFactory;
    }

    // Client side
    public ContainerScreen createClientGui(PlayerEntity PlayerEntity, TileEntity tileEntity) {
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


    //@todo 1.14
//    @Override
//    public BlockState getActualState(BlockState state, IBlockReader world, BlockPos pos) {
//        TileEntity te = world instanceof ChunkCache ? ((ChunkCache)world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) : world.getTileEntity(pos);
//        if (te instanceof GenericTileEntity) {
//            return ((GenericTileEntity) te).getActualState(state);
//        }
//        return super.getActualState(state, world, pos);
//    }

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
            stack.setTag(tagCompound);
        }
        return stack;
    }
}
