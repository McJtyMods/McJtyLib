package mcjty.lib.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mcjty.lib.preferences.PlayerPreferencesProperties;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Change the GUI style.
 */
public class PacketSetGuiStyle implements IMessage, IMessageHandler<PacketSetGuiStyle, IMessage> {

    private String style;

    @Override
    public void fromBytes(ByteBuf buf) {
        style = NetworkTools.readString(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writeString(buf, style);
    }

    public PacketSetGuiStyle() {
    }

    public PacketSetGuiStyle(String style) {
        this.style = style;
    }

    @Override
    public IMessage onMessage(PacketSetGuiStyle message, MessageContext ctx) {
        EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;

        PlayerPreferencesProperties properties = PlayerPreferencesProperties.getProperties(playerEntity);
        properties.getPreferencesProperties().setStyle(message.style);

        return null;
    }

}
