package mcjty.lib.varia;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class SecurityTools {

    public static boolean isAdmin(EntityPlayer player) {
        return player.capabilities.isCreativeMode || MinecraftServer.getServer().getConfigurationManager().getOppedPlayers().func_183026_b(player.getGameProfile());
    }

}
