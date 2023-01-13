package mcjty.lib.blocks;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.module.CapabilityModuleSupport;
import mcjty.lib.api.smartwrench.SmartWrench;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.compat.theoneprobe.TOPDriver;
import mcjty.lib.compat.theoneprobe.TOPInfoProvider;
import mcjty.lib.compat.waila.WailaInfoProvider;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.multipart.IPartBlock;
import mcjty.lib.multipart.PartSlot;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.lib.varia.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class BaseBlock extends Block implements WailaInfoProvider, TOPInfoProvider, IPartBlock, ITooltipSettings, EntityBlock {

    private final boolean infusable;
    private final BlockEntityType.BlockEntitySupplier<BlockEntity> tileEntitySupplier;
    private final TooltipBuilder tooltipBuilder;
    private final TOPDriver topDriver;
    private final ManualEntry manualEntry;

    public static final Property<?>[] HORIZ_PROPERTIES = new Property[]{BlockStateProperties.HORIZONTAL_FACING};
    public static final Property<?>[] ROTATING_PROPERTIES = new Property[]{BlockStateProperties.FACING};
    public static final Property<?>[] NONE_PROPERTIES = new Property[0];

    public BaseBlock(BlockBuilder builder) {
        super(builder.getProperties());
        this.infusable = builder.isInfusable();
        this.tileEntitySupplier = builder.getTileEntitySupplier();
        this.tooltipBuilder = builder.getTooltipBuilder();
        this.topDriver = builder.getTopDriver();
        this.manualEntry = builder.getManualEntry();
    }

    @Override
    public ManualEntry getManualEntry() {
        return manualEntry;
    }

    public static void setInfused(ItemStack stack, int infused) {
        NBTTools.setInfoNBT(stack, CompoundTag::putInt, "infused", infused);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable BlockGetter world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag advanced) {
        intAddInformation(stack, tooltip);

        if (tooltipBuilder.isActive()) {
            tooltipBuilder.makeTooltip(Tools.getId(this), stack, tooltip, advanced);
        }
    }

    private void intAddInformation(ItemStack itemStack, List<Component> list) {
        CompoundTag tagCompound = itemStack.getTag();
        if (tagCompound != null) {
            if (tagCompound.contains("Energy")) {
                long energy = tagCompound.getLong("Energy");
                list.add(ComponentFactory.literal(ChatFormatting.GREEN + "Energy: " + energy + " rf"));
            }
            if (isInfusable()) {
                int infused = getInfused(itemStack);
                int pct = infused * 100 / GeneralConfig.maxInfuse.get();
                list.add(ComponentFactory.literal(ChatFormatting.YELLOW + "Infused: " + pct + "%"));
            }

            if (GeneralConfig.manageOwnership.get() && tagCompound.contains("owner")) {
                String owner = tagCompound.getString("owner");
                int securityChannel = -1;
                if (tagCompound.contains("secChannel")) {
                    securityChannel = tagCompound.getInt("secChannel");
                }

                if (securityChannel == -1) {
                    list.add(ComponentFactory.literal(ChatFormatting.YELLOW + "Owned by: " + owner));
                } else {
                    list.add(ComponentFactory.literal(ChatFormatting.YELLOW + "Owned by: " + owner + " (channel " + securityChannel + ")"));
                }

                if (!tagCompound.contains("idM")) {
                    list.add(ComponentFactory.literal(ChatFormatting.RED + "Warning! Ownership not correctly set! Please place block again!"));
                }
            }
        }
    }

    public static int getInfused(ItemStack itemStack) {
        return NBTTools.getInfoNBT(itemStack, CompoundTag::getInt, "infused", 0);
    }

    // This if this block was activated with a wrench
    private WrenchUsage testWrenchUsage(BlockPos pos, Player player) {
        ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        WrenchUsage wrenchUsed = WrenchUsage.NOT;
        if (!itemStack.isEmpty()) {
            Item item = itemStack.getItem();
            wrenchUsed = getWrenchUsage(pos, player, itemStack, wrenchUsed, item);
        }
        if (wrenchUsed == WrenchUsage.NORMAL && player.isShiftKeyDown()) {
            wrenchUsed = WrenchUsage.SNEAKING;
        }
        return wrenchUsed;
    }

    protected WrenchUsage getWrenchUsage(BlockPos pos, Player player, ItemStack itemStack, WrenchUsage wrenchUsed, Item item) {
        if (item instanceof SmartWrench wrench) {
            return switch (wrench.getMode(itemStack)) {
                case MODE_WRENCH -> WrenchUsage.NORMAL;
                case MODE_SELECT -> player.isShiftKeyDown() ? WrenchUsage.SNEAK_SELECT : WrenchUsage.SELECT;
            };
        } else if (WrenchChecker.isAWrench(item)) {
            return WrenchUsage.NORMAL;
        }
        return wrenchUsed;
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public InteractionResult use(@Nonnull BlockState state, Level world, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult result) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof GenericTileEntity genTileEntity) {
            InteractionResult resultType = genTileEntity.onBlockActivated(state, player, hand, result);
            if (resultType != InteractionResult.PASS) {
                return resultType;
            }
        }
        ItemStack heldItem = player.getItemInHand(hand);
        if (handleModule(world, pos, state, player, hand, heldItem, result)) {
            return InteractionResult.SUCCESS;
        }
        WrenchUsage wrenchUsed = testWrenchUsage(pos, player);
        return switch (wrenchUsed) {
            case NOT -> openGui(world, pos.getX(), pos.getY(), pos.getZ(), player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
            case NORMAL -> wrenchUse(world, pos, result.getDirection(), player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
            case SNEAKING -> wrenchSneak(world, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
            case DISABLED -> wrenchDisabled(world, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
            case SELECT -> wrenchSelect(world, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
            case SNEAK_SELECT -> wrenchSneakSelect(world, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        };
    }

    public boolean handleModule(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, ItemStack heldItem, BlockHitResult result) {
        if (!heldItem.isEmpty()) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te != null) {
                return te.getCapability(CapabilityModuleSupport.MODULE_CAPABILITY).map(h -> {
                    if (h.isModule(heldItem)) {
                        return ModuleTools.installModule(player, heldItem, hand, pos, h.getFirstSlot(), h.getLastSlot());
                    }
                    return false;
                }).orElse(false);
            }
        }
        return false;
    }

    protected boolean wrenchUse(Level world, BlockPos pos, Direction side, Player player) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof GenericTileEntity genTileEntity) {
            if (!genTileEntity.wrenchUse(world, pos, side, player)) {
                rotate(world.getBlockState(pos), world, pos, Rotation.CLOCKWISE_90);
            }
        } else {
            rotate(world.getBlockState(pos), world, pos, Rotation.CLOCKWISE_90);
        }
        return true;
    }

    protected boolean wrenchSneak(Level world, BlockPos pos, Player player) {
        // @todo
        breakAndRemember(world, player, pos);
        return true;
    }

    protected void breakAndRemember(Level world, Player player, BlockPos pos) {
        if (!world.isClientSide) {
            playerDestroy(world, player, pos, world.getBlockState(pos), world.getBlockEntity(pos), ItemStack.EMPTY);
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }


    protected boolean wrenchDisabled(Level world, BlockPos pos, Player player) {
        return false;
    }

    protected boolean wrenchSelect(Level world, BlockPos pos, Player player) {
        return false;
    }

    protected boolean wrenchSneakSelect(Level world, BlockPos pos, Player player) {
        return false;
    }

    protected boolean openGui(Level world, int x, int y, int z, Player player) {
        BlockEntity te = world.getBlockEntity(new BlockPos(x, y, z));
        if (te == null) {
            return false;
        }

        return te.getCapability(CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY).map(h -> {
            if (world.isClientSide) {
                return true;
            }
            if (checkAccess(world, player, te)) {
                return true;
            }
            NetworkHooks.openScreen((ServerPlayer) player, h, te.getBlockPos());
            return true;
        }).orElse(false);
    }


    @Override
    public void setPlacedBy(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        if (!world.isClientSide && GeneralConfig.manageOwnership.get()) {
            setOwner(world, pos, placer);
        }

        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof GenericTileEntity genericTileEntity) {
            genericTileEntity.onBlockPlacedBy(world, pos, state, placer, stack);
        }

        checkRedstone(world, pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, blockIn, fromPos, isMoving);
        checkRedstone(world, pos);
    }

    protected void checkRedstone(Level world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof GenericTileEntity genericTileEntity) {
            genericTileEntity.checkRedstone(world, pos);
        }
    }


    protected void setOwner(Level world, BlockPos pos, LivingEntity entity) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof GenericTileEntity genericTileEntity && entity instanceof Player player) {
            genericTileEntity.setOwner(player);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean triggerEvent(@Nonnull BlockState state, @Nonnull Level worldIn, @Nonnull BlockPos pos, int id, int param) {
        if (hasTileEntitySupplier()) {
            super.triggerEvent(state, worldIn, pos, id, param);
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            return tileentity != null && tileentity.triggerEvent(id, param);
        } else {
            return super.triggerEvent(state, worldIn, pos, id, param);
        }
    }

    protected boolean checkAccess(Level world, Player player, BlockEntity te) {
        if (te instanceof GenericTileEntity genericTileEntity) {
            genericTileEntity.checkAccess(player);
        }
        return false;
    }

    protected boolean hasTileEntitySupplier() {
        return tileEntitySupplier != null;
    }

    public RotationType getRotationType() {
        return RotationType.ROTATION;
    }

    public boolean isInfusable() {
        return infusable;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (hasTileEntitySupplier()) {
            return tileEntitySupplier.create(pos, state);
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState blockState, BlockEntityType<T> blockEntityType) {
        // @todo 1.18 figure out a way to only return a ticker client/server if needed
        return BaseBlock::runTick;
    }

    public static void runTick(Level world, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {
        if (blockEntity instanceof TickingTileEntity ticking) {
            ticking.tick();
        }
    }

    @Nonnull
    @Override
    public PartSlot getSlotFromState(Level world, BlockPos pos, BlockState newState) {
        return PartSlot.NONE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState newstate, boolean isMoving) {
        if (!world.isClientSide) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof GenericTileEntity genericTileEntity) {
                genericTileEntity.onReplaced(world, pos, state, newstate);
            }
        }
        super.onRemove(state, world, pos, newstate, isMoving);
    }

    protected Property<?>[] getProperties() {
        return getProperties(getRotationType());
    }

    public static Property<?>[] getProperties(RotationType rotationType) {
        return switch (rotationType) {
            case HORIZROTATION -> HORIZ_PROPERTIES;
            case ROTATION -> ROTATING_PROPERTIES;
            case NONE -> NONE_PROPERTIES;
        };
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        for (Property<?> property : getProperties()) {
            builder.add(property);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Player placer = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        BlockState state = super.getStateForPlacement(context);
        return switch (getRotationType()) {
            case HORIZROTATION -> state.setValue(BlockStateProperties.HORIZONTAL_FACING, placer.getDirection().getOpposite());
            case ROTATION -> state.setValue(BlockStateProperties.FACING, OrientationTools.getFacingFromEntity(pos, placer));
            case NONE -> state;
        };
    }

    protected Direction getOrientation(BlockPos pos, LivingEntity entity) {
        return switch (getRotationType()) {
            case HORIZROTATION -> OrientationTools.determineOrientationHoriz(entity);
            case ROTATION -> OrientationTools.determineOrientation(pos, entity);
            case NONE -> null;
        };
    }

    public Direction getFrontDirection(BlockState state) {
        return switch (getRotationType()) {
            case HORIZROTATION -> state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            case ROTATION -> state.getValue(BlockStateProperties.FACING);
            case NONE -> Direction.NORTH;
        };
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return switch (getRotationType()) {
            case HORIZROTATION -> state.setValue(BlockStateProperties.HORIZONTAL_FACING, rot.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
            case ROTATION -> state.setValue(BlockStateProperties.FACING, rot.rotate(state.getValue(BlockStateProperties.FACING)));
            case NONE -> state;
        };
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor world, BlockPos pos, Rotation rot) {
        state = rotate(state, rot);
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof GenericTileEntity genericTileEntity) {
            genericTileEntity.rotateBlock(rot);
        }
        return state;
    }

    public Direction getRightDirection(BlockState state) {
        return getFrontDirection(state).getCounterClockWise();
    }

    public Direction getLeftDirection(BlockState state) {
        return getFrontDirection(state).getClockWise();
    }

    public static Direction getFrontDirection(RotationType rotationType, BlockState state) {
        return switch (rotationType) {
            case HORIZROTATION -> OrientationTools.getOrientationHoriz(state);
            case ROTATION -> OrientationTools.getOrientation(state);
            case NONE -> Direction.SOUTH;
        };
    }

    @Override
    public TOPDriver getProbeDriver() {
        return topDriver;
    }

    public List<ItemStack> getItemsForTab() {
        // @todo 1.19.3
        return Collections.emptyList();
    }

    //    @Override
//    public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
//        List<ItemStack> stacks = getItemsForTab();
//        if (stacks.isEmpty()) {
//             super.fillItemCategory(group, items);
//        } else {
//             items.addAll(stacks);
//        }
//    }

}
