package mcjty.lib.varia;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class WorldTools {

    public static boolean isLoaded(World world, BlockPos pos) {
        if (world == null || pos == null) {
            return false;
        }
        return world.isBlockLoaded(pos);
    }

    public static ServerWorld getOverworld() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server.getWorld(World.OVERWORLD);
    }

    public static ServerWorld getOverworld(World world) {
        MinecraftServer server = world.getServer();
        return server.getWorld(World.OVERWORLD);
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
}
