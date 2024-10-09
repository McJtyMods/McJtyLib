package mcjty.lib.tileentity;

import io.netty.buffer.ByteBuf;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.blockcommands.*;
import mcjty.lib.container.AutomationFilterItemHander;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.gui.widgets.ImageChoiceLabel;
import mcjty.lib.multipart.PartSlot;
import mcjty.lib.setup.Registration;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.RedstoneMode;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.codec.StreamCodec;
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
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.common.util.Lazy;
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

    private final Map<AttachmentType<?>, StreamCodec<? extends ByteBuf, ?>> attachments = new HashMap<>();

    protected byte powerLevel;

    // This is a generated function (from the annotated capabilities) that is initially (by the TE
    // constructor) set to be a function that looks for the annotations and replaces itself with
    // a function that does the actual testing
    // @todo 1.21
    private BiFunction<BlockCapability, Direction, Object> capSetup;
    private final List<Lazy<?>> lazyOptsToClean = new ArrayList<>();

    public GenericTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        // Setup code to find the capability annotations and in that code we
        // replace 'capSetup' with the actual code to execute the annotations
        // @todo 1.21
//        capSetup = (cap,dir) -> {
//            List<Pair<Field, Cap>> list = getAnnotationHolder().capabilityList;
//            capSetup = generateCapTests(list, 0);
//            return capSetup.apply(cap, dir);
//        };
        // Make sure the annotation holder exists
//        getAnnotationHolder();
    }

    protected <T> void registerAttachment(AttachmentType<T> type, StreamCodec<? extends ByteBuf, T> codec) {
        attachments.put(type, codec);
    }

    // @todo 1.21
    private BiFunction<BlockCapability, Direction, Object> generateCapTests(List<Pair<Field, Cap>> caps, int index) {
        if (index >= caps.size()) {
            return null;    // @todo 1.21
        } else {
            try {
                Cap annotation = caps.get(index).getRight();
                Object instance = FieldUtils.readField(caps.get(index).getLeft(), this, true);
                Lazy<?> lazy;
                if (instance instanceof Lazy<?>) {
                    lazy = Lazy.of(() -> ((Lazy<?>) instance).get());
                } else if (annotation.type() == CapType.ITEMS_AUTOMATION) {
                    lazy = Lazy.of(() -> new AutomationFilterItemHander((GenericItemHandler) instance));
                } else {
                    lazy = Lazy.of(() -> instance);
                }
                lazyOptsToClean.add(lazy);
                BiFunction<BlockCapability, Direction, Object> tail = generateCapTests(caps, index + 1);
                BlockCapability desiredCapability = annotation.type().getCapability();
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
        if (this.powerLevel != powered) {
            this.powerLevel = (byte) powered;
            setChanged();
        }
    }

    public int getPowerLevel() {
        return powerLevel;
    }

    public RedstoneMode getRSMode() {
        return getData(Registration.BASE_BE_DATA).rsMode();
    }

    public void setRSMode(RedstoneMode redstoneMode) {
        BaseBEData data = getData(Registration.BASE_BE_DATA).withRedstoneMode(redstoneMode);
        setData(Registration.BASE_BE_DATA, data);
        markDirtyClient();
    }

    public void setRSModeInt(int i) {
        setRSMode(RedstoneMode.values()[i]);
    }

    public int getRSModeInt() {
        return getRSMode().ordinal();
    }

    // Use redstone mode and input power to decide if this is enabled or not
    public boolean isMachineEnabled() {
        BaseBEData data = getData(Registration.BASE_BE_DATA);
        if (data.rsMode() != RedstoneMode.REDSTONE_IGNORED) {
            boolean rs = this.powerLevel > 0;
            if (data.rsMode() == RedstoneMode.REDSTONE_OFFREQUIRED && rs) {
                return false;
            } else if (data.rsMode() == RedstoneMode.REDSTONE_ONREQUIRED && !rs) {
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
        CompoundTag tag = new CompoundTag();
        this.saveClientDataToNBT(tag);
        return ClientboundBlockEntityDataPacket.create(this, (BlockEntity entity, RegistryAccess access) -> tag);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveClientDataToNBT(tag);
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        CompoundTag tag = pkt.getTag();
        loadClientDataFromNBT(tag);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);
        loadClientDataFromNBT(tag);
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

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putByte("power", powerLevel);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        powerLevel = tag.getByte("power");
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
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(Registration.ITEM_BASE_BE_DATA, getData(Registration.BASE_BE_DATA));
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        BaseBEData data = input.get(Registration.ITEM_BASE_BE_DATA);
        if (data != null) {
            setData(Registration.BASE_BE_DATA, data);
        }
    }

    public boolean setOwner(Player player) {
        if (!GeneralConfig.manageOwnership.get()) {
            return false;
        }

        BaseBEData data = getData(Registration.BASE_BE_DATA);
        if (data.owner() != null) {
            // Already has an owner.
            return false;
        }
        data = data.withOwner(player.getGameProfile().getId(), player.getName().getString());
        setData(Registration.BASE_BE_DATA, data);
        markDirtyClient();

        return true;
    }

    public void clearOwner() {
        if (!GeneralConfig.manageOwnership.get()) {
            return;
        }
        BaseBEData data = getData(Registration.BASE_BE_DATA);
        data = data.withOwner(null, "");
        data = data.withSecurityChannel(-1);
        setData(Registration.BASE_BE_DATA, data);
        markDirtyClient();
    }

    public void setSecurityChannel(int id) {
        BaseBEData data = getData(Registration.BASE_BE_DATA);
        data = data.withSecurityChannel(id);
        setData(Registration.BASE_BE_DATA, data);
        markDirtyClient();
    }

    public int getSecurityChannel() {
        return getData(Registration.BASE_BE_DATA).securityChannel();
    }

    public String getOwnerName() {
        return getData(Registration.BASE_BE_DATA).ownerName();
    }

    public UUID getOwnerUUID() {
        return getData(Registration.BASE_BE_DATA).owner();
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

    /// Override this function if you have a be entity that needs to be opened remotely and thus has to 'fake' the real dimension
    public ResourceKey<Level> getDimension() {
        return level.dimension();
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
        AnnotationHolder holder = AnnotationHolder.annotations.get(getClass());
//        if (holder == null) {
//            holder = AnnotationTools.createAnnotationHolder(getClass());
//        }
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
