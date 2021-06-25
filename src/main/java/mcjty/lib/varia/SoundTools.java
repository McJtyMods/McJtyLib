package mcjty.lib.varia;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoundTools {

    // Server side: play a sound to all nearby players
    public static void playSound(World worldObj, SoundEvent soundName, double x, double y, double z, double volume, double pitch) {
        SPlaySoundEffectPacket soundEffect = new SPlaySoundEffectPacket(soundName, SoundCategory.BLOCKS, x, y, z, (float) volume, (float) pitch);

        for (int j = 0; j < worldObj.players().size(); ++j) {
            ServerPlayerEntity player = (ServerPlayerEntity)worldObj.players().get(j);
            BlockPos chunkcoordinates = player.blockPosition();
            double d7 = x - chunkcoordinates.getX();
            double d8 = y - chunkcoordinates.getY();
            double d9 = z - chunkcoordinates.getZ();
            double d10 = d7 * d7 + d8 * d8 + d9 * d9;

            if (d10 <= 256.0D) {
                player.connection.send(soundEffect);
            }
        }
    }

}
