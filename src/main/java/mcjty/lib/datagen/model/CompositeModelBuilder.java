package mcjty.lib.datagen.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;

/** Combines authored child models into native vanilla geometry with separate texture bindings. */
public final class CompositeModelBuilder {

    private final BlockModelBuilder parent;
    private final Map<String, BlockModelBuilder> children = new LinkedHashMap<>();

    private CompositeModelBuilder(BlockModelBuilder parent) {
        this.parent = parent;
    }

    public static CompositeModelBuilder begin(BlockModelBuilder parent) {
        return new CompositeModelBuilder(parent);
    }

    public CompositeModelBuilder child(String name, BlockModelBuilder model) {
        children.put(name, model);
        return this;
    }

    public BlockModelBuilder end() {
        JsonObject target = parent.toJson();
        JsonArray elements = new JsonArray();
        JsonObject textures =
                target.has("textures") ? target.getAsJsonObject("textures") : new JsonObject();
        children.forEach(
                (name, builder) -> {
                    JsonObject model = builder.resolvedJson();
                    String prefix = name + "_";
                    if (model.has("textures"))
                        model.getAsJsonObject("textures")
                                .entrySet()
                                .forEach(
                                        entry -> {
                                            String value = entry.getValue().getAsString();
                                            textures.addProperty(
                                                    prefix + entry.getKey(),
                                                    value.startsWith("#")
                                                            ? "#" + prefix + value.substring(1)
                                                            : value);
                                        });
                    if (model.has("elements"))
                        model.getAsJsonArray("elements")
                                .forEach(
                                        value -> {
                                            JsonObject element = value.deepCopy().getAsJsonObject();
                                            if (element.has("faces"))
                                                element.getAsJsonObject("faces")
                                                        .entrySet()
                                                        .forEach(
                                                                face -> {
                                                                    JsonObject json =
                                                                            face.getValue()
                                                                                    .getAsJsonObject();
                                                                    String texture =
                                                                            json.get("texture")
                                                                                    .getAsString();
                                                                    if (texture.startsWith("#"))
                                                                        json.addProperty(
                                                                                "texture",
                                                                                "#"
                                                                                        + prefix
                                                                                        + texture
                                                                                                .substring(
                                                                                                        1));
                                                                });
                                            elements.add(element);
                                        });
                });
        target.add("textures", textures);
        target.add("elements", elements);
        return parent;
    }
}
