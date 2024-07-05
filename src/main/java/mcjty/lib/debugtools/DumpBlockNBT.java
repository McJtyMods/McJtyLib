package mcjty.lib.debugtools;

import com.google.gson.*;
import mcjty.lib.varia.Tools;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class DumpBlockNBT {

    public static String dumpBlockNBT(@Nonnull Level world, @Nonnull BlockPos pos, boolean verbose) {
        BlockState state = world.getBlockState(pos);
        BlockEntity te = world.getBlockEntity(pos);
        Block block = state.getBlock();

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("block", new JsonPrimitive(Tools.getId(block).toString()));
        if (te != null) {
            jsonObject.add("teClass", new JsonPrimitive(te.getClass().getCanonicalName()));
            CompoundTag tag = te.saveWithoutMetadata(world.registryAccess());
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
