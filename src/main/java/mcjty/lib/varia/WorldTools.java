package mcjty.lib.varia;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class WorldTools {

    public static boolean isLoaded(World world, BlockPos pos) {
        if (world == null || pos == null) {
            return false;
        }
        return world.isBlockLoaded(pos);
    }

    public static String getDimensionName(World world) {
        return getDimensionName(world.getDimension().getType());
    }

    public static String getDimensionName(DimensionType type) {
        return type.getRegistryName().toString();
    }

    public static ServerWorld getOverworld() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return DimensionManager.getWorld(server, DimensionType.OVERWORLD, false, false);
    }

    public static ServerWorld getOverworld(World world) {
        MinecraftServer server = world.getServer();
        return DimensionManager.getWorld(server, DimensionType.OVERWORLD, false, false);
    }

    public static ServerWorld loadWorld(DimensionType type) {
        ServerWorld world = getWorld(type);
        if (world == null) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            return server.getWorld(type);
        }
        return world;
    }

    public static ServerWorld getWorld(DimensionType type) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return DimensionManager.getWorld(server, type, false, false);
    }

    public static ServerWorld getWorld(World world, DimensionType type) {
        MinecraftServer server = world.getServer();
        return DimensionManager.getWorld(server, type, false, false);
    }
}
