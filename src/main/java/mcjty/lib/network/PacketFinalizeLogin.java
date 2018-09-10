package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.McJtyLibClient;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This is sent from the server to the client after the login has occured so that packets that implement
 * IClientServerDelayed can be sent
 */
public class PacketFinalizeLogin implements IMessage, IMessageHandler<PacketFinalizeLogin, IMessage> {

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    @Override
    public IMessage onMessage(PacketFinalizeLogin message, MessageContext ctx) {
        finalizeClientLogin();
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void finalizeClientLogin() {
        McJtyLibClient.connected = true;
    }

}