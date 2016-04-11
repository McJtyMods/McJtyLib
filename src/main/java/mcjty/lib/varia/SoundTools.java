package mcjty.lib.varia;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoundTools {

    // Server side: play a sound to all nearby players
    public static void playSound(World worldObj, SoundEvent soundName, double x, double y, double z, double volume, double pitch) {
        SPacketSoundEffect soundEffect = new SPacketSoundEffect(soundName, SoundCategory.BLOCKS, x, y, z, (float) volume, (float) pitch);

        for (int j = 0; j < worldObj.playerEntities.size(); ++j) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)worldObj.playerEntities.get(j);
            BlockPos chunkcoordinates = entityplayermp.getPosition();
            double d7 = x - chunkcoordinates.getX();
            double d8 = y - chunkcoordinates.getY();
            double d9 = z - chunkcoordinates.getZ();
            double d10 = d7 * d7 + d8 * d8 + d9 * d9;

            if (d10 <= 256.0D) {
                entityplayermp.playerNetServerHandler.sendPacket(soundEffect);
            }
        }
    }

}
