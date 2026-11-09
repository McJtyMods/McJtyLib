package mcjty.lib.datagen.model;

import java.util.function.Consumer;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

public record ConfiguredModel(ModelFile model, int x, int y) {

    public ConfiguredModel(ModelFile model) {
        this(model, 0, 0);
    }

    public static Builder<Void> builder() {
        return new Builder<>();
    }

    public JsonObject toJson() {
        var json = new JsonObject();
        json.addProperty("model", model.getLocation().toString());
        if (x != 0) json.addProperty("x", Math.floorMod(x, 360));
        if (y != 0) json.addProperty("y", Math.floorMod(y, 360));
        return json;
    }

    public static class Builder<T> {
        private final List<ConfiguredModel> models = new ArrayList<>();
        private ModelFile model;
        private VariantBlockStateBuilder parent;
        private Consumer<ConfiguredModel[]> onAdd;

        public Builder() {
        }

        Builder(VariantBlockStateBuilder parent, Consumer<ConfiguredModel[]> onAdd) {
            this.parent = parent;
            this.onAdd = onAdd;
        }

        public VariantBlockStateBuilder.Partial partialState() {
            if (parent == null) throw new IllegalStateException("This model has no block state parent");
            return parent.partialState();
        }

        private int x, y;

        public Builder<T> modelFile(ModelFile model) {
            this.model = model;
            return this;
        }

        public Builder<T> rotationX(int x) {
            this.x = x;
            return this;
        }

        public Builder<T> rotationY(int y) {
            this.y = y;
            return this;
        }

        public ConfiguredModel[] build() {
            var result = new ArrayList<>(models);
            if (model != null) result.add(new ConfiguredModel(model, x, y));
            if (result.isEmpty()) throw new IllegalStateException("A variant requires a model");
            return result.toArray(ConfiguredModel[]::new);
        }

        public Builder<T> addModel() {
            if (model == null) throw new IllegalStateException("A variant requires a model");
            models.add(new ConfiguredModel(model, x, y));
            if (onAdd != null) onAdd.accept(models.toArray(ConfiguredModel[]::new));
            model = null;
            x = y = 0;
            return this;
        }
    }
}
