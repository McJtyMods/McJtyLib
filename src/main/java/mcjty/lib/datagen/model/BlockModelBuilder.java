package mcjty.lib.datagen.model;

import com.google.gson.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import java.util.function.BiConsumer;

/** JSON model builder shared by the block and item data providers. */
public class BlockModelBuilder extends ModelFile {
    private final JsonObject json = new JsonObject();
    public BlockModelBuilder(Identifier location) { super(location); }
    public JsonObject toJson() { return json; }
    public BlockModelBuilder parent(ModelFile model) { json.addProperty("parent", model.getLocation().toString()); return this; }
    public BlockModelBuilder texture(String key, Identifier value) { return texture(key, value.toString()); }
    public BlockModelBuilder texture(String key, String value) {
        if (!json.has("textures")) json.add("textures", new JsonObject());
        json.getAsJsonObject("textures").addProperty(key, value); return this;
    }
    public ElementBuilder element() {
        if (!json.has("elements")) json.add("elements", new JsonArray());
        var element = new JsonObject(); json.getAsJsonArray("elements").add(element); return new ElementBuilder(element);
    }
    private static JsonArray numbers(float... values) { var a = new JsonArray(); for (float v : values) a.add(v); return a; }
    public enum FaceRotation { ZERO(0), CLOCKWISE_90(90), UPSIDE_DOWN(180), COUNTERCLOCKWISE_90(270); final int angle; FaceRotation(int a) { angle = a; } }
    public class ElementBuilder {
        final JsonObject element;
        ElementBuilder(JsonObject e) { element = e; }
        public ElementBuilder from(float x, float y, float z) { element.add("from", numbers(x,y,z)); return this; }
        public ElementBuilder to(float x, float y, float z) { element.add("to", numbers(x,y,z)); return this; }
        public FaceBuilder face(Direction side) {
            if (!element.has("faces")) element.add("faces", new JsonObject());
            var face = new JsonObject(); element.getAsJsonObject("faces").add(side.getSerializedName(), face); return new FaceBuilder(face);
        }
        public ElementBuilder allFaces(BiConsumer<Direction, FaceBuilder> consumer) { for (var side : Direction.values()) consumer.accept(side, face(side)); return this; }
        public BlockModelBuilder end() { return BlockModelBuilder.this; }
        public class FaceBuilder {
            final JsonObject face;
            FaceBuilder(JsonObject f) { face=f; }
            public FaceBuilder texture(String texture) { face.addProperty("texture", texture); return this; }
            public FaceBuilder cullface(Direction direction) { face.addProperty("cullface", direction.getSerializedName()); return this; }
            public FaceBuilder uvs(float a,float b,float c,float d) { face.add("uv",numbers(a,b,c,d)); return this; }
            public FaceBuilder rotation(FaceRotation rotation) { face.addProperty("rotation",rotation.angle); return this; }
            public ElementBuilder end() { return ElementBuilder.this; }
        }
    }
}
