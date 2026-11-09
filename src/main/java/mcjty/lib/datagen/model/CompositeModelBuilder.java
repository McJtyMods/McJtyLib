package mcjty.lib.datagen.model;

import com.google.gson.JsonObject;

/** Builds NeoForge's inline composite geometry format. */
public final class CompositeModelBuilder {

    private final BlockModelBuilder parent;
    private final JsonObject children = new JsonObject();

    private CompositeModelBuilder(BlockModelBuilder parent) {
        this.parent = parent;
        parent.toJson().addProperty("loader", "neoforge:composite");
        parent.toJson().add("children", children);
    }

    public static CompositeModelBuilder begin(BlockModelBuilder parent) {
        return new CompositeModelBuilder(parent);
    }

    public CompositeModelBuilder child(String name, BlockModelBuilder model) {
        children.add(name, model.toJson());
        return this;
    }

    public BlockModelBuilder end() {
        return parent;
    }
}
