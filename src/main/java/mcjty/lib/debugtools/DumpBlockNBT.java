package mcjty.lib.debugtools;

import com.google.gson.*;
import mcjty.lib.network.PacketDumpBlockInfo;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.Tools;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DumpBlockNBT {

    public static String dumpBlockNBT(@Nonnull Level world, @Nonnull BlockPos pos, boolean verbose) {
        BlockState state = world.getBlockState(pos);
        BlockEntity te = world.getBlockEntity(pos);
        Block block = state.getBlock();

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("block", new JsonPrimitive(Tools.getId(block).toString()));
        if (te != null) {
            jsonObject.add("teClass", new JsonPrimitive(te.getClass().getCanonicalName()));
            CompoundTag tag = te.saveWithoutMetadata();
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

    // Use client-side
    public static void dumpBlock(@Nullable SimpleChannel network, @Nonnull Level world, @Nonnull BlockPos pos, boolean verbose) {
        String output = DumpBlockNBT.dumpBlockNBT(world, pos, verbose);
        Logging.getLogger().log(org.apache.logging.log4j.Level.INFO, "### Client side ###");
        Logging.getLogger().log(org.apache.logging.log4j.Level.INFO, output);
        if (network != null) {
            network.sendToServer(PacketDumpBlockInfo.create(world, pos, verbose));
        }
    }

    // Use client-side
    public static void dumpFocusedBlock(@Nullable SimpleChannel network, @Nonnull Player player, boolean liquids, boolean verbose) {
        Vec3 start = player.getEyePosition(1.0f);
        Vec3 vec31 = player.getViewVector(1.0f);
        float dist = 20;
        Vec3 end = start.add(vec31.x * dist, vec31.y * dist, vec31.z * dist);
        ClipContext context = new ClipContext(start, end, ClipContext.Block.COLLIDER, liquids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, player);
        HitResult result = player.getCommandSenderWorld().clip(context);
        if (result.getType() != HitResult.Type.BLOCK) {
            return;
        }

        String output = DumpBlockNBT.dumpBlockNBT(player.getCommandSenderWorld(), ((BlockHitResult) result).getBlockPos(), verbose);
        Logging.getLogger().log(org.apache.logging.log4j.Level.INFO, "### Client side ###");
        Logging.getLogger().log(org.apache.logging.log4j.Level.INFO, output);
        if (network != null) {
            network.sendToServer(PacketDumpBlockInfo.create(player.getCommandSenderWorld(), ((BlockHitResult) result).getBlockPos(), verbose));
        }
    }
}
