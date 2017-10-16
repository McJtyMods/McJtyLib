package mcjty.lib.integration.computers;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import li.cil.oc.api.prefab.ManagedEnvironment;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractOCDriver extends DriverSidedTileEntity {
    String componentName;
    Class<? extends TileEntity> clazz;

    public AbstractOCDriver(String componentName, Class<? extends TileEntity> clazz) {
        this.componentName = componentName;
        this.clazz = clazz;
    }

    public abstract static class InternalManagedEnvironment<T> extends ManagedEnvironment implements NamedBlock {
        protected T tile;
        private String componentName;

        public InternalManagedEnvironment(T tile, String name) {
            this.tile = tile;
            this.componentName = name;
            this.setNode(Network.newNode(this, Visibility.Network).withComponent(componentName, Visibility.Network).create());
        }

        @Override
        public String preferredName() {
            return componentName;
        }

        @Override
        public int priority() {
            return 0;
        }
    }

    @Override
    public Class<?> getTileEntityClass() {
        return clazz;
    }

    @Override
    public boolean worksWith(World world, BlockPos pos, EnumFacing side) {
        return clazz.isInstance(world.getTileEntity(pos));
    }

    @Override
    public ManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side) {
        TileEntity tile = world.getTileEntity(pos);
        if (clazz.isInstance(tile)) {
            return this.createEnvironment(world, pos, side, tile);
        }
        return null;
    }

    public abstract ManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side, TileEntity tile);
}
