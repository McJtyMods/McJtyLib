package mcjty.lib.tileentity;

import mcjty.lib.McJtyLib;
import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.information.CapabilityPowerInformation;
import mcjty.lib.api.infusable.CapabilityInfusable;
import mcjty.lib.api.module.CapabilityModuleSupport;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.bindings.DefaultValue;
import mcjty.lib.bindings.IValue;
import mcjty.lib.bindings.Val;
import mcjty.lib.bindings.Value;
import mcjty.lib.blockcommands.*;
import mcjty.lib.container.AutomationFilterItemHander;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.gui.widgets.ImageChoiceLabel;
import mcjty.lib.multipart.PartSlot;
import mcjty.lib.network.PacketRequestDataFromServer;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.items.CapabilityItemHandler;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class GenericTileEntity extends TileEntity {

    public static final Key<Integer> VALUE_RSMODE = new Key<>("rsmode", Type.INTEGER);

    private String ownerName = "";
    private UUID ownerUUID = null;
    private int securityChannel = -1;

    protected RedstoneMode rsMode = RedstoneMode.REDSTONE_IGNORED;
    protected int powerLevel = 0;

    private static final Map<TileEntityType, AnnotationHolder> annotations = new HashMap<>();

    // This is a generated function (from the annotated capabilities) that is initially (by the TE
    // constructor) set to be a function that looks for tne annotations and replaces itself with
    // a function that does the actual testing
    private BiFunction<Capability, Direction, LazyOptional> capSetup;

    public GenericTileEntity(TileEntityType<?> type) {
        super(type);
        // Setup code to find the capability annotations and in that code we
        // replace 'capSetup' with the actual code to execute the annotations
        capSetup = (cap,dir) -> {
            Field[] caps = CapScanner.scan(getClass(), this);
            capSetup = generateCapTests(caps, 0);
            return capSetup.apply(cap, dir);
        };
    }

    private BiFunction<Capability, Direction, LazyOptional> generateCapTests(Field[] caps, int index) {
        if (index >= caps.length) {
            return super::getCapability;
        } else {
            try {
                Cap annotation = caps[index].getAnnotation(Cap.class);
                Object instance = FieldUtils.readField(caps[index], this, true);
                LazyOptional lazy;
                if (instance instanceof LazyOptional) {
                    lazy = (LazyOptional) instance;
                } else if (annotation.type() == CapType.ITEMS_AUTOMATION) {
                    lazy = LazyOptional.of(() -> new AutomationFilterItemHander((NoDirectionItemHander) instance));
                } else {
                    lazy = LazyOptional.of(() -> instance);
                }
                BiFunction<Capability, Direction, LazyOptional> tail = generateCapTests(caps, index + 1);
                switch (annotation.type()) {
                    case ITEMS:
                        return (cap, dir) -> {
                            if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                                return lazy;
                            } else {
                                return tail.apply(cap, dir);
                            }
                        };
                    case ITEMS_AUTOMATION:
                        return (cap, dir) -> {
                            if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                                return lazy;
                            } else {
                                return tail.apply(cap, dir);
                            }
                        };
                    case CONTAINER:
                        return (cap, dir) -> {
                            if (cap == CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY) {
                                return lazy;
                            } else {
                                return tail.apply(cap, dir);
                            }
                        };
                    case ENERGY:
                        return (cap, dir) -> {
                            if (cap == CapabilityEnergy.ENERGY) {
                                return lazy;
                            } else {
                                return tail.apply(cap, dir);
                            }
                        };
                    case INFUSABLE:
                        return (cap, dir) -> {
                            if (cap == CapabilityInfusable.INFUSABLE_CAPABILITY) {
                                return lazy;
                            } else {
                                return tail.apply(cap, dir);
                            }
                        };
                    case MODULE:
                        return (cap, dir) -> {
                            if (cap == CapabilityModuleSupport.MODULE_CAPABILITY) {
                                return lazy;
                            } else {
                                return tail.apply(cap, dir);
                            }
                        };
                    case POWER_INFO:
                        return (cap, dir) -> {
                            if (cap == CapabilityPowerInformation.POWER_INFORMATION_CAPABILITY) {
                                return lazy;
                            } else {
                                return tail.apply(cap, dir);
                            }
                        };
                    case FLUIDS:
                        return (cap, dir) -> {
                            if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
                                return lazy;
                            } else {
                                return tail.apply(cap, dir);
                            }
                        };
                }
                throw new RuntimeException("Unknown cap type");
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
            getLevel().sendBlockUpdated(getBlockPos(), state, state, Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }

    public void markDirtyQuick() {
        if (getLevel() != null) {
            getLevel().blockEntityChanged(this.worldPosition, this);
        }
    }

    public Map<String, IValue<?>> getValueMap() {
        AnnotationHolder holder = getAnnotationHolder();
        return holder.valueMap;
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
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        readClientDataFromNBT(tag);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbtTag = new CompoundNBT();
        this.writeClientDataToNBT(nbtTag);
        return new SUpdateTileEntityPacket(worldPosition, 1, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
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

    public boolean canPlayerAccess(PlayerEntity player) {
        return !isRemoved() && player.distanceToSqr(new Vector3d(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()).add(0.5D, 0.5D, 0.5D)) <= 64D;
    }

    @Nonnull
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
    }

    /**
     * For compatibility reasons this calls read() but for
     * efficiency reasons you should override this in your tile
     * entity to only read what you need on the client.
     *
     * @param tagCompound
     */
    public void readClientDataFromNBT(CompoundNBT tagCompound) {
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
    public void load(@Nonnull BlockState state, @Nonnull CompoundNBT tagCompound) {
        super.load(state, tagCompound);
        read(tagCompound);
    }

    public void read(CompoundNBT tagCompound) {
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
            if (infoTag.contains("powered")) {
                powerLevel = infoTag.getByte("powered");
            }
            if (needsRedstoneMode() && infoTag.contains("rsMode")) {
                int m = infoTag.getByte("rsMode");
                rsMode = RedstoneMode.values()[m];
            }

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

    protected CompoundNBT getOrCreateInfo(CompoundNBT tagCompound) {
        if (tagCompound.contains("Info")) {
            return tagCompound.getCompound("Info");
        }
        CompoundNBT data = new CompoundNBT();
        tagCompound.put("Info", data);
        return data;
    }

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT tagCompound) {
        super.save(tagCompound);
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
            infoTag.putUUID("ownerId", ownerUUID);
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
        for (Map.Entry<String, IValue<?>> entry : getValueMap().entrySet()) {
            IValue value = entry.getValue();
            if (key.getName().equals(value.getKey().getName())) {
                return value.setter();
            }
        }
        return null;
    }

    /// Override this function if you have a tile entity that needs to be opened remotely and thus has to 'fake' the real dimension
    public RegistryKey<World> getDimension() {
        return level.dimension();
    }

    /**
     * Call this client-side to this TE to request data from the server
     */
    public void requestDataFromServer(SimpleChannel channel, Command<?> command, @Nonnull TypedMap params) {
        channel.sendToServer(new PacketRequestDataFromServer(getDimension(), worldPosition, command.getName(), params, false));
    }

    /**
     * Execute a client side command (annotated with @ServerCommand)
     */
    public boolean executeClientCommand(String command, PlayerEntity player, @Nonnull TypedMap params) {
        AnnotationHolder holder = getAnnotationHolder();
        ICommand clientCommand = holder.clientCommands.get(command);
        if (clientCommand != null) {
            clientCommand.run(this, player, params);
            return true;
        }
        return false;
    }

    /**
     * Find a server command
     */
    public ICommand<?> findServerCommand(String command) {
        AnnotationHolder holder = getAnnotationHolder();
        return holder.serverCommands.get(command);
    }

    /**
     * Execute a server side command (annotated with @ServerCommand)
     */
    public boolean executeServerCommand(String command, PlayerEntity player, @Nonnull TypedMap params) {
        AnnotationHolder holder = getAnnotationHolder();
        ICommand serverCommand = holder.serverCommands.get(command);
        if (serverCommand != null) {
            serverCommand.run(this, player, params);
            return true;
        }
        return false;
    }

    /**
     * Execute a server side listcommand (annotated with @ServerCommand)
     */
    public <T> List<T> executeServerCommandList(String command, PlayerEntity player, @Nonnull TypedMap params, @Nonnull Class<T> type) {
        AnnotationHolder holder = getAnnotationHolder();
        ICommandWithListResult cmd = holder.serverCommandsWithListResult.get(command);
        if (cmd != null) {
            return cmd.run(this, player, params);
        }
        return Collections.emptyList();
    }

    /**
     * Helper command that's useful from within client side packets to execute a client command with a list
     */
    public static <T> void executeClientCommandHelper(BlockPos pos, String command, List<T> list) {
        TileEntity te = McJtyLib.proxy.getClientWorld().getBlockEntity(pos);
        if (te instanceof GenericTileEntity) {
            ((GenericTileEntity) te).executeClientCommandList(command, McJtyLib.proxy.getClientPlayer(), TypedMap.EMPTY, list);
        } else {
            Logging.logError("Can't handle command '" + command + "'!");
        }
    }

    /**
     * Execute a client side command that handles the list sent by the server side ListCommand
     */
    public <T> boolean executeClientCommandList(String command, PlayerEntity player, @Nonnull TypedMap params, @Nonnull List<T> list) {
        AnnotationHolder holder = getAnnotationHolder();
        ICommandWithList cmd = holder.clientCommandsWithList.get(command);
        if (cmd != null) {
            cmd.run(this, player, params, list);
            return true;
        }
        return false;
    }

    /**
     * Execute a server side command with a return value (annotated with @ServerCommand)
     */
    @Nullable
    public TypedMap executeServerCommandWR(String command, PlayerEntity player, @Nonnull TypedMap params) {
        AnnotationHolder holder = getAnnotationHolder();
        ICommandWithResult serverCommand = holder.serverCommandsWithResult.get(command);
        if (serverCommand != null) {
            return serverCommand.run(this, player, params);
        }
        return null;
    }

    private AnnotationHolder getAnnotationHolder() {
        AnnotationHolder holder = annotations.get(getType());

        if (holder == null) {
            holder = new AnnotationHolder();
            annotations.put(getType(), holder);
            Field[] commandFields = FieldUtils.getFieldsWithAnnotation(getClass(), ServerCommand.class);
            for (Field field : commandFields) {
                ServerCommand serverCommand = field.getAnnotation(ServerCommand.class);
                try {
                    Object o = field.get(this);
                    if (o instanceof Command) {
                        Command cmd = (Command) o;
                        if (cmd.getCmd() != null) {
                            holder.serverCommands.put(cmd.getName(), cmd.getCmd());
                        }
                        if (cmd.getCmdWithResult() != null) {
                            holder.serverCommandsWithResult.put(cmd.getName(), cmd.getCmdWithResult());
                        }
                        if (cmd.getClientCommand() != null) {
                            holder.clientCommands.put(cmd.getName(), cmd.getClientCommand());
                        }
                    } else if (o instanceof ListCommand) {
                        ListCommand cmd = (ListCommand) o;
                        holder.serverCommandsWithListResult.put(cmd.getName(), cmd.getCmd());
                        holder.clientCommandsWithList.put(cmd.getName(), cmd.getClientCommand());
                    } else {
                        throw new IllegalStateException("Only use @ServerCommand with either a Command, a ListCommand or a ResultCommand!");
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            Field[] valFields = FieldUtils.getFieldsWithAnnotation(getClass(), Val.class);
            for (Field field : valFields) {
                Val val = field.getAnnotation(Val.class);
                try {
                    Value value = (Value) field.get(this);
                    holder.valueMap.put(value.getKey().getName(), new DefaultValue<>(value.getKey(), () -> value.getSupplier().apply(this), o -> value.getConsumer().accept(this, o)));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            if (needsRedstoneMode()) {
                holder.valueMap.put(VALUE_RSMODE.getName(), new DefaultValue<>(VALUE_RSMODE, this::getRSModeInt, this::setRSModeInt));
            }
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
        findSetter(bkey).accept(o);
    }

    private void syncBinding(TypedMap params) {
        for (Key<?> key : params.getKeys()) {
            syncBindingHelper(params, key);
        }
        markDirtyClient();
    }
}
