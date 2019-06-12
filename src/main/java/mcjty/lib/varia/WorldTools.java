package mcjty.lib.varia;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

public class WorldTools {

    public static boolean chunkLoaded(World world, BlockPos pos) {
        if (world == null || pos == null) {
            return false;
        }
        return world.isBlockLoaded(pos);
//        return world.getChunkProvider().getLoadedChunk(pos.getX() >> 4, pos.getZ() >> 4) != null && world.getChunkFromBlockCoords(pos).isLoaded();
    }

    /**
     * Find a biome based on ID or registry name
     */
    public static Biome findBiome(String biomeId) {
        Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(biomeId));
        if (biome == null) {
            for (Biome b : ForgeRegistries.BIOMES) {
                ResourceLocation registryName = b.getRegistryName();
                if (registryName != null && biomeId.equals(registryName.getPath())) {
                    biome = b;
                    break;
                }
            }
        }
        return biome;
    }


}
