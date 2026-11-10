package mcjty.lib.client;

import java.util.HashMap;
import java.util.Map;

public final class ModelData {

    public static final ModelData EMPTY = new ModelData(Map.of());
    private final Map<ModelProperty<?>, Object> values;

    private ModelData(Map<ModelProperty<?>, Object> values) {
        this.values = Map.copyOf(values);
    }

    public <T> T get(ModelProperty<T> property) {
        return (T) values.get(property);
    }

    public Builder derive() {
        return new Builder(values);
    }

    public static Builder builder() {
        return new Builder(Map.of());
    }

    public static final class Builder {

        private final Map<ModelProperty<?>, Object> values;

        private Builder(Map<ModelProperty<?>, Object> values) {
            this.values = new HashMap<>(values);
        }

        public <T> Builder with(ModelProperty<T> property, T value) {
            if (value == null) values.remove(property);
            else values.put(property, value);
            return this;
        }

        public ModelData build() {
            return new ModelData(values);
        }
    }
}
