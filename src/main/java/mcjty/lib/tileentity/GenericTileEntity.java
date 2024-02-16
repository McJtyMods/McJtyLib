package mcjty.lib.tileentity;

import mcjty.lib.api.infusable.CapabilityInfusable;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.blockcommands.*;
import mcjty.lib.container.AutomationFilterItemHander;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.gui.widgets.ImageChoiceLabel;
import mcjty.lib.multipart.PartSlot;
import mcjty.lib.network.PacketRequestDataFromServer;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.RedstoneMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class GenericTileEntity extends BlockEntity {

    public static final Key<Integer> VALUE_RSMODE = new Key<>("rsmode", Type.INTEGER);

    private String ownerName = "";
    private UUID ownerUUID = null;
    private int securityChannel = -1;

    protected RedstoneMode rsMode = RedstoneMode.REDSTONE_IGNORED;
    protected int powerLevel = 0;

    // This is a generated function (from the annotated capabilities) that is initially (by the TE
    // constructor) set to be a function that looks for the annotations and replaces itself with
    // a function that does the actual testing
    private BiFunction<Capability, Direction, LazyOptional> capSetup;
    private final List<LazyOptional> lazyOptsToClean = new ArrayList<>();

    public GenericTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        // Setup code to find the capability annotations and in that code we
        // replace 'capSetup' with the actual code to execute the annotations
        capSetup = (cap,dir) -> {
            List<Pair<Field, Cap>> list = getAnnotationHolder().capabilityList;
            capSetup = generateCapTests(list, 0);
            return capSetup.apply(cap, dir);
        };
        // Make sure the annotation holder exists
        getAnnotationHolder();
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
//        lazyOptsToClean.forEach(LazyOptional::invalidate);
//        lazyOptsToClean.clear();
    }

    private BiFunction<Capability, Direction, LazyOptional> generateCapTests(List<Pair<Field, Cap>> caps, int index) {
        if (index >= caps.size()) {
            return super::getCapability;
        } else {
            try {
                Cap annotation = caps.get(index).getRight();
                Object instance = FieldUtils.readField(caps.get(index).getLeft(), this, true);
                LazyOptional lazy;
                if (instance instanceof LazyOptional) {
                    lazy = (LazyOptional) instance;
                } else if (annotation.type() == CapType.ITEMS_AUTOMATION) {
                    lazy = LazyOptional.of(() -> new AutomationFilterItemHander((GenericItemHandler) instance));
                } else {
                    lazy = LazyOptional.of(() -> instance);
                }
                lazyOptsToClean.add(lazy);
                BiFunction<Capability, Direction, LazyOptional> tail = generateCapTests(caps, index + 1);
                Capability desiredCapability = annotation.type().getCapability();
                return (cap, dir) -> {
                    if (cap == desiredCapability) {
                        return lazy;
                    } else {
                        return tail.apply(cap, dir);
                    }
                };
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return capSetup.apply(cap, side);
    }

    public void markDirtyClient() {
        setChanged();
        if (getLevel() != null) {
            BlockState state = getLevel().getBlockState(getBlockPos());
            getLevel().sendBlockUpdated(getBlockPos(), state, state, Block.UPDATE_CLIENTS + Block.UPDATE_NEIGHBORS);
        }
    }

    public void markDirtyQuick() {
        if (getLevel() != null) {
            getLevel().blockEntityChanged(this.worldPosition);
        }
    }

    public Map<String, ValueHolder<?, ?>> getValueMap() {
        AnnotationHolder holder = getAnnotationHolder();
        return holder.valueMap;
    }

    // @todo 1.14
//    @Override
//    public boolean shouldRefresh(Level world, BlockPos pos, BlockState oldState, BlockState newSate) {
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

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag nbtTag = new CompoundTag();
        this.saveClientDataToNBT(nbtTag);
        return ClientboundBlockEntityDataPacket.create(this, (BlockEntity entity) -> {return nbtTag;});
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet) {
        loadClientDataFromNBT(packet.getTag());
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
        return !isRemoved() && player.distanceToSqr(new Vec3(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()).add(new Vec3(0.5D, 0.5D, 0.5D))) <= 64D;
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag updateTag = super.getUpdateTag();
        saveClientDataToNBT(updateTag);
        return updateTag;
    }

    /**
     * Override to write only the data you need on the client
     */
    public void saveClientDataToNBT(CompoundTag tagCompound) {
    }

    /**
     * Override to read only the data you need on the client
     */
    public void loadClientDataFromNBT(CompoundTag tagCompound) {
    }

    @Override
    public void load(CompoundTag tagCompound) {
        loadCaps(tagCompound);
        loadInfo(tagCompound);
    }

    protected void loadCaps(CompoundTag tagCompound) {
        if (tagCompound.contains("Info")) {
            CompoundTag infoTag = tagCompound.getCompound("Info");
            getCapability(CapabilityInfusable.INFUSABLE_CAPABILITY).ifPresent(h -> h.setInfused(infoTag.getInt("infused")));
        }
        loadItemHandlerCap(tagCompound);
        loadEnergyCap(tagCompound);
    }

    protected void loadItemHandlerCap(CompoundTag tagCompound) {
        getCapability(ForgeCapabilities.ITEM_HANDLER)
                .filter(h -> h instanceof INBTSerializable)
                .map(h -> (INBTSerializable) h)
                .ifPresent(h -> {
                    // For compatibility with loot tables we check McItems first
                    if (tagCompound.contains("McItems")) {
                        h.deserializeNBT(tagCompound.getList("McItems", Tag.TAG_COMPOUND));
                    } else {
                        h.deserializeNBT(tagCompound.getList("Items", Tag.TAG_COMPOUND));
                    }
                });
    }

    protected void loadEnergyCap(CompoundTag tagCompound) {
        getCapability(ForgeCapabilities.ENERGY)
                .filter(h -> h instanceof INBTSerializable)
                .map(h -> (INBTSerializable) h)
                .ifPresent(h -> {
                    if (tagCompound.contains("Energy")) {
                        h.deserializeNBT(tagCompound.get("Energy"));
                    }
                });
    }

    protected void loadInfo(CompoundTag tagCompound) {
        if (tagCompound.contains("Info")) {
            CompoundTag infoTag = tagCompound.getCompound("Info");
            if (infoTag.contains("powered")) {
                powerLevel = infoTag.getByte("powered");
            }
            loadRSMode(infoTag);
            if (infoTag.contains("owner")) {
                ownerName = infoTag.getString("owner");
            }
            if (infoTag.hasUUID("ownerId")) {
                ownerUUID = infoTag.getUUID("ownerId");
            }
            if (infoTag.contains("secChannel")) {
                securityChannel = infoTag.getInt("secChannel");
            }
        }
    }

    protected void loadRSMode(CompoundTag infoTag) {
        if (needsRedstoneMode() && infoTag.contains("rsMode")) {
            int m = infoTag.getByte("rsMode");
            rsMode = RedstoneMode.values()[m];
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

    public void saveAdditional(@Nonnull CompoundTag tagCompound) {
        saveCaps(tagCompound);
        saveInfo(tagCompound);
    }

    protected void saveCaps(CompoundTag tagCompound) {
        CompoundTag infoTag = getOrCreateInfo(tagCompound);
        getCapability(CapabilityInfusable.INFUSABLE_CAPABILITY).ifPresent(h -> infoTag.putInt("infused", h.getInfused()));
        saveItemHandlerCap(tagCompound);
        saveEnergyCap(tagCompound);
    }

    protected void saveItemHandlerCap(CompoundTag tagCompound) {
        getCapability(ForgeCapabilities.ITEM_HANDLER)
                .filter(h -> h instanceof INBTSerializable)
                .map(h -> (INBTSerializable) h)
                .ifPresent(h -> tagCompound.put("Items", h.serializeNBT()));
    }

    protected void saveEnergyCap(CompoundTag tagCompound) {
        getCapability(ForgeCapabilities.ENERGY)
                .filter(h -> h instanceof INBTSerializable)
                .map(h -> (INBTSerializable) h)
                .ifPresent(h -> tagCompound.put("Energy", h.serializeNBT()));
    }

    protected void saveInfo(CompoundTag tagCompound) {
        CompoundTag infoTag = getOrCreateInfo(tagCompound);
        infoTag.putByte("powered", (byte) powerLevel);
        saveRSMode(infoTag);
        infoTag.putString("owner", ownerName);
        if (ownerUUID != null) {
            infoTag.putUUID("ownerId", ownerUUID);
        }
        if (securityChannel != -1) {
            infoTag.putInt("secChannel", securityChannel);
        }
    }

    protected void saveRSMode(CompoundTag infoTag) {
        if (needsRedstoneMode()) {
            infoTag.putByte("rsMode", (byte) rsMode.ordinal());
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

    public boolean checkAccess(Player player) {
        return false;
    }

//    @Optional.Method(modid = "theoneprobe")
//    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
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
//        if (SafeClientTools.isSneaking()) {
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

    private <T extends GenericTileEntity, V> BiConsumer<T, V> findSetter(Key<V> key) {
        // Cache or use Map?
        for (Map.Entry<String, ValueHolder<?, ?>> entry : getValueMap().entrySet()) {
            ValueHolder value = entry.getValue();
            if (key.name().equals(value.key().name())) {
                return value.setter();
            }
        }
        return null;
    }

    /// Override this function if you have a tile entity that needs to be opened remotely and thus has to 'fake' the real dimension
    public ResourceKey<Level> getDimension() {
        return level.dimension();
    }

    /**
     * Call this client-side to this TE to request data from the server
     */
    public void requestDataFromServer(SimpleChannel channel, ICommand command, @Nonnull TypedMap params) {
        channel.sendToServer(PacketRequestDataFromServer.create(getDimension(), worldPosition, command.name(), params, false));
    }

    /**
     * Execute a client side command (annotated with @ServerCommand)
     */
    public boolean executeClientCommand(String command, Player player, @Nonnull TypedMap params) {
        AnnotationHolder holder = getAnnotationHolder();
        IRunnable clientCommand = holder.clientCommands.get(command);
        if (clientCommand != null) {
            clientCommand.run(this, player, params);
            return true;
        }
        return false;
    }

    /**
     * Find a server command
     */
    public IRunnable<?> findServerCommand(String command) {
        AnnotationHolder holder = getAnnotationHolder();
        return holder.serverCommands.get(command);
    }

    /**
     * Execute a server side command (annotated with @ServerCommand). Note! Do not call this client-side!
     * This is meant to be called server side. If you want to call this client-side use the
     * PacketServerCommandTyped packet
     */
    public boolean executeServerCommand(String command, Player player, @Nonnull TypedMap params) {
        AnnotationHolder holder = getAnnotationHolder();
        IRunnable serverCommand = holder.serverCommands.get(command);
        if (serverCommand != null) {
            serverCommand.run(this, player, params);
            return true;
        }
        return false;
    }

    /**
     * Execute a server side listcommand (annotated with @ServerCommand). Note! Do not call this client-side!
     * This is meant to be called server side. If you want to call this client-side use the
     * PacketGetListFromServer packet
     */
    public <T> List<T> executeServerCommandList(String command, Player player, @Nonnull TypedMap params, @Nonnull Class<T> type) {
        AnnotationHolder holder = getAnnotationHolder();
        IRunnableWithListResult cmd = holder.serverCommandsWithListResult.get(command);
        if (cmd != null) {
            return cmd.run(this, player, params);
        }
        return Collections.emptyList();
    }

    /**
     * Execute a client side command that handles the list sent by the server side ListCommand
     */
    public <T> boolean handleListFromServer(String command, Player player, @Nonnull TypedMap params, @Nonnull List<T> list) {
        AnnotationHolder holder = getAnnotationHolder();
        IRunnableWithList cmd = holder.clientCommandsWithList.get(command);
        if (cmd != null) {
            cmd.run(this, player, params, list);
            return true;
        }
        return false;
    }

    /**
     * Execute a server side command with a return value (annotated with @ServerCommand). Note! Do not call this client-side!
     * This is meant to be called server side. If you want to call this client-side use the
     * PacketRequestDataFromServer packet
     */
    @Nullable
    public TypedMap executeServerCommandWR(String command, Player player, @Nonnull TypedMap params) {
        AnnotationHolder holder = getAnnotationHolder();
        IRunnableWithResult serverCommand = holder.serverCommandsWithResult.get(command);
        if (serverCommand != null) {
            return serverCommand.run(this, player, params);
        }
        return null;
    }

    private AnnotationHolder getAnnotationHolder() {
        AnnotationHolder holder = AnnotationHolder.annotations.get(getType());
        if (holder == null) {
            holder = AnnotationTools.createAnnotationHolder(getType(), getClass());
        }
        return holder;
    }

    @ServerCommand
    public static final Command<?> COMMAND_SYNC_BINDING = Command.create("generic.syncBinding",
            (te, playerEntity, params) -> te.syncBinding(params));

    @ServerCommand
    public static final Command<?> CMD_RSMODE = Command.create("mcjtylib.setRsMode",
            (te, playerEntity, params) -> te.setRSMode(RedstoneMode.values()[params.get(ImageChoiceLabel.PARAM_CHOICE_IDX)]));

    private <T> void syncBindingHelper(TypedMap params, Key<T> bkey) {
        T o = params.get(bkey);
        findSetter(bkey).accept(this, o);
    }

    private void syncBinding(TypedMap params) {
        for (Key<?> key : params.getKeys()) {
            syncBindingHelper(params, key);
        }
        markDirtyClient();
    }
}
