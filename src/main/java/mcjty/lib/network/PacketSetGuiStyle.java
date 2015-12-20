package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.preferences.PlayerPreferencesProperties;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Change the GUI style.
 */
public class PacketSetGuiStyle implements IMessage {

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

    public static class Handler implements IMessageHandler<PacketSetGuiStyle, IMessage> {
        @Override
        public IMessage onMessage(PacketSetGuiStyle message, MessageContext ctx) {
            MinecraftServer.getServer().addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketSetGuiStyle message, MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;

            PlayerPreferencesProperties properties = PlayerPreferencesProperties.getProperties(playerEntity);
            properties.getPreferencesProperties().setStyle(message.style);
        }

    }
}
