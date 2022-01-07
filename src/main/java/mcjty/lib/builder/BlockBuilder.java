package mcjty.lib.builder;

import mcjty.lib.compat.theoneprobe.McJtyLibTOPDriver;
import mcjty.lib.compat.theoneprobe.TOPDriver;
import mcjty.lib.gui.ManualEntry;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockBuilder {

    public static final BlockBehaviour.Properties STANDARD_IRON = BlockBehaviour.Properties.of(Material.METAL)
            .strength(2.0f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.METAL);

    private BlockBehaviour.Properties properties = STANDARD_IRON;
    private boolean infusable = false;
    private final TooltipBuilder tooltipBuilder = new TooltipBuilder();
    private BlockEntityType.BlockEntitySupplier<BlockEntity> tileEntitySupplier;
    private TOPDriver topDriver = McJtyLibTOPDriver.DRIVER;
    private ManualEntry manualEntry = ManualEntry.EMPTY;

    public BlockBehaviour.Properties getProperties() {
        return properties;
    }

    public ManualEntry getManualEntry() {
        return manualEntry;
    }

    public boolean isInfusable() {
        return infusable;
    }

    public TooltipBuilder getTooltipBuilder() {
        return tooltipBuilder;
    }

    public BlockEntityType.BlockEntitySupplier<BlockEntity> getTileEntitySupplier() {
        return tileEntitySupplier;
    }

    public TOPDriver getTopDriver() {
        return topDriver;
    }

    // ----------------------------------------------------------------------

    public BlockBuilder properties(BlockBehaviour.Properties properties) {
        this.properties = properties;
        return this;
    }

    public BlockBuilder topDriver(TOPDriver driver) {
        this.topDriver = driver;
        return this;
    }

    public BlockBuilder infusable() {
        this.infusable = true;
        return this;
    }

    public BlockBuilder manualEntry(ManualEntry manualEntry) {
        this.manualEntry = manualEntry;
        return this;
    }

    public BlockBuilder info(InfoLine... lines) {
        tooltipBuilder.info(lines);
        return this;
    }

    public BlockBuilder infoShift(InfoLine... lines) {
        tooltipBuilder.infoShift(lines);
        return this;
    }

    public BlockBuilder infoAdvanced(InfoLine... lines) {
        tooltipBuilder.infoAdvanced(lines);
        return this;
    }

    public BlockBuilder tileEntitySupplier(BlockEntityType.BlockEntitySupplier<BlockEntity> supplier) {
        this.tileEntitySupplier = supplier;
        return this;
    }
}
