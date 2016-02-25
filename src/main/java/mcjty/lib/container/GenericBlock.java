package mcjty.lib.container;

import cofh.api.item.IToolHammer;
import mcjty.lib.api.Infusable;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.base.ModBase;
import mcjty.lib.compat.waila.WailaInfoProvider;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.varia.BlockTools;
import mcjty.lib.varia.WrenchChecker;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public abstract class GenericBlock extends Block implements ITileEntityProvider, WailaInfoProvider {

    public static final PropertyDirection FACING_HORIZ = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyDirection FACING = PropertyDirection.create("facing");

    protected ModBase modBase;
    protected final Class<? extends TileEntity> tileEntityClass;

    private boolean creative;

    public GenericBlock(ModBase mod, Material material, Class<? extends TileEntity> tileEntityClass, boolean isContainer) {
        super(material);
        this.modBase = mod;
        this.isBlockContainer = isContainer;
        this.creative = false;
        this.tileEntityClass = tileEntityClass;
        setHardness(2.0f);
        setStepSound(soundTypeMetal);
        setHarvestLevel("pickaxe", 0);
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

    public void setCreative(boolean creative) {
        this.creative = creative;
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
                currenttip.add(EnumChatFormatting.YELLOW + "Infused: " + pct + "%");
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                if (GeneralConfig.manageOwnership) {
                    if (genericTileEntity.getOwnerName() != null && !genericTileEntity.getOwnerName().isEmpty()) {
                        int securityChannel = genericTileEntity.getSecurityChannel();
                        if (securityChannel == -1) {
                            currenttip.add(EnumChatFormatting.YELLOW + "Owned by: " + genericTileEntity.getOwnerName());
                        } else {
                            currenttip.add(EnumChatFormatting.YELLOW + "Owned by: " + genericTileEntity.getOwnerName() + " (channel " + securityChannel + ")");
                        }
                        if (genericTileEntity.getOwnerUUID() == null) {
                            currenttip.add(EnumChatFormatting.RED + "Warning! Ownership not correctly set! Please place block again!");
                        }
                    }
                }
            }
        }
        return currenttip;
    }


    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean advancedToolTip) {
        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {
            if (tagCompound.hasKey("Energy")) {
                int energy = tagCompound.getInteger("Energy");
                list.add(EnumChatFormatting.GREEN + "Energy: " + energy + " rf");
            }
            if (this instanceof Infusable) {
                int infused = tagCompound.getInteger("infused");
                int pct = infused * 100 / GeneralConfig.maxInfuse;
                list.add(EnumChatFormatting.YELLOW + "Infused: " + pct + "%");
            }

            if (GeneralConfig.manageOwnership && tagCompound.hasKey("owner")) {
                String owner = tagCompound.getString("owner");
                int securityChannel = -1;
                if (tagCompound.hasKey("secChannel")) {
                    securityChannel = tagCompound.getInteger("secChannel");
                }

                if (securityChannel == -1) {
                    list.add(EnumChatFormatting.YELLOW + "Owned by: " + owner);
                } else {
                    list.add(EnumChatFormatting.YELLOW + "Owned by: " + owner + " (channel " + securityChannel + ")");
                }

                if (!tagCompound.hasKey("idM")) {
                    list.add(EnumChatFormatting.RED + "Warning! Ownership not correctly set! Please place block again!");
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
            ArrayList<ItemStack> result = new ArrayList<ItemStack>();
            result.add(stack);
            return result;
        } else {
            return super.getDrops(world, pos, metadata, fortune);
        }
    }

    @Override
    public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (willHarvest) return true; // If it will harvest, delay deletion of the block until after getDrops
        return super.removedByPlayer(world, pos, player, willHarvest);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState meta, TileEntity te) {
        super.harvestBlock(world, player, pos, meta, te);
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
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // This if this block was activated with a wrench
    private WrenchUsage testWrenchUsage(BlockPos pos, EntityPlayer player) {
        ItemStack itemStack = player.getHeldItem();
        WrenchUsage wrenchUsed = WrenchUsage.NOT;
        if (itemStack != null) {
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
        if (item instanceof IToolHammer) {
            IToolHammer hammer = (IToolHammer) item;
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            if (hammer.isUsable(itemStack, player, x, y, z)) {
                hammer.toolUsed(itemStack, player, x, y, z);
                wrenchUsed = WrenchUsage.NORMAL;
            } else {
                wrenchUsed = WrenchUsage.DISABLED;
            }
        } else if (WrenchChecker.isAWrench(item)) {
            wrenchUsed = WrenchUsage.NORMAL;
        }
        return wrenchUsed;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float sidex, float sidey, float sidez) {
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

    protected boolean checkAccess(World world, EntityPlayer player, TileEntity te) {
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
    public boolean shouldCheckWeakPower(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    /*
     * Check the redstone level reaching this block. Correctly checks for horizRotation mode.
     * @param world
     * @param x
     * @param y
     * @param z
     */
    protected void checkRedstone(World world, BlockPos pos) {
//        IBlockState state = world.getBlockState(pos);
//        int powered = world.isBlockIndirectlyGettingPowered(pos);
//        if (horizRotation) {
//            meta = BlockTools.setRedstoneSignalIn(meta, powered > 0);
//        } else {
//            meta = BlockTools.setRedstoneSignal(meta, powered > 0);
//        }
//        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    /**
     * Check the redstone level reaching this block. This version sends the
     * signal directly to the TE.
     * @param world
     * @param pos
     */
    protected void checkRedstoneWithTE(World world, BlockPos pos) {
//        int powered = world.getStrongestIndirectPower(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof GenericTileEntity) {
            int powered = world.isBlockIndirectlyGettingPowered(pos); //TODO: check
            GenericTileEntity genericTileEntity = (GenericTileEntity) te;
            genericTileEntity.setPowered(powered);
        }
    }

    /**
     * Break a block in the world, convert it to an entity and remember all the settings
     * for this block in the itemstack.
     */
    protected void breakAndRemember(World world, EntityPlayer player, BlockPos pos) {
        if (!world.isRemote) {
            harvestBlock(world, player, pos, world.getBlockState(pos), world.getTileEntity(pos));
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
     * Return the name of the icon to be used for the front side of the machine.
     */
    public String getIdentifyingIconName() {
        return null;
    }

    /**
     * Return the id of the gui to use for this block.
     */
    public abstract int getGuiID();

    /**
     * Return a server side container for opening the GUI.
     */
    public Container createServerContainer(EntityPlayer entityPlayer, TileEntity tileEntity) {
        return new EmptyContainer(entityPlayer);
    }

    /**
     * Return a client side gui for this block.
     */
    @SideOnly(Side.CLIENT)
    public GuiContainer createClientGui(EntityPlayer entityPlayer, TileEntity tileEntity) {
        return null;
    }
/*
    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        int meta = blockAccess.getBlockMetadata(x, y, z);
        EnumFacing k = getOrientation(meta);
        if (iconInd != null && side == k.ordinal()) {
            return getIconInd(blockAccess, x, y, z, meta);
        } else if (iconTop != null && side == BlockTools.getTopDirection(k).ordinal()) {
            return iconTop;
        } else if (iconBottom != null && side ==  BlockTools.getBottomDirection(k).ordinal()) {
            return iconBottom;
        } else {
            return iconSide;
        }
    }



    @Override
    public IIcon getIcon(int side, int meta) {
        if (iconInd != null && side == EnumFacing.SOUTH.ordinal()) {
            return iconInd;
        } else if (iconTop != null && side == EnumFacing.UP.ordinal()) {
            return iconTop;
        } else if (iconBottom != null && side == EnumFacing.DOWN.ordinal()) {
            return iconBottom;
        } else {
            return iconSide;
        }
    }

    public IIcon getIconInd(IBlockAccess blockAccess, int x, int y, int z, int meta) {
        return iconInd;
    }
*/

    @Override
    public boolean onBlockEventReceived(World world, BlockPos pos, IBlockState state, int eventId, int eventData) {
        if (isBlockContainer) {
            super.onBlockEventReceived(world, pos, state, eventId, eventData);
            TileEntity tileentity = world.getTileEntity(pos);
            return tileentity != null && tileentity.receiveClientEvent(eventId, eventData);
        } else {
            return super.onBlockEventReceived(world, pos, state, eventId, eventData);
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
    protected BlockState createBlockState() {
        if (hasNoRotation()) {
            return new BlockState(this);
        } else if (isHorizRotation()) {
            return new BlockState(this, FACING_HORIZ);
        } else {
            return new BlockState(this, FACING);
        }
    }
}
