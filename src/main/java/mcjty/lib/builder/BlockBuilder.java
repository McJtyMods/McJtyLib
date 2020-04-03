package mcjty.lib.builder;

import mcjty.lib.compat.theoneprobe.McJtyLibTOPDriver;
import mcjty.lib.compat.theoneprobe.TOPDriver;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ToolType;

import java.util.function.Supplier;

public class BlockBuilder {

    public static final Block.Properties STANDARD_IRON = Block.Properties.create(Material.IRON)
            .hardnessAndResistance(2.0f)
            .sound(SoundType.METAL);

    private Block.Properties properties = STANDARD_IRON;
    private boolean infusable = false;
    private TooltipBuilder tooltipBuilder = new TooltipBuilder();
    private Supplier<TileEntity> tileEntitySupplier;
    private ToolType toolType = ToolType.PICKAXE;
    private int harvestLevel = 0;
    private TOPDriver topDriver = McJtyLibTOPDriver.DRIVER;

    public Block.Properties getProperties() {
        return properties;
    }

    public boolean isInfusable() {
        return infusable;
    }

    public TooltipBuilder getTooltipBuilder() {
        return tooltipBuilder;
    }

    public Supplier<TileEntity> getTileEntitySupplier() {
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

    public BlockBuilder properties(Block.Properties properties) {
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

    public BlockBuilder tileEntitySupplier(Supplier<TileEntity> supplier) {
        this.tileEntitySupplier = supplier;
        return this;
    }

    public BlockBuilder harvestLevel(ToolType type, int level) {
        this.toolType = type;
        this.harvestLevel = level;
        return this;
    }
}
