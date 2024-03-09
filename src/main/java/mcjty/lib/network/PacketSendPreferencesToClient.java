package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.gui.BuffStyle;
import mcjty.lib.gui.GuiStyle;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record PacketSendPreferencesToClient(BuffStyle buffStyle, Integer buffX, Integer buffY, GuiStyle style) implements CustomPacketPayload {

    public final static ResourceLocation ID = new ResourceLocation(McJtyLib.MODID, "sendpreferences");

    public static PacketSendPreferencesToClient create(FriendlyByteBuf buf) {
        BuffStyle buffStyle = BuffStyle.values()[buf.readInt()];
        int buffX = buf.readInt();
        int buffY = buf.readInt();
        GuiStyle style = GuiStyle.values()[buf.readInt()];
        return new PacketSendPreferencesToClient(buffStyle, buffX, buffY, style);
    }

    public static PacketSendPreferencesToClient create(BuffStyle buffStyle, int buffX, int buffY, GuiStyle style) {
        return new PacketSendPreferencesToClient(buffStyle, buffX, buffY, style);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(buffStyle.ordinal());
        buf.writeInt(buffX);
        buf.writeInt(buffY);
        buf.writeInt(style.ordinal());
    }

    @Override
    public ResourceLocation id() {
        return ID;
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

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            SendPreferencesToClientHelper.setPreferences(this);
        });
    }

}
