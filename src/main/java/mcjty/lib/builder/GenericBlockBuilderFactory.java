package mcjty.lib.builder;

import mcjty.lib.base.ModBase;
import mcjty.lib.entity.GenericTileEntity;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.Container;

public class GenericBlockBuilderFactory {

    private final ModBase mod;
    private CreativeTabs creativeTabs;

    public GenericBlockBuilderFactory(ModBase mod) {
        this.mod = mod;
    }

    public GenericBlockBuilderFactory creativeTabs(CreativeTabs creativeTabs) {
        this.creativeTabs = creativeTabs;
        return this;
    }

    public <T extends GenericTileEntity, C extends Container> GenericBlockBuilder<T, C> builder(String registryName) {
        return new GenericBlockBuilder<T, C>(mod, registryName)
                .creativeTabs(creativeTabs);
    }
}
