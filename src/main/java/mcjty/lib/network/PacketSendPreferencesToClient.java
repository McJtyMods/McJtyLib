package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.gui.BuffStyle;
import mcjty.lib.gui.GuiStyle;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketSendPreferencesToClient(BuffStyle buffStyle, Integer buffX, Integer buffY, GuiStyle style) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "sendpreferences");
    public static final CustomPacketPayload.Type<PacketSendPreferencesToClient> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PacketSendPreferencesToClient> CODEC = StreamCodec.composite(
            BuffStyle.STREAM_CODEC, PacketSendPreferencesToClient::buffStyle,
            ByteBufCodecs.INT, PacketSendPreferencesToClient::buffX,
            ByteBufCodecs.INT, PacketSendPreferencesToClient::buffY,
            GuiStyle.STREAM_CODEC, PacketSendPreferencesToClient::style,
            PacketSendPreferencesToClient::new
    );

    public static PacketSendPreferencesToClient create(BuffStyle buffStyle, int buffX, int buffY, GuiStyle style) {
        return new PacketSendPreferencesToClient(buffStyle, buffX, buffY, style);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public BuffStyle getBuffStyle() {
        return buffStyle;
    }

    public int getBuffX() {
        return buffX;
    }

    public int getBuffY() {
        return buffY;
    }

    public GuiStyle getStyle() {
        return style;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            SendPreferencesToClientHelper.setPreferences(this);
        });
    }

}
