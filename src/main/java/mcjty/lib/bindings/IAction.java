package mcjty.lib.bindings;

import mcjty.lib.tileentity.GenericTileEntity;

import java.util.function.Consumer;

public interface IAction<T extends GenericTileEntity> {

    String getKey();

    Consumer<T> consumer();
}
