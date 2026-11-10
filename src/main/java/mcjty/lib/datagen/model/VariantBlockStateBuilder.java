package mcjty.lib.datagen.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class VariantBlockStateBuilder {

    private final Block block;
    private final JsonObject variants = new JsonObject();

    public VariantBlockStateBuilder(Block block) {
        this.block = block;
    }

    public VariantBlockStateBuilder forAllStates(Function<BlockState, ConfiguredModel[]> factory) {
        for (BlockState state : block.getStateDefinition().getPossibleStates()) {
            String key =
                    state.getValues().entrySet().stream()
                            .map(
                                    entry ->
                                            entry.getKey().getName()
                                                    + "="
                                                    + name(entry.getKey(), entry.getValue()))
                            .collect(Collectors.joining(","));
            addModels(key, factory.apply(state));
        }
        return this;
    }

    private static <T extends Comparable<T>> String name(
            Property<T> property, Comparable<?> value) {
        return property.getName((T) value);
    }

    private void addModels(String key, ConfiguredModel[] models) {
        if (models.length == 1) {
            variants.add(key, models[0].toJson());
        } else {
            JsonArray array = new JsonArray();
            for (ConfiguredModel model : models) {
                array.add(model.toJson());
            }
            variants.add(key, array);
        }
    }

    public Partial partialState() {
        return new Partial();
    }

    public class Partial {
        private final Map<String, String> values = new TreeMap<>();

        public <T extends Comparable<T>> Partial with(Property<T> property, T value) {
            if (!block.getStateDefinition().getProperties().contains(property)
                    || !property.getPossibleValues().contains(value)) {
                throw new IllegalArgumentException(
                        "Invalid property value " + property + "=" + value);
            }
            values.put(property.getName(), property.getName(value));
            return this;
        }

        public ConfiguredModel.Builder<VariantBlockStateBuilder> modelForState() {
            return new ConfiguredModel.Builder<>(VariantBlockStateBuilder.this, this::addModels);
        }

        public VariantBlockStateBuilder addModels(ConfiguredModel... models) {
            String key =
                    values.entrySet().stream()
                            .map(entry -> entry.getKey() + "=" + entry.getValue())
                            .collect(Collectors.joining(","));
            VariantBlockStateBuilder.this.addModels(key, models);
            return VariantBlockStateBuilder.this;
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("variants", variants);
        return json;
    }
}
