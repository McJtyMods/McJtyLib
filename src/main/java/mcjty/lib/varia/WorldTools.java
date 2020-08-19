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
        return server.getWorld(World.field_234918_g_);
    }

    public static ServerWorld getOverworld(World world) {
        MinecraftServer server = world.getServer();
        return server.getWorld(World.field_234918_g_);
    }

    public static ServerWorld loadWorld(DimensionId type) {
        ServerWorld world = getWorld(type);
        if (world == null) {
//            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            // @todo 1.16 somehow load the world?
            return type.loadWorld();
        }
        return world;
    }

    public static ServerWorld getWorld(DimensionId type) {
        return type.loadWorld();
    }

    public static ServerWorld getWorld(World world, DimensionId type) {
        // @todo 1.16 load?
        return type.loadWorld(world);
//        MinecraftServer server = world.getServer();
//        return DimensionManager.getWorld(server, type, false, false);
    }
}
