package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.preferences.PreferencesProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class SendPreferencesToClientHelper {

    public static void setPreferences(PacketSendPreferencesToClient prefs) {
        Player player = Minecraft.getInstance().player;
        PreferencesProperties properties = McJtyLib.getPreferencesProperties(player);
        if (properties != null) {
            properties.setBuffXY(prefs.getBuffStyle(), prefs.getBuffX(), prefs.getBuffY());
            properties.setStyle(prefs.getStyle().getStyle());
        }
    }
}
