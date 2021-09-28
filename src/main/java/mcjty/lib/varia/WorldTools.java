package mcjty.lib.varia;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.stream.Stream;

public class WorldTools {

    public static boolean isLoaded(World world, BlockPos pos) {
        if (world == null || pos == null) {
            return false;
        }
        return world.hasChunkAt(pos);
    }

    public static ServerWorld getOverworld() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server.getLevel(World.OVERWORLD);
    }

    public static ServerWorld getOverworld(World world) {
        MinecraftServer server = world.getServer();
        return server.getLevel(World.OVERWORLD);
    }

    public static ServerWorld loadWorld(DimensionId type) {
        ServerWorld world = getWorld(type);
        if (world == null) {
            // Worlds in 1.16 are always loaded
            return type.loadWorld();
        }
        return world;
    }

    public static ServerWorld getWorld(DimensionId type) {
        return type.getWorld();
    }

    public static ServerWorld getWorld(World world, DimensionId type) {
        // Worlds in 1.16 are always loaded
        return type.loadWorld(world);
    }

    /**
     * Gets all players who have the chunk in which the provided (x,z) coordinates are located loaded
     */
    public static Stream<ServerPlayerEntity> getAllPlayersWatchingBlock(World world, BlockPos pos) {
        if (world instanceof ServerWorld) {
            ChunkHolder.IPlayerProvider playerManager = ((ServerWorld)world).getChunkSource().chunkMap;
            return playerManager.getPlayers(new ChunkPos(pos), false);
        }
        return Stream.empty();
    }
}
