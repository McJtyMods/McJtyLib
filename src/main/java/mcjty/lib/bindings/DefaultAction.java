package mcjty.lib.bindings;

import mcjty.lib.tileentity.GenericTileEntity;

import java.util.function.Consumer;

public class DefaultAction<T extends GenericTileEntity> implements IAction<T> {

    private final String key;
    private final Consumer<T> consumer;

    public DefaultAction(String key, Consumer<T> consumer) {
        this.key = key;
        this.consumer = consumer;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Consumer<T> consumer() {
        return consumer;
    }
}
