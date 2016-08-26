package mcjty.lib.varia;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldTools {
    public static boolean chunkLoaded(World world, BlockPos pos) {
        if (world == null || pos == null) {
            return false;
        }
        return world.getChunkProvider().getLoadedChunk(pos.getX() >> 4, pos.getZ() >> 4) != null && world.getChunkFromBlockCoords(pos).isLoaded();
    }


}
