package mcjty.lib.network;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import mcjty.lib.preferences.PlayerPreferencesProperties;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public class SendPreferencesToClientHelper {

    public static void setPreferences(PacketSendPreferencesToClient prefs) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        PlayerPreferencesProperties properties = PlayerPreferencesProperties.getProperties(player);
        properties.getPreferencesProperties().setBuffXY(prefs.getBuffX(), prefs.getBuffY());
        System.out.println("setPreferences: prefs.getStyle() = " + prefs.getStyle());
        properties.getPreferencesProperties().setStyle(prefs.getStyle().getStyle());
    }
}
