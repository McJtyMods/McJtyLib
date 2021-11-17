package mcjty.lib.blocks;

import mcjty.lib.McJtyLib;
import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.module.CapabilityModuleSupport;
import mcjty.lib.api.smartwrench.SmartWrench;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.compat.CofhApiItemCompatibility;
import mcjty.lib.compat.theoneprobe.TOPDriver;
import mcjty.lib.compat.theoneprobe.TOPInfoProvider;
import mcjty.lib.compat.waila.WailaInfoProvider;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.multipart.IPartBlock;
import mcjty.lib.multipart.PartSlot;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.lib.varia.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class BaseBlock extends Block implements WailaInfoProvider, TOPInfoProvider, IPartBlock, ITooltipSettings {

    private final boolean infusable;
    private final Supplier<TileEntity> tileEntitySupplier;
    private final TooltipBuilder tooltipBuilder;
    private final ToolType toolType;
    private final int harvestLevel;
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
        this.toolType = builder.getToolType();
        this.harvestLevel = builder.getHarvestLevel();
        this.topDriver = builder.getTopDriver();
        this.manualEntry = builder.getManualEntry();
    }

    @Override
    public ManualEntry getManualEntry() {
        return manualEntry;
    }

    public static void setInfused(ItemStack stack, int infused) {
        NBTTools.setInfoNBT(stack, CompoundNBT::putInt, "infused", infused);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable IBlockReader world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag advanced) {
        intAddInformation(stack, tooltip);

        if (tooltipBuilder.isActive()) {
            tooltipBuilder.makeTooltip(getRegistryName(), stack, tooltip, advanced);
        }
    }

    private void intAddInformation(ItemStack itemStack, List<ITextComponent> list) {
        CompoundNBT tagCompound = itemStack.getTag();
        if (tagCompound != null) {
            if (tagCompound.contains("Energy")) {
                long energy = tagCompound.getLong("Energy");
                list.add(new StringTextComponent(TextFormatting.GREEN + "Energy: " + energy + " rf"));
            }
            if (isInfusable()) {
                int infused = getInfused(itemStack);
                int pct = infused * 100 / GeneralConfig.maxInfuse.get();
                list.add(new StringTextComponent(TextFormatting.YELLOW + "Infused: " + pct + "%"));
            }

            if (GeneralConfig.manageOwnership.get() && tagCompound.contains("owner")) {
                String owner = tagCompound.getString("owner");
                int securityChannel = -1;
                if (tagCompound.contains("secChannel")) {
                    securityChannel = tagCompound.getInt("secChannel");
                }

                if (securityChannel == -1) {
                    list.add(new StringTextComponent(TextFormatting.YELLOW + "Owned by: " + owner));
                } else {
                    list.add(new StringTextComponent(TextFormatting.YELLOW + "Owned by: " + owner + " (channel " + securityChannel + ")"));
                }

                if (!tagCompound.contains("idM")) {
                    list.add(new StringTextComponent(TextFormatting.RED + "Warning! Ownership not correctly set! Please place block again!"));
                }
            }
        }
    }

    public static int getInfused(ItemStack itemStack) {
        return NBTTools.getInfoNBT(itemStack, CompoundNBT::getInt, "infused", 0);
    }

    // This if this block was activated with a wrench
    private WrenchUsage testWrenchUsage(BlockPos pos, PlayerEntity player) {
        ItemStack itemStack = player.getItemInHand(Hand.MAIN_HAND);
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

    protected WrenchUsage getWrenchUsage(BlockPos pos, PlayerEntity player, ItemStack itemStack, WrenchUsage wrenchUsed, Item item) {
        if (item instanceof SmartWrench) {
            switch(((SmartWrench)item).getMode(itemStack)) {
                case MODE_WRENCH: return WrenchUsage.NORMAL;
                case MODE_SELECT: return player.isShiftKeyDown() ? WrenchUsage.SNEAK_SELECT : WrenchUsage.SELECT;
                default:          throw new RuntimeException("SmartWrench in unknown mode!");
            }
        } else if (McJtyLib.cofhapiitem && CofhApiItemCompatibility.isToolHammer(item)) {
            return CofhApiItemCompatibility.getWrenchUsage(item, itemStack, player, pos);
        } else if (WrenchChecker.isAWrench(item)) {
            return WrenchUsage.NORMAL;
        }
        return wrenchUsed;
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public ActionResultType use(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult result) {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof GenericTileEntity) {
            ActionResultType resultType = ((GenericTileEntity) te).onBlockActivated(state, player, hand, result);
            if (resultType != ActionResultType.PASS) {
                return resultType;
            }
        }
        ItemStack heldItem = player.getItemInHand(hand);
        if (handleModule(world, pos, state, player, hand, heldItem, result)) {
            return ActionResultType.SUCCESS;
        }
        WrenchUsage wrenchUsed = testWrenchUsage(pos, player);
        switch (wrenchUsed) {
            case NOT:          return openGui(world, pos.getX(), pos.getY(), pos.getZ(), player) ? ActionResultType.SUCCESS : ActionResultType.PASS;
            case NORMAL:       return wrenchUse(world, pos, result.getDirection(), player) ? ActionResultType.SUCCESS : ActionResultType.PASS;
            case SNEAKING:     return wrenchSneak(world, pos, player) ? ActionResultType.SUCCESS : ActionResultType.PASS;
            case DISABLED:     return wrenchDisabled(world, pos, player) ? ActionResultType.SUCCESS : ActionResultType.PASS;
            case SELECT:       return wrenchSelect(world, pos, player) ? ActionResultType.SUCCESS : ActionResultType.PASS;
            case SNEAK_SELECT: return wrenchSneakSelect(world, pos, player) ? ActionResultType.SUCCESS : ActionResultType.PASS;
        }
        return ActionResultType.PASS;
    }

    public boolean handleModule(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, ItemStack heldItem, BlockRayTraceResult result) {
        if (!heldItem.isEmpty()) {
            TileEntity te = world.getBlockEntity(pos);
            if (te != null) {
                return te.getCapability(CapabilityModuleSupport.MODULE_CAPABILITY).map(h -> {
                    if (h.isModule(heldItem)) {
                        if (ModuleTools.installModule(player, heldItem, hand, pos, h.getFirstSlot(), h.getLastSlot())) {
                            return true;
                        }
                    }
                    return false;
                }).orElse(false);
            }
        }
        return false;
    }

    protected boolean wrenchUse(World world, BlockPos pos, Direction side, PlayerEntity player) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof GenericTileEntity) {
            if (!((GenericTileEntity) tileEntity).wrenchUse(world, pos, side, player)) {
                rotate(world.getBlockState(pos), world, pos, Rotation.CLOCKWISE_90);
            }
        } else {
            rotate(world.getBlockState(pos), world, pos, Rotation.CLOCKWISE_90);
        }
        return true;
    }

    protected boolean wrenchSneak(World world, BlockPos pos, PlayerEntity player) {
        // @todo
        breakAndRemember(world, player, pos);
        return true;
    }

    protected void breakAndRemember(World world, PlayerEntity player, BlockPos pos) {
        if (!world.isClientSide) {
            playerDestroy(world, player, pos, world.getBlockState(pos), world.getBlockEntity(pos), ItemStack.EMPTY);
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }


    protected boolean wrenchDisabled(World world, BlockPos pos, PlayerEntity player) {
        return false;
    }

    protected boolean wrenchSelect(World world, BlockPos pos, PlayerEntity player) {
        return false;
    }

    protected boolean wrenchSneakSelect(World world, BlockPos pos, PlayerEntity player) {
        return false;
    }

    protected boolean openGui(World world, int x, int y, int z, PlayerEntity player) {
        TileEntity te = world.getBlockEntity(new BlockPos(x, y, z));
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
            NetworkHooks.openGui((ServerPlayerEntity) player, h, te.getBlockPos());
            return true;
        }).orElse(false);
    }


    @Override
    public void setPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        if (!world.isClientSide && GeneralConfig.manageOwnership.get()) {
            setOwner(world, pos, placer);
        }

        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof GenericTileEntity) {
            GenericTileEntity genericTileEntity = (GenericTileEntity) te;
            genericTileEntity.onBlockPlacedBy(world, pos, state, placer, stack);
        }

        checkRedstone(world, pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, blockIn, fromPos, isMoving);
        checkRedstone(world, pos);
    }

    protected void checkRedstone(World world, BlockPos pos) {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof GenericTileEntity) {
            ((GenericTileEntity) te).checkRedstone(world, pos);
        }
    }


    protected void setOwner(World world, BlockPos pos, LivingEntity entity) {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof GenericTileEntity && entity instanceof PlayerEntity) {
            GenericTileEntity genericTileEntity = (GenericTileEntity) te;
            PlayerEntity player = (PlayerEntity) entity;
            genericTileEntity.setOwner(player);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean triggerEvent(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, int id, int param) {
        if (hasTileEntity(state)) {
            super.triggerEvent(state, worldIn, pos, id, param);
            TileEntity tileentity = worldIn.getBlockEntity(pos);
            return tileentity == null ? false : tileentity.triggerEvent(id, param);
        } else {
            return super.triggerEvent(state, worldIn, pos, id, param);
        }
    }

    protected boolean checkAccess(World world, PlayerEntity player, TileEntity te) {
        if (te instanceof GenericTileEntity) {
            ((GenericTileEntity) te).checkAccess(player);
        }
        return false;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(@Nonnull BlockState state) {
        return toolType;
    }

    @Override
    public int getHarvestLevel(@Nonnull BlockState state) {
        return harvestLevel;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return tileEntitySupplier != null;
    }

    public RotationType getRotationType() {
        return RotationType.ROTATION;
    }

    public boolean isInfusable() {
        return infusable;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        if (tileEntitySupplier == null) {
            return null;
        } else {
            return tileEntitySupplier.get();
        }
    }

    @Nonnull
    @Override
    public PartSlot getSlotFromState(World world, BlockPos pos, BlockState newState) {
        return PartSlot.NONE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newstate, boolean isMoving) {
        if (!world.isClientSide) {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof GenericTileEntity) {
                GenericTileEntity genericTileEntity = (GenericTileEntity) te;
                genericTileEntity.onReplaced(world, pos, state, newstate);
            }
        }
        super.onRemove(state, world, pos, newstate, isMoving);
    }

    protected Property<?>[] getProperties() {
        return getProperties(getRotationType());
    }

    public static Property<?>[] getProperties(RotationType rotationType) {
        switch (rotationType) {
            case HORIZROTATION:
                return HORIZ_PROPERTIES;
            case ROTATION:
                return ROTATING_PROPERTIES;
            case NONE:
            default:
                return NONE_PROPERTIES;
        }
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        for (Property<?> property : getProperties()) {
            builder.add(property);
        }
    }


    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        PlayerEntity placer = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        BlockState state = super.getStateForPlacement(context);
        switch (getRotationType()) {
            case HORIZROTATION:
                return state.setValue(BlockStateProperties.HORIZONTAL_FACING, placer.getDirection().getOpposite());
            case ROTATION:
                return state.setValue(BlockStateProperties.FACING, OrientationTools.getFacingFromEntity(pos, placer));
            case NONE:
            default:
                return state;
        }
    }

    protected Direction getOrientation(BlockPos pos, LivingEntity entity) {
        switch (getRotationType()) {
            case HORIZROTATION:
                return OrientationTools.determineOrientationHoriz(entity);
            case ROTATION:
                return OrientationTools.determineOrientation(pos, entity);
            case NONE:
            default:
                return null;
        }
    }

    public Direction getFrontDirection(BlockState state) {
        switch (getRotationType()) {
            case HORIZROTATION:
                return state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            case ROTATION:
                return state.getValue(BlockStateProperties.FACING);
            case NONE:
            default:
                return Direction.NORTH;
        }
    }

    @Override
    public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation rot) {
        switch (getRotationType()) {
            case HORIZROTATION:
                state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, rot.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
                break;
            case ROTATION:
                state = state.setValue(BlockStateProperties.FACING, rot.rotate(state.getValue(BlockStateProperties.FACING)));
                break;
            case NONE:
                break;
        }
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof GenericTileEntity) {
            ((GenericTileEntity) tileEntity).rotateBlock(rot);
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
        switch (rotationType) {
            case HORIZROTATION:
                return OrientationTools.getOrientationHoriz(state);
            case ROTATION:
                return OrientationTools.getOrientation(state);
            case NONE:
            default:
                return Direction.SOUTH;
        }
    }

    @Override
    public TOPDriver getProbeDriver() {
        return topDriver;
    }
}
