package mcjty.lib.varia;

import net.minecraft.server.MinecraftServer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class WorldTools {

    public static boolean isLoaded(Level world, BlockPos pos) {
        if (world == null || pos == null) {
            return false;
        }
        return world.hasChunkAt(pos);
    }

    public static ServerLevel getOverworld() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server.getLevel(Level.OVERWORLD);
    }

    public static ServerLevel getOverworld(Level world) {
        MinecraftServer server = world.getServer();
        return server.getLevel(Level.OVERWORLD);
    }

    public static ServerLevel loadWorld(DimensionId type) {
        ServerLevel world = getWorld(type);
        if (world == null) {
            // Worlds in 1.16 are always loaded
            return type.loadWorld();
        }
        return world;
    }

    public static ServerLevel getWorld(DimensionId type) {
        return type.getWorld();
    }

    public static ServerLevel getWorld(Level world, DimensionId type) {
        // Worlds in 1.16 are always loaded
        return type.loadWorld(world);
    }
}
