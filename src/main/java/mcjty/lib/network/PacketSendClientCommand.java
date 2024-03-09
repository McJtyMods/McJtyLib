package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.typed.TypedMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record PacketSendClientCommand(String modid, String command, TypedMap arguments) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(McJtyLib.MODID, "sendclientcommand");

    public static PacketSendClientCommand create(String modid, String cmdFlashEndergenic, TypedMap build) {
        return new PacketSendClientCommand(modid, cmdFlashEndergenic, build);
    }

    public String getModid() {
        return modid;
    }

    public String getCommand() {
        return command;
    }

    public TypedMap getArguments() {
        return arguments;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(modid);
        buf.writeUtf(command);
        TypedMapTools.writeArguments(buf, arguments);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static PacketSendClientCommand create(FriendlyByteBuf buf) {
        String modid = buf.readUtf(32767);
        String command = buf.readUtf(32767);
        TypedMap arguments = TypedMapTools.readArguments(buf);
        return new PacketSendClientCommand(modid, command, arguments);
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ClientCommandHandlerHelper.handle(this);
        });
    }
}
