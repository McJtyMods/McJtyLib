package mcjty.lib.tileentity;

import mcjty.lib.api.infusable.CapabilityInfusable;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.bindings.IAction;
import mcjty.lib.bindings.IValue;
import mcjty.lib.multipart.PartSlot;
import mcjty.lib.network.IClientCommandHandler;
import mcjty.lib.network.ICommandHandler;
import mcjty.lib.network.PacketRequestDataFromServer;
import mcjty.lib.network.PacketServerCommandTyped;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.RedstoneMode;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class GenericTileEntity extends TileEntity implements ICommandHandler, IClientCommandHandler {

    public static final IValue<?>[] EMPTY_VALUES = new IValue[0];
    public static final IAction[] EMPTY_ACTIONS = new IAction[0];

    public static final String COMMAND_SYNC_BINDING = "generic.syncBinding";
    public static final String COMMAND_SYNC_ACTION = "generic.syncAction";
    public static final Key<String> PARAM_KEY = new Key<>("key", Type.STRING);

    public static final Key<Integer> VALUE_RSMODE = new Key<>("rsmode", Type.INTEGER);

    private String ownerName = "";
    private UUID ownerUUID = null;
    private int securityChannel = -1;

    protected RedstoneMode rsMode = RedstoneMode.REDSTONE_IGNORED;
    protected int powerLevel = 0;

    public GenericTileEntity(TileEntityType<?> type) {
        super(type);
    }

    public void markDirtyClient() {
        markDirty();
        if (getWorld() != null) {
            BlockState state = getWorld().getBlockState(getPos());
            getWorld().notifyBlockUpdate(getPos(), state, state, Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }

    public void markDirtyQuick() {
        if (getWorld() != null) {
            getWorld().markChunkDirty(this.pos, this);
        }
    }

    public IValue<?>[] getValues() {
        return EMPTY_VALUES;
    }

    public IAction[] getActions() {
        return EMPTY_ACTIONS;
    }

    // @todo 1.14
//    @Override
//    public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newSate) {
//        return oldState.getBlock() != newSate.getBlock();
//    }

    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
    }

    public void onReplaced(World world, BlockPos pos, BlockState state, BlockState newstate) {
    }

    public void onPartAdded(PartSlot slot, BlockState state, TileEntity multipartTile) {
    }

    public ActionResultType onBlockActivated(BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        return ActionResultType.PASS;
    }

    // ------------------------------------------------------
    // Redstone

    protected boolean needsRedstoneMode() {
        return false;
    }

    public void checkRedstone(World world, BlockPos pos) {
        int powered = world.getRedstonePowerFromNeighbors(pos); // @todo check
        setPowerInput(powered);
    }


    public void setPowerInput(int powered) {
        if (powerLevel != powered) {
            powerLevel = powered;
            markDirty();
        }
    }

    public int getPowerLevel() {
        return powerLevel;
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

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        super.handleUpdateTag(tag);
        readClientDataFromNBT(tag);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbtTag = new CompoundNBT();
        this.writeClientDataToNBT(nbtTag);
        return new SUpdateTileEntityPacket(pos, 1, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        readClientDataFromNBT(packet.getNbtCompound());
    }

    //    public void setInfused(int infused) {
//        this.infused = infused;
//        markDirtyClient();
//    }
//
//    public int getInfused() {
//        return infused;
//    }
//
//    public float getInfusedFactor() {
//        return ((float) infused) / GeneralConfig.maxInfuse.get();
//    }

    public boolean canPlayerAccess(PlayerEntity player) {
        return !isRemoved() && player.getDistanceSq(new Vec3d(pos).add(0.5D, 0.5D, 0.5D)) <= 64D;
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT updateTag = super.getUpdateTag();
        writeClientDataToNBT(updateTag);
        return updateTag;
    }

    /**
     * For compatibility reasons this calls write() but for
     * efficiency reasons you should override this in your tile
     * entity to only write what you need on the client.
     *
     * @param tagCompound
     */
    public void writeClientDataToNBT(CompoundNBT tagCompound) {
        write(tagCompound);
    }

    /**
     * For compatibility reasons this calls read() but for
     * efficiency reasons you should override this in your tile
     * entity to only read what you need on the client.
     *
     * @param tagCompound
     */
    public void readClientDataFromNBT(CompoundNBT tagCompound) {
        read(tagCompound);
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

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        readCaps(tagCompound);
        readInfo(tagCompound);
    }

    protected void readCaps(CompoundNBT tagCompound) {
        if (tagCompound.contains("Info")) {
            CompoundNBT infoTag = tagCompound.getCompound("Info");
            getCapability(CapabilityInfusable.INFUSABLE_CAPABILITY).ifPresent(h -> h.setInfused(infoTag.getInt("infused")));
        }
        readItemHandlerCap(tagCompound);
        readEnergyCap(tagCompound);
    }

    protected void readItemHandlerCap(CompoundNBT tagCompound) {
        getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .filter(h -> h instanceof INBTSerializable)
                .map(h -> (INBTSerializable) h)
                .ifPresent(h -> h.deserializeNBT(tagCompound.getList("Items", Constants.NBT.TAG_COMPOUND)));
    }

    protected void readEnergyCap(CompoundNBT tagCompound) {
        getCapability(CapabilityEnergy.ENERGY)
                .filter(h -> h instanceof INBTSerializable)
                .map(h -> (INBTSerializable) h)
                .ifPresent(h -> {
                    if (tagCompound.contains("Energy")) {
                        h.deserializeNBT(tagCompound.get("Energy"));
                    }
                });
    }

    protected void readInfo(CompoundNBT tagCompound) {
        if (tagCompound.contains("Info")) {
            CompoundNBT infoTag = tagCompound.getCompound("Info");
            powerLevel = infoTag.getByte("powered");
            if (needsRedstoneMode()) {
                int m = infoTag.getByte("rsMode");
                rsMode = RedstoneMode.values()[m];
            }

            ownerName = infoTag.getString("owner");
            ownerUUID = infoTag.getUniqueId("ownerId");
            if (infoTag.contains("secChannel")) {
                securityChannel = infoTag.getInt("secChannel");
            } else {
                securityChannel = -1;
            }
        } else {
            securityChannel = -1;
        }
    }

    protected CompoundNBT getOrCreateInfo(CompoundNBT tagCompound) {
        if (tagCompound.contains("Info")) {
            return tagCompound.getCompound("Info");
        }
        CompoundNBT data = new CompoundNBT();
        tagCompound.put("Info", data);
        return data;
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        super.write(tagCompound);
        writeCaps(tagCompound);
        writeInfo(tagCompound);
        return tagCompound;
    }

    protected void writeCaps(CompoundNBT tagCompound) {
        CompoundNBT infoTag = getOrCreateInfo(tagCompound);
        getCapability(CapabilityInfusable.INFUSABLE_CAPABILITY).ifPresent(h -> infoTag.putInt("infused", h.getInfused()));
        writeItemHandlerCap(tagCompound);
        writeEnergyCap(tagCompound);
    }

    protected void writeItemHandlerCap(CompoundNBT tagCompound) {
        getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .filter(h -> h instanceof INBTSerializable)
                .map(h -> (INBTSerializable) h)
                .ifPresent(h -> tagCompound.put("Items", h.serializeNBT()));
    }

    protected void writeEnergyCap(CompoundNBT tagCompound) {
        getCapability(CapabilityEnergy.ENERGY)
                .filter(h -> h instanceof INBTSerializable)
                .map(h -> (INBTSerializable) h)
                .ifPresent(h -> tagCompound.put("Energy", h.serializeNBT()));
    }

    protected void writeInfo(CompoundNBT tagCompound) {
        CompoundNBT infoTag = getOrCreateInfo(tagCompound);
        if (powerLevel > 0) {
            infoTag.putByte("powered", (byte) powerLevel);
        }
        if (needsRedstoneMode()) {
            infoTag.putByte("rsMode", (byte) rsMode.ordinal());
        }
        infoTag.putString("owner", ownerName);
        if (ownerUUID != null) {
            infoTag.putUniqueId("ownerId", ownerUUID);
        }
        if (securityChannel != -1) {
            infoTag.putInt("secChannel", securityChannel);
        }
    }

    public boolean setOwner(PlayerEntity player) {
        if (!GeneralConfig.manageOwnership.get()) {
            return false;
        }

        if (ownerUUID != null) {
            // Already has an owner.
            return false;
        }
        ownerUUID = player.getGameProfile().getId();
        ownerName = player.getName().getFormattedText();
        markDirtyClient();

        return true;
    }

    public void clearOwner() {
        if (!GeneralConfig.manageOwnership.get()) {
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

    public boolean checkAccess(PlayerEntity player) {
        return false;
    }

//    @Optional.Method(modid = "theoneprobe")
//    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
//        if (blockState.getBlock() instanceof Infusable) {
//            int infused = getInfused();
//            int pct = infused * 100 / GeneralConfig.maxInfuse;
//            probeInfo.text(TextFormatting.YELLOW + "Infused: " + pct + "%");
//        }
//        if (mode == ProbeMode.EXTENDED) {
//            if (GeneralConfig.manageOwnership) {
//                if (getOwnerName() != null && !getOwnerName().isEmpty()) {
//                    int securityChannel = getSecurityChannel();
//                    if (securityChannel == -1) {
//                        probeInfo.text(TextFormatting.YELLOW + "Owned by: " + getOwnerName());
//                    } else {
//                        probeInfo.text(TextFormatting.YELLOW + "Owned by: " + getOwnerName() + " (channel " + securityChannel + ")");
//                    }
//                    if (getOwnerUUID() == null) {
//                        probeInfo.text(TextFormatting.RED + "Warning! Ownership not correctly set! Please place block again!");
//                    }
//                }
//            }
//        }
//    }
//
//    @SideOnly(Side.CLIENT)
//    @Optional.Method(modid = "waila")
//    public void addWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
//        Block block = accessor.getBlock();
//        if (block instanceof Infusable) {
//            int infused = getInfused();
//            int pct = infused * 100 / GeneralConfig.maxInfuse;
//            currenttip.add(TextFormatting.YELLOW + "Infused: " + pct + "%");
//        }
//        if (McJtyLib.proxy.isShiftKeyDown()) {
//            if (GeneralConfig.manageOwnership) {
//                if (getOwnerName() != null && !getOwnerName().isEmpty()) {
//                    int securityChannel = getSecurityChannel();
//                    if (securityChannel == -1) {
//                        currenttip.add(TextFormatting.YELLOW + "Owned by: " + getOwnerName());
//                    } else {
//                        currenttip.add(TextFormatting.YELLOW + "Owned by: " + getOwnerName() + " (channel " + securityChannel + ")");
//                    }
//                    if (getOwnerUUID() == null) {
//                        currenttip.add(TextFormatting.RED + "Warning! Ownership not correctly set! Please place block again!");
//                    }
//                }
//            }
//        }
//    }

    public int getRedstoneOutput(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        return -1;
    }

    public void getDrops(NonNullList<ItemStack> drops, IBlockReader world, BlockPos pos, BlockState metadata, int fortune) {
    }

    public void rotateBlock(Rotation axis) {

    }

    /**
     * Return false if this was not handled here. In that case the default rotateBlock() will be done
     */
    public boolean wrenchUse(World world, BlockPos pos, Direction side, PlayerEntity player) {
        return false;
    }

    private <V> Consumer<V> findSetter(Key<V> key) {
        // Cache or use Map?
        for (IValue value : getValues()) {
            if (key.getName().equals(value.getKey().getName())) {
                return value.setter();
            }
        }
        return null;
    }

    private Runnable findConsumer(String key) {
        for (IAction action : getActions()) {
            if (key.equals(action.getKey())) {
                return action.consumer();
            }
        }
        return null;
    }

    /// Override this function if you have a tile entity that needs to be opened remotely and thus has to 'fake' the real dimension
    public DimensionType getDimensionType() {
        return world.getDimension().getType();
    }

        // Client side function to send a value to the server
    public <T> void valueToServer(SimpleChannel network, Key<T> valueKey, T value) {
        network.sendToServer(new PacketServerCommandTyped(getPos(),
                getDimensionType(),
                COMMAND_SYNC_BINDING,
                TypedMap.builder()
                        .put(valueKey, value)
                        .build()));
    }

    /**
     * Call this client-side to this TE to request data from the server
     */
    public void requestDataFromServer(SimpleChannel channel, String command, @Nonnull TypedMap params) {
        channel.sendToServer(new PacketRequestDataFromServer(getDimensionType(), pos, command, params, false));
    }


    @Override
    public boolean execute(PlayerEntity playerMP, String command, TypedMap params) {
        if (COMMAND_SYNC_BINDING.equals(command)) {
            syncBinding(params);
            return true;
        } else if (COMMAND_SYNC_ACTION.equals(command)) {
            String key = params.get(PARAM_KEY);
            findConsumer(key).run();
            return true;
        }
        return false;
    }

    private <T> void syncBindingHelper(TypedMap params, Key<T> bkey) {
        T o = params.get(bkey);
        findSetter(bkey).accept(o);
    }

    private void syncBinding(TypedMap params) {
        for (Key<?> key : params.getKeys()) {
            syncBindingHelper(params, key);
        }
        markDirtyClient();
    }
}
