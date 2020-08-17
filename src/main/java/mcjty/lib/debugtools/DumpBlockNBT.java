package mcjty.lib.debugtools;

import com.google.gson.*;
import mcjty.lib.network.PacketDumpBlockInfo;
import mcjty.lib.varia.Logging;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.simple.SimpleChannel;
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
        if (te != null) {
            jsonObject.add("teClass", new JsonPrimitive(te.getClass().getCanonicalName()));
            CompoundNBT tag = new CompoundNBT();
            te.write(tag);
            if (verbose) {
                String nbtJson = tag.toString();
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(nbtJson);
                jsonObject.add("nbt", element);
            } else {
                JsonArray array = new JsonArray();
                for (String key : tag.keySet()) {
                    array.add(new JsonPrimitive(key));
                }
                jsonObject.add("nbt", array);
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
    }

    // Use client-side
    public static void dumpBlock(@Nullable SimpleChannel network, @Nonnull World world, @Nonnull BlockPos pos, boolean verbose) {
        String output = DumpBlockNBT.dumpBlockNBT(world, pos, verbose);
        Logging.getLogger().log(Level.INFO, "### Client side ###");
        Logging.getLogger().log(Level.INFO, output);
        if (network != null) {
            network.sendToServer(new PacketDumpBlockInfo(world, pos, verbose));
        }
    }

    // Use client-side
    public static void dumpFocusedBlock(@Nullable SimpleChannel network, @Nonnull PlayerEntity player, boolean liquids, boolean verbose) {
        Vector3d start = player.getEyePosition(1.0f);
        Vector3d vec31 = player.getLook(1.0f);
        float dist = 20;
        Vector3d end = start.add(vec31.x * dist, vec31.y * dist, vec31.z * dist);
        RayTraceContext context = new RayTraceContext(start, end, RayTraceContext.BlockMode.COLLIDER, liquids ? RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE, player);
        RayTraceResult result = player.getEntityWorld().rayTraceBlocks(context);
        if (result == null || result.getType() != RayTraceResult.Type.BLOCK) {
            return;
        }

        String output = DumpBlockNBT.dumpBlockNBT(player.getEntityWorld(), ((BlockRayTraceResult) result).getPos(), verbose);
        Logging.getLogger().log(Level.INFO, "### Client side ###");
        Logging.getLogger().log(Level.INFO, output);
        if (network != null) {
            network.sendToServer(new PacketDumpBlockInfo(player.getEntityWorld(), ((BlockRayTraceResult) result).getPos(), verbose));
        }
    }
}
