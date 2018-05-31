package mcjty.lib.bindings;

public class DefaultAction implements IAction {

    private final String key;
    private final Runnable consumer;

    public DefaultAction(String key, Runnable consumer) {
        this.key = key;
        this.consumer = consumer;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Runnable consumer() {
        return consumer;
    }
}
