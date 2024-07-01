package mcjty.lib.network;

import mcjty.lib.typed.TypedMap;
import net.neoforged.neoforge.network.handling.IPayloadContext;

// @todo 1.21 Neo: we no longer use this
public record PacketSendClientCommand(String modid, String command, TypedMap arguments) {
//
//    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "sendclientcommand");
//    public static final CustomPacketPayload.Type<PacketSendClientCommand> TYPE = new Type<>(ID);
//
//    public static final StreamCodec<FriendlyByteBuf, PacketSendClientCommand> CODEC = StreamCodec.composite(
//            ByteBufCodecs.STRING_UTF8, PacketSendClientCommand::modid,
//            ByteBufCodecs.STRING_UTF8, PacketSendClientCommand::command,
//            TypedMap.CODEC,
//            PacketSendClientCommand::new);
//
//    public static PacketSendClientCommand create(String modid, String cmdFlashEndergenic, TypedMap build) {
//        return new PacketSendClientCommand(modid, cmdFlashEndergenic, build);
//    }
//
//    @Override
//    public Type<? extends CustomPacketPayload> type() {
//        return TYPE;
//    }
//
//    public String getModid() {
//        return modid;
//    }
//
//    public String getCommand() {
//        return command;
//    }
//
//    public TypedMap getArguments() {
//        return arguments;
//    }
//
    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ClientCommandHandlerHelper.handle(this);
        });
    }
}
