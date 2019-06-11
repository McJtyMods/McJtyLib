package mcjty.lib.integration.computers;

public abstract class AbstractOCDriver {} /* @todo 1.14 extends DriverSidedTileEntity {
    String componentName;
    Class<? extends TileEntity> clazz;

    public AbstractOCDriver(String componentName, Class<? extends TileEntity> clazz) {
        this.componentName = componentName;
        this.clazz = clazz;
    }

    public abstract static class InternalManagedEnvironment<T> extends AbstractManagedEnvironment implements NamedBlock {
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
    public boolean worksWith(World world, BlockPos pos, Direction side) {
        return clazz.isInstance(world.getTileEntity(pos));
    }

    @Override
    public AbstractManagedEnvironment createEnvironment(World world, BlockPos pos, Direction side) {
        TileEntity tile = world.getTileEntity(pos);
        if (clazz.isInstance(tile)) {
            return this.createEnvironment(world, pos, side, tile);
        }
        return null;
    }

    public abstract AbstractManagedEnvironment createEnvironment(World world, BlockPos pos, Direction side, TileEntity tile);
}
*/