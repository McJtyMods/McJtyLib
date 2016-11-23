package mcjty.lib.container;

import cofh.api.item.IToolHammer;
import crazypants.enderio.api.redstone.IRedstoneConnectable;
import mcjty.lib.api.IModuleSupport;
import mcjty.lib.api.Infusable;
import mcjty.lib.api.smartwrench.SmartWrench;
import mcjty.lib.api.smartwrench.SmartWrenchMode;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.base.ModBase;
import mcjty.lib.compat.CompatBlock;
import mcjty.lib.compat.theoneprobe.TOPInfoProvider;
import mcjty.lib.compat.waila.WailaInfoProvider;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.tools.ItemStackTools;
import mcjty.lib.varia.BlockTools;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.WrenchChecker;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class GenericBlock<T extends GenericTileEntity, C extends Container>
        extends CompatBlock
        implements ITileEntityProvider, WailaInfoProvider, TOPInfoProvider, IRedstoneConnectable {

    public static final PropertyDirection FACING_HORIZ = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyDirection FACING = PropertyDirection.create("facing");

    protected ModBase modBase;
    protected final Class<? extends TileEntity> tileEntityClass;
    private final Class<? extends C> containerClass;

    private boolean creative;

    private void init(ModBase mod, boolean isContainer) {
        this.modBase = mod;
        this.isBlockContainer = isContainer;
        this.creative = false;
        setHardness(2.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 0);
    }

    public GenericBlock(ModBase mod,
                           Material material,
                           Class<? extends T> tileEntityClass,
                           Class<? extends C> containerClass,
                           String name, boolean isContainer) {
        this(mod, material, tileEntityClass, containerClass, GenericItemBlock.class, name, isContainer);
    }

    public GenericBlock(ModBase mod,
                           Material material,
                           Class<? extends T> tileEntityClass,
                           Class<? extends C> containerClass,
                           Class<? extends ItemBlock> itemBlockClass,
                           String name, boolean isContainer) {
        super(material);
        init(mod, isContainer);
        this.tileEntityClass = tileEntityClass;
        this.containerClass = containerClass;
        setUnlocalizedName(mod.getModId() + "." + name);
        setRegistryName(name);
        GameRegistry.register(this);
        if (itemBlockClass != null) {
            GameRegistry.register(createItemBlock(itemBlockClass), getRegistryName());
        }
        GameRegistry.registerTileEntityWithAlternatives(tileEntityClass, mod.getModId() + "_" + name, name);
    }

    private ItemBlock createItemBlock(Class<? extends ItemBlock> itemBlockClass) {
        try {
            Class<?>[] ctorArgClasses = new Class<?>[1];
            ctorArgClasses[0] = Block.class;
            Constructor<? extends ItemBlock> itemCtor = itemBlockClass.getConstructor(ctorArgClasses);
            return itemCtor.newInstance(this);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }


    // Set this to true in case horizontal rotation is used (2 bits rotation as opposed to 3).
    public boolean isHorizRotation() {
        return false;
    }

    // Override and return true if this block has no rotation needed (overrides isHorizRotation)
    public boolean hasNoRotation() { return false; }

    public boolean isCreative() {
        return creative;
    }

    public boolean needsRedstoneCheck() {
        return false;
    }

    public boolean hasRedstoneOutput() {
        return false;
    }

    @Override
    public boolean shouldRedstoneConduitConnect(World world, int x, int y, int z, EnumFacing from) {
        return needsRedstoneCheck() || hasRedstoneOutput();
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
    protected void clOnNeighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn) {
        if (needsRedstoneCheck()) {
            checkRedstone(world, pos);
        }
    }

    public void setCreative(boolean creative) {
        this.creative = creative;
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
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        BlockPos pos = data.getPos();
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof GenericTileEntity) {
            GenericTileEntity genericTileEntity = (GenericTileEntity) te;
            if (blockState.getBlock() instanceof Infusable) {
                int infused = genericTileEntity.getInfused();
                int pct = infused * 100 / GeneralConfig.maxInfuse;
                probeInfo.text(TextFormatting.YELLOW + "Infused: " + pct + "%");
            }
            if (mode == ProbeMode.EXTENDED) {
                if (GeneralConfig.manageOwnership) {
                    if (genericTileEntity.getOwnerName() != null && !genericTileEntity.getOwnerName().isEmpty()) {
                        int securityChannel = genericTileEntity.getSecurityChannel();
                        if (securityChannel == -1) {
                            probeInfo.text(TextFormatting.YELLOW + "Owned by: " + genericTileEntity.getOwnerName());
                        } else {
                            probeInfo.text(TextFormatting.YELLOW + "Owned by: " + genericTileEntity.getOwnerName() + " (channel " + securityChannel + ")");
                        }
                        if (genericTileEntity.getOwnerUUID() == null) {
                            probeInfo.text(TextFormatting.RED + "Warning! Ownership not correctly set! Please place block again!");
                        }
                    }
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Block block = accessor.getBlock();
        TileEntity tileEntity = accessor.getTileEntity();
        if (tileEntity instanceof GenericTileEntity) {
            GenericTileEntity genericTileEntity = (GenericTileEntity) tileEntity;
            if (block instanceof Infusable) {
                int infused = genericTileEntity.getInfused();
                int pct = infused * 100 / GeneralConfig.maxInfuse;
                currenttip.add(TextFormatting.YELLOW + "Infused: " + pct + "%");
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                if (GeneralConfig.manageOwnership) {
                    if (genericTileEntity.getOwnerName() != null && !genericTileEntity.getOwnerName().isEmpty()) {
                        int securityChannel = genericTileEntity.getSecurityChannel();
                        if (securityChannel == -1) {
                            currenttip.add(TextFormatting.YELLOW + "Owned by: " + genericTileEntity.getOwnerName());
                        } else {
                            currenttip.add(TextFormatting.YELLOW + "Owned by: " + genericTileEntity.getOwnerName() + " (channel " + securityChannel + ")");
                        }
                        if (genericTileEntity.getOwnerUUID() == null) {
                            currenttip.add(TextFormatting.RED + "Warning! Ownership not correctly set! Please place block again!");
                        }
                    }
                }
            }
        }
        return currenttip;
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean advancedToolTip) {
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
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState metadata, int fortune) {
        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity instanceof GenericTileEntity) {
            ItemStack stack = new ItemStack(Item.getItemFromBlock(this));
            NBTTagCompound tagCompound = new NBTTagCompound();
            ((GenericTileEntity)tileEntity).writeRestorableToNBT(tagCompound);

            stack.setTagCompound(tagCompound);
            List<ItemStack> result = new ArrayList<>();
            result.add(stack);
            return result;
        } else {
            return super.getDrops(world, pos, metadata, fortune);
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (willHarvest) return true; // If it will harvest, delay deletion of the block until after getDrops
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
        if (ItemStackTools.isValid(itemStack)) {
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

    private WrenchUsage getWrenchUsageInt(BlockPos pos, EntityPlayer player, ItemStack itemStack, WrenchUsage wrenchUsed, Item item) {
        if (item instanceof IToolHammer) {
            IToolHammer hammer = (IToolHammer) item;
            if (hammer.isUsable(itemStack, player, pos)) {
                hammer.toolUsed(itemStack, player, pos);
                wrenchUsed = WrenchUsage.NORMAL;
            } else {
                wrenchUsed = WrenchUsage.DISABLED;
            }
        } else if (WrenchChecker.isAWrench(item)) {
            wrenchUsed = WrenchUsage.NORMAL;
        }
        return wrenchUsed;
    }

    protected WrenchUsage getWrenchUsage(BlockPos pos, EntityPlayer player, ItemStack itemStack, WrenchUsage wrenchUsed, Item item) {
        WrenchUsage usage = getWrenchUsageInt(pos, player, itemStack, wrenchUsed, item);
        if (item instanceof IToolHammer && usage == WrenchUsage.DISABLED) {
            // It is still possible it is a smart wrench.
            if (item instanceof SmartWrench) {
                SmartWrench smartWrench = (SmartWrench) item;
                SmartWrenchMode mode = smartWrench.getMode(itemStack);
                if (mode.equals(SmartWrenchMode.MODE_SELECT)) {
                    if (player.isSneaking()) {
                        usage = WrenchUsage.SNEAK_SELECT;
                    } else {
                        usage = WrenchUsage.SELECT;
                    }
                }
            }
        }
        return usage;
    }

    @Override
    protected boolean clOnBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
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
        return null;
    }

    public boolean handleModule(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (ItemStackTools.isValid(heldItem)) {
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



    protected boolean wrenchUse(World world, BlockPos pos, EnumFacing side, EntityPlayer player) {
        rotateBlock(world, pos, EnumFacing.UP);
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
            if (isBlockContainer && !tileEntityClass.isInstance(te)) {
                return false;
            }
            if (checkAccess(world, player, te))
                return true;
            player.openGui(modBase, getGuiID(), world, x, y, z);
            return true;
        }
        return false;
    }

    protected EnumFacing getOrientation(BlockPos pos, EntityLivingBase entityLivingBase) {
        if (hasNoRotation()) {
            return null;
        } else if (isHorizRotation()) {
            return BlockTools.determineOrientationHoriz(entityLivingBase);
        } else {
            return BlockTools.determineOrientation(pos, entityLivingBase);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (hasNoRotation()) {
        } else if (isHorizRotation()) {
            world.setBlockState(pos, state.withProperty(FACING_HORIZ, placer.getHorizontalFacing().getOpposite()), 2);
        } else {
            world.setBlockState(pos, state.withProperty(FACING, getFacingFromEntity(pos, placer)), 2);
        }
        restoreBlockFromNBT(world, pos, stack);
        if (!world.isRemote && GeneralConfig.manageOwnership) {
            setOwner(world, pos, placer);
        }

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof GenericTileEntity) {
            GenericTileEntity genericTileEntity = (GenericTileEntity) te;
            genericTileEntity.onBlockPlacedBy(world, pos, state, placer, stack);
        }
    }

    public static EnumFacing getFacingFromEntity(BlockPos clickedBlock, EntityLivingBase entityIn) {
        if (MathHelper.abs((float) entityIn.posX - clickedBlock.getX()) < 2.0F && MathHelper.abs((float) entityIn.posZ - clickedBlock.getZ()) < 2.0F) {
            double d0 = entityIn.posY + entityIn.getEyeHeight();

            if (d0 - clickedBlock.getY() > 2.0D) {
                return EnumFacing.UP;
            }

            if (clickedBlock.getY() - d0 > 0.0D) {
                return EnumFacing.DOWN;
            }
        }

        return entityIn.getHorizontalFacing().getOpposite();
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
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        return super.rotateBlock(world, pos, axis);
    }


    @Override
    public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    protected void checkRedstone(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof GenericTileEntity) {
            int powered = world.isBlockIndirectlyGettingPowered(pos); //TODO: check
            GenericTileEntity genericTileEntity = (GenericTileEntity) te;
            genericTileEntity.setPowerInput(powered);
        }
    }

    /**
     * Break a block in the world, convert it to an entity and remember all the settings
     * for this block in the itemstack.
     */
    protected void breakAndRemember(World world, EntityPlayer player, BlockPos pos) {
        if (!world.isRemote) {
            harvestBlock(world, player, pos, world.getBlockState(pos), world.getTileEntity(pos), null);
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

    public EnumFacing getOrientation(int meta) {
        EnumFacing k;
        if (hasNoRotation()) {
            k = null;
        } else if (isHorizRotation()) {
            k = BlockTools.getOrientationHoriz(meta);
        } else {
            k = BlockTools.getOrientation(meta);
        }
        return k;
    }

    /**
     * Return the id of the gui to use for this block.
     */
    public abstract int getGuiID();

    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
        if (isBlockContainer) {
            super.eventReceived(state, worldIn, pos, id, param);
            TileEntity tileentity = worldIn.getTileEntity(pos);
            return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
        } else {
            return super.eventReceived(state, worldIn, pos, id, param);
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        if (hasNoRotation()) {
            return getDefaultState();
        } else if (isHorizRotation()) {
            return getDefaultState().withProperty(FACING_HORIZ, getFacingHoriz(meta));
        } else {
            return getDefaultState().withProperty(FACING, getFacing(meta));
        }
    }

    public static EnumFacing getFacingHoriz(int meta) {
        return EnumFacing.values()[meta+2];
    }

    public static EnumFacing getFacing(int meta) {
        return EnumFacing.values()[meta & 7];
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        if (hasNoRotation()) {
            return 0;
        } else if (isHorizRotation()) {
            return state.getValue(FACING_HORIZ).getIndex()-2;
        } else {
            return state.getValue(FACING).getIndex();
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        if (hasNoRotation()) {
            return new BlockStateContainer(this);
        } else if (isHorizRotation()) {
            return new BlockStateContainer(this, FACING_HORIZ);
        } else {
            return new BlockStateContainer(this, FACING);
        }
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @SideOnly(Side.CLIENT)
    public Class<? extends GenericGuiContainer> getGuiClass() {
        return null;
    }

    @SideOnly(Side.CLIENT)
    public GuiContainer createClientGui(EntityPlayer entityPlayer, TileEntity tileEntity) {
        T inventory = (T) tileEntity;
        C container;
        GenericGuiContainer gui;
        try {
            Constructor<? extends C> constructor = containerClass.getConstructor(EntityPlayer.class, IInventory.class);
            container = constructor.newInstance(entityPlayer, inventory instanceof IInventory ? inventory : null);
            Constructor<? extends GenericGuiContainer> guiConstructor = getGuiClass().getConstructor(tileEntityClass, containerClass);
            gui = guiConstructor.newInstance(inventory, container);
            return gui;
        } catch (Exception e) {
            Logging.logError("Severe exception during creation of gui!");
            throw new RuntimeException(e);
        }
    }

    public Container createServerContainer(EntityPlayer entityPlayer, TileEntity tileEntity) {
        T inventory = (T) tileEntity;
        C container;
        try {
            Constructor<? extends C> constructor = containerClass.getConstructor(EntityPlayer.class, IInventory.class);
            container = constructor.newInstance(entityPlayer, inventory instanceof IInventory ? inventory : null);
            return container;
        } catch (Exception e) {
            Logging.logError("Severe exception during creation of gui!");
            throw new RuntimeException(e);
        }
    }

    protected boolean checkAccess(World world, EntityPlayer player, TileEntity te) {
//        if (te instanceof GenericTileEntity) {
//            GenericTileEntity genericTileEntity = (GenericTileEntity) te;
//            if ((!SecurityTools.isPrivileged(player, world)) && (!player.getPersistentID().equals(genericTileEntity.getOwnerUUID()))) {
//                int securityChannel = genericTileEntity.getSecurityChannel();
//                if (securityChannel != -1) {
//                    SecurityChannels securityChannels = SecurityChannels.getChannels(world);
//                    SecurityChannels.SecurityChannel channel = securityChannels.getChannel(securityChannel);
//                    boolean playerListed = channel.getPlayers().contains(player.getDisplayNameString());
//                    if (channel.isWhitelist() != playerListed) {
//                        Logging.message(player, TextFormatting.RED + "You have no permission to use this block!");
//                        return true;
//                    }
//                }
//            }
//        }
        // @todo, security system from RFTools should be accessed here
        return false;
    }

}
