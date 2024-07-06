package mcjty.lib.varia;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class JSonTools {

    public static Optional<JsonElement> getElement(JsonObject element, String name) {
        JsonElement el = element.get(name);
        if (el != null) {
            return Optional.of(el);
        } else {
            return Optional.empty();
        }
    }

    public static int get(JsonObject object, String name, int def) {
        if (object.has(name)) {
            return object.get(name).getAsInt();
        } else {
            return def;
        }
    }

    public static boolean get(JsonObject object, String name, boolean def) {
        if (object.has(name)) {
            return object.get(name).getAsBoolean();
        } else {
            return def;
        }
    }

    public static String get(JsonObject object, String name, String def) {
        if (object.has(name)) {
            return object.get(name).getAsString();
        } else {
            return def;
        }
    }

    public static void put(JsonObject object, String name, Integer value, Integer def) {
        if (value == null) {
            return;
        }
        if (value.equals(def)) {
            return;
        }
        object.add(name, new JsonPrimitive(value));
    }

    public static void put(JsonObject object, String name, Boolean value, Boolean def) {
        if (value == null) {
            return;
        }
        if (value.equals(def)) {
            return;
        }
        object.add(name, new JsonPrimitive(value));
    }

    public static void put(JsonObject object, String name, String value, String def) {
        if (value == null) {
            return;
        }
        if (value.equals(def)) {
            return;
        }
        object.add(name, new JsonPrimitive(value));
    }

    public static void putStringList(JsonObject object, String name, @Nullable List<String> list) {
        if (list == null) {
            return;
        }
        if (list.size() == 1) {
            object.add(name, new JsonPrimitive(list.get(0)));
        } else {
            JsonArray array = new JsonArray();
            for (String s : list) {
                array.add(new JsonPrimitive(s));
            }
            object.add(name, array);
        }
    }

    @Nullable
    public static List<String> getStringList(JsonObject object, String name) {
        if (!object.has(name)) {
            return null;
        }
        if (object.get(name).isJsonArray()) {
            JsonArray array = object.getAsJsonArray(name);
            List<String> result = new ArrayList<>();
            for (JsonElement element : array) {
                result.add(element.getAsString());
            }
            return result;
        } else {
            return Collections.singletonList(object.get(name).getAsString());
        }
    }

    public static JsonElement itemStackToJson(ItemStack item) {
        return ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, item).result().orElseThrow(RuntimeException::new);
    }

    public static ItemStack jsonToItemStack(JsonObject obj) {
        return ItemStack.CODEC.parse(JsonOps.INSTANCE, obj).result().orElseThrow(RuntimeException::new);
    }
}
