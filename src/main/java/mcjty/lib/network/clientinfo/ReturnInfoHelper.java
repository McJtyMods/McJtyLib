package mcjty.lib.network.clientinfo;



import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ReturnInfoHelper {
    public static void onMessageFromServer(PacketReturnInfoToClient message) {
        Minecraft.getMinecraft().addScheduledTask(() -> message.getPacket().onMessageClient(Minecraft.getMinecraft().thePlayer));
    }
}
