package mcjty.lib.entity;

import java.util.function.Consumer;

public interface IAction<T extends GenericTileEntity> {

    String getKey();

    Consumer<T> consumer();
}
