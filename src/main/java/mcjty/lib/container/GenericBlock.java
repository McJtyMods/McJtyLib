package mcjty.lib.container;

import cofh.api.item.IToolHammer;
import crazypants.enderio.api.redstone.IRedstoneConnectable;
import mcjty.lib.McJtyRegister;
import mcjty.lib.api.IModuleSupport;
import mcjty.lib.api.Infusable;
import mcjty.lib.api.smartwrench.SmartWrench;
import mcjty.lib.api.smartwrench.SmartWrenchMode;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.base.ModBase;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.WrenchChecker;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class GenericBlock<T extends GenericTileEntity, C extends Container> extends BaseBlock
        implements ITileEntityProvider, IRedstoneConnectable {

    protected final Class<? extends T> tileEntityClass;
    private final Class<? extends C> containerClass;

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
        super(mod, material, name, itemBlockClass);
        this.hasTileEntity = isContainer;
        this.tileEntityClass = tileEntityClass;
        this.containerClass = containerClass;
        McJtyRegister.registerLater(this, tileEntityClass);
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
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
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
        currenttip = super.getWailaBody(itemStack, currenttip, accessor, config);
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
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        intAddInformation(stack, tooltip);
    }

    private void intAddInformation(ItemStack itemStack, List<String> list) {
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
        return null;
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
    public abstract int getGuiID();

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
            container = constructor.newInstance(entityPlayer, inventory instanceof IInventory ? (IInventory)inventory : null);
            Constructor<? extends GenericGuiContainer> guiConstructor = getGuiClass().getConstructor(tileEntityClass, containerClass);
            gui = guiConstructor.newInstance(inventory, container);
            return gui;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            Logging.logError("Severe exception during creation of gui!");
            throw new RuntimeException(e);
        }
    }

    public Container createServerContainer(EntityPlayer entityPlayer, TileEntity tileEntity) {
        T inventory = (T) tileEntity;
        C container;
        try {
            Constructor<? extends C> constructor = containerClass.getConstructor(EntityPlayer.class, IInventory.class);
            container = constructor.newInstance(entityPlayer, inventory instanceof IInventory ? (IInventory)inventory : null);
            return container;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
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
