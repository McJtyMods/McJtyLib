package mcjty.lib.tileentity;

import mcjty.lib.api.infusable.CapabilityInfusable;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.bindings.IAction;
import mcjty.lib.bindings.IValue;
import mcjty.lib.gui.widgets.ImageChoiceLabel;
import mcjty.lib.multipart.PartSlot;
import mcjty.lib.network.IClientCommandHandler;
import mcjty.lib.network.ICommandHandler;
import mcjty.lib.network.PacketRequestDataFromServer;
import mcjty.lib.network.PacketServerCommandTyped;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.DimensionId;
import mcjty.lib.varia.RedstoneMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class GenericTileEntity extends BlockEntity implements ICommandHandler, IClientCommandHandler {

    public static final IValue<?>[] EMPTY_VALUES = new IValue[0];
    public static final IAction[] EMPTY_ACTIONS = new IAction[0];

    public static final String CMD_RSMODE = "mcjtylib.setRsMode";

    public static final String COMMAND_SYNC_BINDING = "generic.syncBinding";
    public static final String COMMAND_SYNC_ACTION = "generic.syncAction";
    public static final Key<String> PARAM_KEY = new Key<>("key", Type.STRING);

    public static final Key<Integer> VALUE_RSMODE = new Key<>("rsmode", Type.INTEGER);

    private String ownerName = "";
    private UUID ownerUUID = null;
    private int securityChannel = -1;

    protected RedstoneMode rsMode = RedstoneMode.REDSTONE_IGNORED;
    protected int powerLevel = 0;

    public GenericTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void markDirtyClient() {
        setChanged();
        if (getLevel() != null) {
            BlockState state = getLevel().getBlockState(getBlockPos());
            getLevel().sendBlockUpdated(getBlockPos(), state, state, Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }

    public void markDirtyQuick() {
        if (getLevel() != null) {
            getLevel().blockEntityChanged(this.worldPosition);
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

    public void onBlockPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
    }

    public void onReplaced(Level world, BlockPos pos, BlockState state, BlockState newstate) {
    }

    public void onPartAdded(PartSlot slot, BlockState state, BlockEntity multipartTile) {
    }

    public InteractionResult onBlockActivated(BlockState state, Player player, InteractionHand hand, BlockHitResult result) {
        return InteractionResult.PASS;
    }

    // ------------------------------------------------------
    // Redstone

    protected boolean needsRedstoneMode() {
        return false;
    }

    public void checkRedstone(Level world, BlockPos pos) {
        int powered = world.getBestNeighborSignal(pos); // @todo check
        setPowerInput(powered);
    }


    public void setPowerInput(int powered) {
        if (powerLevel != powered) {
            powerLevel = powered;
            setChanged();
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
        setRSMode(RedstoneMode.values()[i]);
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
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        readClientDataFromNBT(tag);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag nbtTag = new CompoundTag();
        this.writeClientDataToNBT(nbtTag);
        return new ClientboundBlockEntityDataPacket(worldPosition, 1, nbtTag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        readClientDataFromNBT(packet.getTag());
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

    public boolean canPlayerAccess(Player player) {
        return !isRemoved() && player.distanceToSqr(new Vec3(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()).add(0.5D, 0.5D, 0.5D)) <= 64D;
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag updateTag = super.getUpdateTag();
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
    public void writeClientDataToNBT(CompoundTag tagCompound) {
        save(tagCompound); // @todo TEST IF THIS IS REALLY NEEDED
    }

    /**
     * For compatibility reasons this calls read() but for
     * efficiency reasons you should override this in your tile
     * entity to only read what you need on the client.
     *
     * @param tagCompound
     */
    public void readClientDataFromNBT(CompoundTag tagCompound) {
        read(tagCompound);  // @TEST IF THIS IS REALLY NEEDED
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
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);
        read(tagCompound);
    }

    public void read(CompoundTag tagCompound) {
        readCaps(tagCompound);
        readInfo(tagCompound);
    }

    protected void readCaps(CompoundTag tagCompound) {
        if (tagCompound.contains("Info")) {
            CompoundTag infoTag = tagCompound.getCompound("Info");
            getCapability(CapabilityInfusable.INFUSABLE_CAPABILITY).ifPresent(h -> h.setInfused(infoTag.getInt("infused")));
        }
        readItemHandlerCap(tagCompound);
        readEnergyCap(tagCompound);
    }

    protected void readItemHandlerCap(CompoundTag tagCompound) {
        getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .filter(h -> h instanceof INBTSerializable)
                .map(h -> (INBTSerializable) h)
                .ifPresent(h -> h.deserializeNBT(tagCompound.getList("Items", Constants.NBT.TAG_COMPOUND)));
    }

    protected void readEnergyCap(CompoundTag tagCompound) {
        getCapability(CapabilityEnergy.ENERGY)
                .filter(h -> h instanceof INBTSerializable)
                .map(h -> (INBTSerializable) h)
                .ifPresent(h -> {
                    if (tagCompound.contains("Energy")) {
                        h.deserializeNBT(tagCompound.get("Energy"));
                    }
                });
    }

    protected void readInfo(CompoundTag tagCompound) {
        if (tagCompound.contains("Info")) {
            CompoundTag infoTag = tagCompound.getCompound("Info");
            powerLevel = infoTag.getByte("powered");
            if (needsRedstoneMode()) {
                int m = infoTag.getByte("rsMode");
                rsMode = RedstoneMode.values()[m];
            }

            ownerName = infoTag.getString("owner");
            if (infoTag.hasUUID("ownerId")) {
                ownerUUID = infoTag.getUUID("ownerId");
            } else {
                ownerUUID = null;
            }
            if (infoTag.contains("secChannel")) {
                securityChannel = infoTag.getInt("secChannel");
            } else {
                securityChannel = -1;
            }
        } else {
            securityChannel = -1;
        }
    }

    protected CompoundTag getOrCreateInfo(CompoundTag tagCompound) {
        if (tagCompound.contains("Info")) {
            return tagCompound.getCompound("Info");
        }
        CompoundTag data = new CompoundTag();
        tagCompound.put("Info", data);
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tagCompound) {
        super.save(tagCompound);
        writeCaps(tagCompound);
        writeInfo(tagCompound);
        return tagCompound;
    }

    protected void writeCaps(CompoundTag tagCompound) {
        CompoundTag infoTag = getOrCreateInfo(tagCompound);
        getCapability(CapabilityInfusable.INFUSABLE_CAPABILITY).ifPresent(h -> infoTag.putInt("infused", h.getInfused()));
        writeItemHandlerCap(tagCompound);
        writeEnergyCap(tagCompound);
    }

    protected void writeItemHandlerCap(CompoundTag tagCompound) {
        getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .filter(h -> h instanceof INBTSerializable)
                .map(h -> (INBTSerializable) h)
                .ifPresent(h -> tagCompound.put("Items", h.serializeNBT()));
    }

    protected void writeEnergyCap(CompoundTag tagCompound) {
        getCapability(CapabilityEnergy.ENERGY)
                .filter(h -> h instanceof INBTSerializable)
                .map(h -> (INBTSerializable) h)
                .ifPresent(h -> tagCompound.put("Energy", h.serializeNBT()));
    }

    protected void writeInfo(CompoundTag tagCompound) {
        CompoundTag infoTag = getOrCreateInfo(tagCompound);
        if (powerLevel > 0) {
            infoTag.putByte("powered", (byte) powerLevel);
        }
        if (needsRedstoneMode()) {
            infoTag.putByte("rsMode", (byte) rsMode.ordinal());
        }
        infoTag.putString("owner", ownerName);
        if (ownerUUID != null) {
            infoTag.putUUID("ownerId", ownerUUID);
        }
        if (securityChannel != -1) {
            infoTag.putInt("secChannel", securityChannel);
        }
    }

    public boolean setOwner(Player player) {
        if (!GeneralConfig.manageOwnership.get()) {
            return false;
        }

        if (ownerUUID != null) {
            // Already has an owner.
            return false;
        }
        ownerUUID = player.getGameProfile().getId();
        ownerName = player.getName().getString() /* was getFormattedText() */;
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

    public boolean checkAccess(Player player) {
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
//        if (McJtyLib.proxy.isSneaking()) {
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

    public int getRedstoneOutput(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return -1;
    }

    public void getDrops(NonNullList<ItemStack> drops, BlockGetter world, BlockPos pos, BlockState metadata, int fortune) {
    }

    public void rotateBlock(Rotation axis) {

    }

    /**
     * Return false if this was not handled here. In that case the default rotateBlock() will be done
     */
    public boolean wrenchUse(Level world, BlockPos pos, Direction side, Player player) {
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
    public DimensionId getDimension() {
        return DimensionId.fromWorld(level);
    }

        // Client side function to send a value to the server
    public <T> void valueToServer(SimpleChannel network, Key<T> valueKey, T value) {

        network.sendToServer(new PacketServerCommandTyped(getBlockPos(),
                getDimension(),
                COMMAND_SYNC_BINDING,
                TypedMap.builder()
                        .put(valueKey, value)
                        .build()));
    }

    /**
     * Call this client-side to this TE to request data from the server
     */
    public void requestDataFromServer(SimpleChannel channel, String command, @Nonnull TypedMap params) {
        channel.sendToServer(new PacketRequestDataFromServer(getDimension(), worldPosition, command, params, false));
    }


    @Override
    public boolean execute(Player playerMP, String command, TypedMap params) {
        if (COMMAND_SYNC_BINDING.equals(command)) {
            syncBinding(params);
            return true;
        } else if (COMMAND_SYNC_ACTION.equals(command)) {
            String key = params.get(PARAM_KEY);
            findConsumer(key).run();
            return true;
        } else if (CMD_RSMODE.equals(command)) {
            setRSMode(RedstoneMode.values()[params.get(ImageChoiceLabel.PARAM_CHOICE_IDX)]);
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
