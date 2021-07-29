package mcjty.lib.builder;

import mcjty.lib.compat.theoneprobe.McJtyLibTOPDriver;
import mcjty.lib.compat.theoneprobe.TOPDriver;
import mcjty.lib.gui.ManualEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ToolType;

import java.util.function.BiFunction;

public class BlockBuilder {

    public static final BlockBehaviour.Properties STANDARD_IRON = BlockBehaviour.Properties.of(Material.METAL)
            .strength(2.0f)
            .sound(SoundType.METAL);

    private BlockBehaviour.Properties properties = STANDARD_IRON;
    private boolean infusable = false;
    private TooltipBuilder tooltipBuilder = new TooltipBuilder();
    private BiFunction<BlockPos, BlockState, BlockEntity> tileEntitySupplier;
    private ToolType toolType = ToolType.PICKAXE;
    private int harvestLevel = 0;
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

    public BiFunction<BlockPos, BlockState, BlockEntity> getTileEntitySupplier() {
        return tileEntitySupplier;
    }

    public ToolType getToolType() {
        return toolType;
    }

    public int getHarvestLevel() {
        return harvestLevel;
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

    public BlockBuilder tileEntitySupplier(BiFunction<BlockPos, BlockState, BlockEntity> supplier) {
        this.tileEntitySupplier = supplier;
        return this;
    }

    public BlockBuilder harvestLevel(ToolType type, int level) {
        this.toolType = type;
        this.harvestLevel = level;
        return this;
    }
}
