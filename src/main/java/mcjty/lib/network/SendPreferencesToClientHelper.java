package mcjty.lib.network;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.lib.preferences.PlayerPreferencesProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;

@SideOnly(Side.CLIENT)
public class SendPreferencesToClientHelper {

    public static void setPreferences(PacketSendPreferencesToClient prefs) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        PlayerPreferencesProperties properties = PlayerPreferencesProperties.getProperties(player);
        properties.getPreferencesProperties().setBuffXY(prefs.getBuffX(), prefs.getBuffY());
        System.out.println("setPreferences: prefs.getStyle() = " + prefs.getStyle());
        properties.getPreferencesProperties().setStyle(prefs.getStyle().getStyle());
    }
}
