package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.debugtools.DumpBlockNBT;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.LevelTools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.ServerOpList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Debug packet to dump block info
 */
public record PacketDumpBlockInfo(ResourceKey<Level> dimid, BlockPos pos, Boolean verbose) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(McJtyLib.MODID, "dumpblockinfo");

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(dimid.location());
        buf.writeBlockPos(pos);
        buf.writeBoolean(verbose);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static PacketDumpBlockInfo create(FriendlyByteBuf buf) {
        ResourceKey<Level> dimid = LevelTools.getId(buf.readResourceLocation());
        BlockPos pos = buf.readBlockPos();
        boolean verbose = buf.readBoolean();
        return new PacketDumpBlockInfo(dimid, pos, verbose);
    }

    public static PacketDumpBlockInfo create(Level world, BlockPos pos, boolean verbose) {
        return new PacketDumpBlockInfo(world.dimension(), pos, verbose);
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ctx.player().ifPresent(player -> {
                MinecraftServer server = player.getCommandSenderWorld().getServer();
                ServerOpList oppedPlayers = server.getPlayerList().getOps();
                ServerOpListEntry entry = oppedPlayers.get(player.getGameProfile());
                int perm = entry == null ? server.getOperatorUserPermissionLevel() : entry.getLevel();
                if (perm >= 1) {
                    Level world = LevelTools.getLevel(player.level(), dimid);
                    String output = DumpBlockNBT.dumpBlockNBT(world, pos, verbose);
                    Logging.getLogger().log(org.apache.logging.log4j.Level.INFO, "### Server side ###");
                    Logging.getLogger().log(org.apache.logging.log4j.Level.INFO, output);
                }
            });
        });
    }
}
