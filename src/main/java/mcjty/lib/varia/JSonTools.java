package mcjty.lib.varia;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
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

    public static JsonObject itemStackToJson(ItemStack item) {
        JsonObject object = new JsonObject();
        object.add("item", new JsonPrimitive(Tools.getId(item).toString()));
        if (item.getCount() != 1) {
            object.add("amount", new JsonPrimitive(item.getCount()));
        }
        if (item.hasTag()) {
            String string = item.getTag().toString();
            object.add("nbt", new JsonPrimitive(string));
        }
        return object;
    }

    public static ItemStack jsonToItemStack(JsonObject obj) {
        String itemName = obj.get("item").getAsString();
        Item item = Tools.getItem(ResourceLocation.parse(itemName));
        // @todo error checking
        int amount = 1;
        if (obj.has("amount")) {
            amount = obj.get("amount").getAsInt();
        }
        ItemStack stack = new ItemStack(item, amount);
        if (obj.has("nbt")) {
            try {
                CompoundTag nbt = TagParser.parseTag(obj.get("nbt").getAsString());
                stack.setTag(nbt);
            } catch (CommandSyntaxException e) {
                Logging.logError("Parsing error", e);
            }
        }
        return stack;
    }
}
