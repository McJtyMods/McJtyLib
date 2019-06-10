package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.preferences.PreferencesProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;

public class SendPreferencesToClientHelper {

    public static void setPreferences(PacketSendPreferencesToClient prefs) {
        PlayerEntity player = Minecraft.getInstance().player;
        PreferencesProperties properties = McJtyLib.getPreferencesProperties(player);
        properties.setBuffXY(prefs.getBuffX(), prefs.getBuffY());
        properties.setStyle(prefs.getStyle().getStyle());
    }
}
