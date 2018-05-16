package mcjty.lib.tileentity;

import mcjty.lib.api.Infusable;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.bindings.IAction;
import mcjty.lib.bindings.IValue;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.network.*;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.NullSidedInvWrapper;
import mcjty.lib.varia.RedstoneMode;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GenericTileEntity extends TileEntity implements ICommandHandler, IClientCommandHandler {

    public static final IValue<?, ?>[] EMPTY_VALUES = new IValue[0];
    public static final IAction<?>[] EMPTY_ACTIONS = new IAction[0];

    public static final String COMMAND_SYNC_BINDING = "generic.syncBinding";
    public static final String COMMAND_SYNC_ACTION = "generic.syncAction";
    public static final Key<String> PARAM_KEY = new Key<>("key", Type.STRING);

    public static final Key<Integer> VALUE_RSMODE = new Key<>("rsmode", Type.INTEGER);

    private int infused = 0;

    private String ownerName = "";
    private UUID ownerUUID = null;
    private int securityChannel = -1;

    protected RedstoneMode rsMode = RedstoneMode.REDSTONE_IGNORED;
    protected int powerLevel = 0;

    public void markDirtyClient() {
        markDirty();
        if (getWorld() != null) {
            IBlockState state = getWorld().getBlockState(getPos());
            getWorld().notifyBlockUpdate(getPos(), state, state, 3);
        }
    }

    public void markDirtyQuick() {
        if (getWorld() != null) {
            getWorld().markChunkDirty(this.pos, this);
        }
    }

    public IValue<?, ?>[] getValues() {
        return EMPTY_VALUES;
    }

    public IAction<?>[] getActions() {
        return EMPTY_ACTIONS;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    }

    public void onBlockBreak(World workd, BlockPos pos, IBlockState state) {
    }

    // ------------------------------------------------------
    // Redstone

    protected boolean needsRedstoneMode() {
        return false;
    }

    public void checkRedstone(World world, BlockPos pos) {
        int powered = world.isBlockIndirectlyGettingPowered(pos); //TODO: check
        setPowerInput(powered);
    }


    public void setPowerInput(int powered) {
        if (powerLevel != powered) {
            powerLevel = powered;
            markDirty();
        }
    }

    public RedstoneMode getRSMode() {
        return rsMode;
    }

    public void setRSMode(RedstoneMode redstoneMode) {
        this.rsMode = redstoneMode;
        markDirtyClient();
    }

    public void setRSModeInt(int i) {
        this.rsMode = RedstoneMode.values()[i];
        markDirtyClient();
    }

    public int getRSModeInt() {
        return rsMode.ordinal();
    }

    // Use redstone mode and input power to decide if this is enabled or not
    public boolean isMachineEnabled() {
        if (rsMode != RedstoneMode.REDSTONE_IGNORED) {
            boolean rs = powerLevel > 0;
            if (rsMode == RedstoneMode.REDSTONE_OFFREQUIRED && rs) {
                return false;
            } else if (rsMode == RedstoneMode.REDSTONE_ONREQUIRED && !rs) {
                return false;
            }
        }
        return true;
    }

    // ------------------------------------------------------

    // Called when a slot is changed.
    public void onSlotChanged(int index, ItemStack stack) {
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeClientDataToNBT(nbtTag);
        return new SPacketUpdateTileEntity(pos, 1, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readClientDataFromNBT(packet.getNbtCompound());
    }

    public void setInfused(int infused) {
        this.infused = infused;
        markDirtyClient();
    }

    public int getInfused() {
        return infused;
    }

    public float getInfusedFactor() {
        return ((float) infused) / GeneralConfig.maxInfuse;
    }

    public boolean canPlayerAccess(EntityPlayer player) {
        return !isInvalid() && player.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound updateTag = super.getUpdateTag();
        writeClientDataToNBT(updateTag);
        return updateTag;
    }

    /**
     * For compatibility reasons this calls writeToNBT() but for
     * efficiency reasons you should override this in your tile
     * entity to only write what you need on the client.
     *
     * @param tagCompound
     */
    public void writeClientDataToNBT(NBTTagCompound tagCompound) {
        writeToNBT(tagCompound);
    }

    /**
     * For compatibility reasons this calls readFromNBT() but for
     * efficiency reasons you should override this in your tile
     * entity to only read what you need on the client.
     *
     * @param tagCompound
     */
    public void readClientDataFromNBT(NBTTagCompound tagCompound) {
        readFromNBT(tagCompound);
    }

    /**
     * GUI Data sync
     */
    public void syncDataForGUI(Object[] data) {
    }

    /**
     * GUI Data sync. Supported types: Integer, Long, Float, String, Byte, Boolean
     */
    public Object[] getDataForGUI() {
        return new Object[0];
    }

    protected void readBufferFromNBT(NBTTagCompound tagCompound, InventoryHelper inventoryHelper) {
        NBTTagList bufferTagList = tagCompound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < bufferTagList.tagCount(); i++) {
            NBTTagCompound nbtTagCompound = bufferTagList.getCompoundTagAt(i);
            inventoryHelper.setStackInSlot(i, new ItemStack(nbtTagCompound));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        powerLevel = tagCompound.getByte("powered");
        readRestorableFromNBT(tagCompound);
    }

    /**
     * Override this method to recover all information that you want
     * to recover from an ItemBlock in the player's inventory.
     *
     * @param tagCompound
     */
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        if (needsRedstoneMode()) {
            int m = tagCompound.getByte("rsMode");
            rsMode = RedstoneMode.values()[m];
        }

        infused = tagCompound.getInteger("infused");
        ownerName = tagCompound.getString("owner");
        if (tagCompound.hasKey("idM")) {
            ownerUUID = new UUID(tagCompound.getLong("idM"), tagCompound.getLong("idL"));
        } else {
            ownerUUID = null;
        }
        if (tagCompound.hasKey("secChannel")) {
            securityChannel = tagCompound.getInteger("secChannel");
        } else {
            securityChannel = -1;
        }
    }

    protected void writeBufferToNBT(NBTTagCompound tagCompound, InventoryHelper inventoryHelper) {
        NBTTagList bufferTagList = new NBTTagList();
        for (int i = 0; i < inventoryHelper.getCount(); i++) {
            ItemStack stack = inventoryHelper.getStackInSlot(i);
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            if (!stack.isEmpty()) {
                stack.writeToNBT(nbtTagCompound);
            }
            bufferTagList.appendTag(nbtTagCompound);
        }
        tagCompound.setTag("Items", bufferTagList);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        if (powerLevel > 0) {
            tagCompound.setByte("powered", (byte) powerLevel);
        }
        writeRestorableToNBT(tagCompound);
        return tagCompound;
    }

    /**
     * Override this method to store all information that you want
     * to store in an ItemBlock in the player's inventory (when the block
     * is picked up with a wrench).
     *
     * @param tagCompound
     */
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        if (needsRedstoneMode()) {
            tagCompound.setByte("rsMode", (byte) rsMode.ordinal());
        }
        tagCompound.setInteger("infused", infused);
        tagCompound.setString("owner", ownerName);
        if (ownerUUID != null) {
            tagCompound.setLong("idM", ownerUUID.getMostSignificantBits());
            tagCompound.setLong("idL", ownerUUID.getLeastSignificantBits());
        }
        if (securityChannel != -1) {
            tagCompound.setInteger("secChannel", securityChannel);
        }
    }

    public boolean setOwner(EntityPlayer player) {
        if (!GeneralConfig.manageOwnership) {
            return false;
        }

        if (ownerUUID != null) {
            // Already has an owner.
            return false;
        }
        ownerUUID = player.getGameProfile().getId();
        ownerName = player.getName();
        markDirtyClient();

        return true;
    }

    public void clearOwner() {
        if (!GeneralConfig.manageOwnership) {
            return;
        }
        ownerUUID = null;
        ownerName = "";
        securityChannel = -1;
        markDirtyClient();
    }

    public void setSecurityChannel(int id) {
        securityChannel = id;
        markDirtyClient();
    }

    public int getSecurityChannel() {
        return securityChannel;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Nonnull
    @Override
    public <T> List<T> executeWithResultList(String command, TypedMap args, Type<T> type) {
        return Collections.emptyList();
    }

    @Override
    public TypedMap executeWithResult(String command, TypedMap args) {
        return null;
    }

    @Override
    public <T> boolean receiveListFromServer(String command, List<T> list, Type<T> type) {
        return false;
    }

    @Override
    public boolean receiveDataFromServer(String command, @Nonnull TypedMap result) {
        return false;
    }


    protected boolean needsCustomInvWrapper() {
        return false;
    }

    protected IItemHandler invHandlerNull;
    protected IItemHandler invHandlerSided;

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (needsCustomInvWrapper()) {
            if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                return true;
            }
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (needsCustomInvWrapper()) {
                if (facing == null) {
                    if (invHandlerNull == null) {
                        invHandlerNull = new InvWrapper((IInventory) this);
                    }
                    return (T) invHandlerNull;
                } else {
                    if (invHandlerSided == null) {
                        invHandlerSided = new NullSidedInvWrapper((ISidedInventory) this);
                    }
                    return (T) invHandlerSided;
                }
            }
        }
        return super.getCapability(capability, facing);
    }

    public boolean checkAccess(EntityPlayer player) {
        return false;
    }

    @Optional.Method(modid = "theoneprobe")
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        if (blockState.getBlock() instanceof Infusable) {
            int infused = getInfused();
            int pct = infused * 100 / GeneralConfig.maxInfuse;
            probeInfo.text(TextFormatting.YELLOW + "Infused: " + pct + "%");
        }
        if (mode == ProbeMode.EXTENDED) {
            if (GeneralConfig.manageOwnership) {
                if (getOwnerName() != null && !getOwnerName().isEmpty()) {
                    int securityChannel = getSecurityChannel();
                    if (securityChannel == -1) {
                        probeInfo.text(TextFormatting.YELLOW + "Owned by: " + getOwnerName());
                    } else {
                        probeInfo.text(TextFormatting.YELLOW + "Owned by: " + getOwnerName() + " (channel " + securityChannel + ")");
                    }
                    if (getOwnerUUID() == null) {
                        probeInfo.text(TextFormatting.RED + "Warning! Ownership not correctly set! Please place block again!");
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Optional.Method(modid = "waila")
    public void addWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Block block = accessor.getBlock();
        if (block instanceof Infusable) {
            int infused = getInfused();
            int pct = infused * 100 / GeneralConfig.maxInfuse;
            currenttip.add(TextFormatting.YELLOW + "Infused: " + pct + "%");
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            if (GeneralConfig.manageOwnership) {
                if (getOwnerName() != null && !getOwnerName().isEmpty()) {
                    int securityChannel = getSecurityChannel();
                    if (securityChannel == -1) {
                        currenttip.add(TextFormatting.YELLOW + "Owned by: " + getOwnerName());
                    } else {
                        currenttip.add(TextFormatting.YELLOW + "Owned by: " + getOwnerName() + " (channel " + securityChannel + ")");
                    }
                    if (getOwnerUUID() == null) {
                        currenttip.add(TextFormatting.RED + "Warning! Ownership not correctly set! Please place block again!");
                    }
                }
            }
        }
    }

    public IBlockState getActualState(IBlockState state) {
        return state;
    }

    public int getRedstoneOutput(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return -1;
    }

    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState metadata, int fortune) {
    }

    public void rotateBlock(EnumFacing axis) {

    }

    /**
     * Return false if this was not handled here. In that case the default rotateBlock() will be done
     */
    public boolean wrenchUse(World world, BlockPos pos, EnumFacing side, EntityPlayer player) {
        return false;
    }

    private <T extends GenericTileEntity, V> BiConsumer<T, V> findSetter(Key<V> key) {
        // Cache or use Map?
        for (IValue value : getValues()) {
            if (key.getName().equals(value.getKey().getName())) {
                return value.setter();
            }
        }
        return null;
    }

    private <T extends GenericTileEntity> Consumer<T> findConsumer(String key) {
        for (IAction action : getActions()) {
            if (key.equals(action.getKey())) {
                return action.consumer();
            }
        }
        return null;
    }

    // Client side function to send a value to the server
    public <T> void valueToServer(SimpleNetworkWrapper network, Key<T> valueKey, T value) {
        network.sendToServer(new PacketServerCommandTyped(getPos(),
                COMMAND_SYNC_BINDING,
                TypedMap.builder()
                        .put(valueKey, value)
                        .build()));
    }

    /**
     * Call this client-side to this TE to request data from the server
     */
    public void requestDataFromServer(String modid, String command, @Nonnull TypedMap params) {
        PacketHandler.modNetworking.get(modid).sendToServer(new PacketRequestDataFromServer(modid, pos, command, params));
    }


    @Override
    public boolean execute(EntityPlayerMP playerMP, String command, TypedMap params) {
        if (COMMAND_SYNC_BINDING.equals(command)) {
            syncBinding(params);
            return true;
        } else if (COMMAND_SYNC_ACTION.equals(command)) {
            String key = params.get(PARAM_KEY);
            findConsumer(key).accept(this);
            return true;
        }
        return false;
    }

    private void syncBinding(TypedMap params) {
        for (Key<?> key : params.getKeys()) {
            // @todo can be improved?
            if (Type.BOOLEAN.equals(key.getType())) {
                Key<Boolean> bkey = (Key<Boolean>) key;
                Boolean o = params.get(bkey);
                findSetter(bkey).accept(this, o);
            } else if (Type.INTEGER.equals(key.getType())) {
                Key<Integer> bkey = (Key<Integer>) key;
                Integer o = params.get(bkey);
                findSetter(bkey).accept(this, o);
            } else if (Type.STRING.equals(key.getType())) {
                Key<String> bkey = (Key<String>) key;
                String o = params.get(bkey);
                findSetter(bkey).accept(this, o);
            } else if (Type.DOUBLE.equals(key.getType())) {
                Key<Double> bkey = (Key<Double>) key;
                Double o = params.get(bkey);
                findSetter(bkey).accept(this, o);
            }
        }
        markDirtyClient();
    }
}
