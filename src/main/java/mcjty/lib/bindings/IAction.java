package mcjty.lib.bindings;

public interface IAction {

    String getKey();

    Runnable consumer();
}
