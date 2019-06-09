package mcjty.lib.debugtools;

import com.google.gson.*;
import mcjty.lib.network.PacketDumpItemInfo;
import mcjty.lib.varia.Logging;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DumpItemNBT {

    public static String dumpItemNBT(@Nonnull ItemStack item, boolean verbose) {
        if (item.isEmpty()) {
            return "<null>";
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("item", new JsonPrimitive(item.getItem().getRegistryName().toString()));
        jsonObject.add("meta", new JsonPrimitive(item.getItemDamage()));
        if (item.hasTagCompound()) {
            NBTTagCompound tag = item.getTagCompound();
            if (verbose) {
                String nbtJson = tag.toString();
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(nbtJson);
                jsonObject.add("nbt", element);
            } else {
                JsonArray array = new JsonArray();
                for (String key : tag.getKeySet()) {
                    array.add(new JsonPrimitive(key));
                }
                jsonObject.add("nbt", array);
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
    }

    // Use client-side
    public static void dumpHeldItem(@Nullable SimpleNetworkWrapper network, @Nonnull PlayerEntity player, boolean verbose) {
        ItemStack item = player.getHeldItemMainhand();
        if (item.isEmpty()) {
            return;
        }
        String output = DumpItemNBT.dumpItemNBT(item, verbose);
        Logging.getLogger().log(Level.INFO, "### Client side ###");
        Logging.getLogger().log(Level.INFO, output);
        if (network != null) {
            network.sendToServer(new PacketDumpItemInfo(verbose));
        }
    }
}
