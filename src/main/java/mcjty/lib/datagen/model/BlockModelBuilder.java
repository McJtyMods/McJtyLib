package mcjty.lib.datagen.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

/** JSON model builder shared by the block and item data providers. */
public class BlockModelBuilder extends ModelFile {

    private final JsonObject json = new JsonObject();

    public BlockModelBuilder(Identifier location) {
        super(location);
    }

    public JsonObject toJson() {
        return json;
    }

    public BlockModelBuilder parent(ModelFile model) {
        json.addProperty("parent", model.getLocation().toString());
        return this;
    }

    public BlockModelBuilder texture(String key, Identifier value) {
        return texture(key, value.toString());
    }

    public BlockModelBuilder texture(String key, String value) {
        if (!json.has("textures")) {
            json.add("textures", new JsonObject());
        }
        json.getAsJsonObject("textures").addProperty(key, value.startsWith("#") || value.contains(":")
                ? value : Identifier.fromNamespaceAndPath(getLocation().getNamespace(), value).toString());
        return this;
    }

    public <T> T customLoader(Function<BlockModelBuilder, T> factory) {
        return factory.apply(this);
    }

    public ElementBuilder element() {
        if (!json.has("elements")) {
            json.add("elements", new JsonArray());
        }
        JsonObject element = new JsonObject();
        json.getAsJsonArray("elements").add(element);
        return new ElementBuilder(element);
    }

    public ElementBuilder element(int index) {
        return new ElementBuilder(json.getAsJsonArray("elements").get(index).getAsJsonObject());
    }

    private static JsonArray numbers(float... values) {
        JsonArray array = new JsonArray();
        for (float value : values) {
            array.add(value);
        }
        return array;
    }

    public enum FaceRotation {
        ZERO(0), CLOCKWISE_90(90), UPSIDE_DOWN(180), COUNTERCLOCKWISE_90(270);

        private final int angle;

        FaceRotation(int angle) {
            this.angle = angle;
        }
    }

    public class ElementBuilder {

        private final JsonObject element;

        private ElementBuilder(JsonObject element) {
            this.element = element;
        }

        public RotationBuilder rotation() {
            if (!element.has("rotation")) {
                element.add("rotation", new JsonObject());
            }
            return new RotationBuilder(element.getAsJsonObject("rotation"));
        }

        public ElementBuilder from(float x, float y, float z) {
            element.add("from", numbers(x, y, z));
            return this;
        }

        public ElementBuilder to(float x, float y, float z) {
            element.add("to", numbers(x, y, z));
            return this;
        }

        public FaceBuilder face(Direction side) {
            if (!element.has("faces")) {
                element.add("faces", new JsonObject());
            }
            JsonObject faces = element.getAsJsonObject("faces");
            if (!faces.has(side.getSerializedName())) {
                faces.add(side.getSerializedName(), new JsonObject());
            }
            return new FaceBuilder(faces.getAsJsonObject(side.getSerializedName()));
        }

        public ElementBuilder allFaces(BiConsumer<Direction, FaceBuilder> consumer) {
            for (Direction side : Direction.values()) {
                consumer.accept(side, face(side));
            }
            return this;
        }

        public BlockModelBuilder end() {
            return BlockModelBuilder.this;
        }

        public class RotationBuilder {

            private final JsonObject rotation;

            private RotationBuilder(JsonObject rotation) {
                this.rotation = rotation;
            }

            public RotationBuilder origin(float x, float y, float z) {
                rotation.add("origin", numbers(x, y, z));
                return this;
            }

            public RotationBuilder axis(Direction.Axis axis) {
                rotation.addProperty("axis", axis.getSerializedName());
                return this;
            }

            public RotationBuilder angle(float angle) {
                rotation.addProperty("angle", angle);
                return this;
            }

            public ElementBuilder end() {
                return ElementBuilder.this;
            }
        }

        public class FaceBuilder {

            private final JsonObject face;

            private FaceBuilder(JsonObject face) {
                this.face = face;
            }

            public FaceBuilder texture(String texture) {
                face.addProperty("texture", texture);
                return this;
            }

            public FaceBuilder cullface(Direction direction) {
                face.addProperty("cullface", direction.getSerializedName());
                return this;
            }

            public FaceBuilder uvs(float minU, float minV, float maxU, float maxV) {
                face.add("uv", numbers(minU, minV, maxU, maxV));
                return this;
            }

            public FaceBuilder rotation(FaceRotation rotation) {
                face.addProperty("rotation", rotation.angle);
                return this;
            }

            public ElementBuilder end() {
                return ElementBuilder.this;
            }
        }
    }
}
