package mcjty.lib.network.clientinfo;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Optional;

public interface InfoPacketServer {

    void fromBytes(ByteBuf buf);
    void toBytes(ByteBuf buf);

    Optional<InfoPacketClient> onMessageServer(EntityPlayerMP player);
}
