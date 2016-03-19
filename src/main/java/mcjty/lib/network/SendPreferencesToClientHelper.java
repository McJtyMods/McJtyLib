package mcjty.lib.network;

import mcjty.lib.preferences.PreferencesProperties;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import mcjty.lib.preferences.PlayerPreferencesProperties;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public class SendPreferencesToClientHelper {

    public static void setPreferences(PacketSendPreferencesToClient prefs) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        PreferencesProperties properties = PlayerPreferencesProperties.getProperties(player);
        properties.setBuffXY(prefs.getBuffX(), prefs.getBuffY());
        System.out.println("setPreferences: prefs.getStyle() = " + prefs.getStyle());
        properties.setStyle(prefs.getStyle().getStyle());
    }
}
