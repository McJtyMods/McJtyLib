package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.preferences.PreferencesProperties;
import mcjty.lib.tools.MinecraftTools;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public class SendPreferencesToClientHelper {

    public static void setPreferences(PacketSendPreferencesToClient prefs) {
        EntityPlayerSP player = MinecraftTools.getPlayer(Minecraft.getMinecraft());
        PreferencesProperties properties = McJtyLib.getPreferencesProperties(player);
        properties.setBuffXY(prefs.getBuffX(), prefs.getBuffY());
        System.out.println("setPreferences: prefs.getStyle() = " + prefs.getStyle());
        properties.setStyle(prefs.getStyle().getStyle());
    }
}
