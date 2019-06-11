package mcjty.lib.debugtools;

import com.google.gson.*;
import mcjty.lib.network.PacketDumpBlockInfo;
import mcjty.lib.varia.Logging;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DumpBlockNBT {

    public static String dumpBlockNBT(@Nonnull World world, @Nonnull BlockPos pos, boolean verbose) {
        BlockState state = world.getBlockState(pos);
        TileEntity te = world.getTileEntity(pos);
        Block block = state.getBlock();

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("block", new JsonPrimitive(block.getRegistryName().toString()));
        jsonObject.add("meta", new JsonPrimitive(block.getMetaFromState(state)));
        if (te != null) {
            jsonObject.add("teClass", new JsonPrimitive(te.getClass().getCanonicalName()));
            CompoundNBT tag = new CompoundNBT();
            te.writeToNBT(tag);
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
    public static void dumpBlock(@Nullable SimpleNetworkWrapper network, @Nonnull World world, @Nonnull BlockPos pos, boolean verbose) {
        String output = DumpBlockNBT.dumpBlockNBT(world, pos, verbose);
        Logging.getLogger().log(Level.INFO, "### Client side ###");
        Logging.getLogger().log(Level.INFO, output);
        if (network != null) {
            network.sendToServer(new PacketDumpBlockInfo(world, pos, verbose));
        }
    }

    // Use client-side
    public static void dumpFocusedBlock(@Nullable SimpleNetworkWrapper network, @Nonnull PlayerEntity player, boolean liquids, boolean verbose) {
        Vec3d start = player.getPositionEyes(1.0f);
        Vec3d vec31 = player.getLook(1.0f);
        float dist = 20;
        Vec3d end = start.addVector(vec31.x * dist, vec31.y * dist, vec31.z * dist);
        RayTraceResult result = player.getEntityWorld().rayTraceBlocks(start, end, liquids);
        if (result == null || result.typeOfHit != RayTraceResult.Type.BLOCK) {
            return;
        }

        String output = DumpBlockNBT.dumpBlockNBT(player.getEntityWorld(), result.getBlockPos(), verbose);
        Logging.getLogger().log(Level.INFO, "### Client side ###");
        Logging.getLogger().log(Level.INFO, output);
        if (network != null) {
            network.sendToServer(new PacketDumpBlockInfo(player.getEntityWorld(), result.getBlockPos(), verbose));
        }
    }
}
