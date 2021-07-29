package mcjty.lib.varia;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SecurityTools {

    // @todo
//    public static boolean isAdmin(PlayerEntity player) {
//        return player.capabilities.isCreativeMode || MinecraftServer.getServer().getConfigurationManager().getOppedPlayers().canBypassPlayerLimit(player.getGameProfile());
//    }

    public static boolean isPrivileged(Player player, Level world) {
        return player.abilities.instabuild || world.getServer().getPlayerList().isOp(player.getGameProfile());
    }

}
