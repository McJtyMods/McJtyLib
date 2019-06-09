package mcjty.lib.varia;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class SecurityTools {

    // @todo
//    public static boolean isAdmin(PlayerEntity player) {
//        return player.capabilities.isCreativeMode || MinecraftServer.getServer().getConfigurationManager().getOppedPlayers().func_183026_b(player.getGameProfile());
//    }

    public static boolean isPrivileged(PlayerEntity player, World world) {
        return player.capabilities.isCreativeMode || world.getMinecraftServer().getPlayerList().canSendCommands(player.getGameProfile());
    }

}
