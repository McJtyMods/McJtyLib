package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Send a packet from the client to the server in order to execute a server side command
 * registered with McJtyLib.registerCommand()
 */
public record PacketSendServerCommand(String modid, String command, TypedMap arguments) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "sendservercommand");
    public static final CustomPacketPayload.Type<PacketSendServerCommand> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSendServerCommand> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PacketSendServerCommand::modid,
            ByteBufCodecs.STRING_UTF8, PacketSendServerCommand::command,
            TypedMap.STREAM_CODEC, PacketSendServerCommand::arguments,
            PacketSendServerCommand::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PacketSendServerCommand create(String modid, String command, TypedMap arguments) {
        return new PacketSendServerCommand(modid, command, arguments);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            try {
                boolean result = McJtyLib.handleCommand(modid, command, player, arguments);
                if (!result) {
                    Logging.logError("Error handling command '" + command + "' for mod '" + modid + "'!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }
}
