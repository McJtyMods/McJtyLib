package mcjty.lib.varia;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class SoundTools {

    // Server side: play a sound to all nearby players
    public static void playSound(Level worldObj, SoundEvent soundName, double x, double y, double z, double volume, double pitch) {
        ClientboundSoundPacket soundEffect = new ClientboundSoundPacket(soundName, SoundSource.BLOCKS, x, y, z, (float) volume, (float) pitch);

        for (int j = 0; j < worldObj.players().size(); ++j) {
            ServerPlayer player = (ServerPlayer)worldObj.players().get(j);
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
