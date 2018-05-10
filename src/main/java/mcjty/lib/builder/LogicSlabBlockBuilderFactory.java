package mcjty.lib.builder;

import mcjty.lib.base.ModBase;
import mcjty.lib.tileentity.LogicTileEntity;
import net.minecraft.creativetab.CreativeTabs;

public class LogicSlabBlockBuilderFactory {

    private final ModBase mod;
    private CreativeTabs creativeTabs;

    public LogicSlabBlockBuilderFactory(ModBase mod) {
        this.mod = mod;
    }

    public LogicSlabBlockBuilderFactory creativeTabs(CreativeTabs creativeTabs) {
        this.creativeTabs = creativeTabs;
        return this;
    }

    public <T extends LogicTileEntity> LogicSlabBlockBuilder<T> builder(String registryName) {
        return new LogicSlabBlockBuilder<T>(mod, registryName)
                .creativeTabs(creativeTabs);
    }
}
