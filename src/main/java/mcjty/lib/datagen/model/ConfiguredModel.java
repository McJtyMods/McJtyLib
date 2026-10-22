package mcjty.lib.datagen.model;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

public record ConfiguredModel(ModelFile model, int x, int y) {
    public static Builder<Void> builder() { return new Builder<>(); }

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
        private int x, y;

        public Builder<T> modelFile(ModelFile model) { this.model = model; return this; }
        public Builder<T> rotationX(int x) { this.x = x; return this; }
        public Builder<T> rotationY(int y) { this.y = y; return this; }

        public ConfiguredModel[] build() {
            var result = new ArrayList<>(models);
            if (model != null) result.add(new ConfiguredModel(model, x, y));
            if (result.isEmpty()) throw new IllegalStateException("A variant requires a model");
            return result.toArray(ConfiguredModel[]::new);
        }

        public Builder<T> addModel() {
            if (model == null) throw new IllegalStateException("A variant requires a model");
            models.add(new ConfiguredModel(model, x, y));
            model = null;
            x = y = 0;
            return this;
        }
    }
}
