package mcjty.lib.varia;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JSonTools {

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
        object.add("item", new JsonPrimitive(item.getItem().getRegistryName().toString()));
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
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
        // @todo error checking
        int amount = 1;
        if (obj.has("amount")) {
            amount = obj.get("amount").getAsInt();
        }
        ItemStack stack = new ItemStack(item, amount);
        if (obj.has("nbt")) {
            try {
                CompoundNBT nbt = JsonToNBT.getTagFromJson(obj.get("nbt").getAsString());
                stack.setTag(nbt);
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
                // @todo
            }
        }
        return stack;
    }
}
