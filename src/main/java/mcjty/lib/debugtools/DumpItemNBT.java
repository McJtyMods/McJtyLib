package mcjty.lib.debugtools;

import com.google.gson.*;
import mcjty.lib.varia.Tools;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class DumpItemNBT {

    public static String dumpItemNBT(@Nonnull ItemStack item, boolean verbose) {
        if (item.isEmpty()) {
            return "<null>";
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("item", new JsonPrimitive(Tools.getId(item).toString()));
        if (item.hasTag()) {
            CompoundTag tag = item.getTag();
            if (verbose) {
                String nbtJson = tag.toString();
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(nbtJson);
                jsonObject.add("nbt", element);
            } else {
                JsonArray array = new JsonArray();
                for (String key : tag.getAllKeys()) {
                    array.add(new JsonPrimitive(key));
                }
                jsonObject.add("nbt", array);
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
    }
}
