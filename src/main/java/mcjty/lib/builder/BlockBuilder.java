package mcjty.lib.builder;

import mcjty.lib.compat.theoneprobe.McJtyLibTOPDriver;
import mcjty.lib.compat.theoneprobe.TOPDriver;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ToolType;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockBuilder {

    public static final Block.Properties STANDARD_IRON = Block.Properties.create(Material.IRON)
            .hardnessAndResistance(2.0f)
            .sound(SoundType.METAL);

    private Block.Properties properties = STANDARD_IRON;
    private boolean hasGui = false;
    private boolean infusable = false;
    private InformationString informationString;
    private InformationString informationStringWithShift;
    private Supplier<TileEntity> tileEntitySupplier;
    private ToolType toolType = ToolType.PICKAXE;
    private int harvestLevel = 0;
    private TOPDriver topDriver = McJtyLibTOPDriver.DRIVER;

    public Block.Properties getProperties() {
        return properties;
    }

    public boolean isHasGui() {
        return hasGui;
    }

    public boolean isInfusable() {
        return infusable;
    }

    public InformationString getInformationString() {
        return informationString;
    }

    public InformationString getInformationStringWithShift() {
        return informationStringWithShift;
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

    public BlockBuilder hasGui() {
        this.hasGui = true;
        return this;
    }

    public BlockBuilder infusable() {
        this.infusable = true;
        return this;
    }

    public BlockBuilder info(String informationString) {
        this.informationString = new InformationString(informationString);
        return this;
    }

    public BlockBuilder infoParameter(Function<ItemStack, String> parameter) {
        this.informationString.addParameter(parameter);
        return this;
    }

    public BlockBuilder infoExtended(String informationString) {
        this.informationStringWithShift = new InformationString(informationString);
        return this;
    }

    public BlockBuilder infoExtendedParameter(Function<ItemStack, String> parameter) {
        this.informationStringWithShift.addParameter(parameter);
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
