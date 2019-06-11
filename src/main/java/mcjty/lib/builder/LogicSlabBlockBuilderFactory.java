package mcjty.lib.builder;

import mcjty.lib.base.ModBase;
import mcjty.lib.tileentity.LogicTileEntity;
import net.minecraft.item.ItemGroup;

public class LogicSlabBlockBuilderFactory {

    private final ModBase mod;
    private ItemGroup creativeTabs;

    public LogicSlabBlockBuilderFactory(ModBase mod) {
        this.mod = mod;
    }

    public LogicSlabBlockBuilderFactory creativeTabs(ItemGroup creativeTabs) {
        this.creativeTabs = creativeTabs;
        return this;
    }

    public <T extends LogicTileEntity> LogicSlabBlockBuilder<T> builder(String registryName) {
        return new LogicSlabBlockBuilder<T>(mod, registryName)
                .creativeTabs(creativeTabs);
    }
}
