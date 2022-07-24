package mcjty.lib.varia;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

public class SoundTools {

    // Server side: play a sound to all nearby players
    public static void playSound(Level worldObj, SoundEvent soundName, double x, double y, double z, double volume, double pitch) {
        ClientboundSoundPacket soundEffect = new ClientboundSoundPacket(soundName, SoundSource.BLOCKS, x, y, z, (float) volume, (float) pitch, 0);  // @todo seed parameter?

        for (int j = 0; j < worldObj.players().size(); ++j) {
            ServerPlayer player = (ServerPlayer)worldObj.players().get(j);
            BlockPos chunkcoordinates = player.blockPosition();
            double xx = x - chunkcoordinates.getX();
            double yy = y - chunkcoordinates.getY();
            double zz = z - chunkcoordinates.getZ();
            double sqDist = xx * xx + yy * yy + zz * zz;

            if (sqDist <= 256.0D) {
                player.connection.send(soundEffect);
            }
        }
    }

}
