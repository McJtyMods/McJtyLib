package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.gui.BuffStyle;
import mcjty.lib.gui.GuiStyle;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSendPreferencesToClient {
    private final BuffStyle buffStyle;
    private final int buffX;
    private final int buffY;
    private final GuiStyle style;

    public PacketSendPreferencesToClient(ByteBuf buf) {
        buffStyle = BuffStyle.values()[buf.readInt()];
        buffX = buf.readInt();
        buffY = buf.readInt();
        style = GuiStyle.values()[buf.readInt()];
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(buffStyle.ordinal());
        buf.writeInt(buffX);
        buf.writeInt(buffY);
        buf.writeInt(style.ordinal());
    }

    public PacketSendPreferencesToClient(BuffStyle buffStyle, int buffX, int buffY, GuiStyle style) {
        this.buffStyle = buffStyle;
        this.buffX = buffX;
        this.buffY = buffY;
        this.style = style;
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

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            SendPreferencesToClientHelper.setPreferences(this);
        });
        ctx.setPacketHandled(true);
    }

}
