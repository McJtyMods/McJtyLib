package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.gui.GuiStyle;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSendPreferencesToClient {
    private int buffX;
    private int buffY;
    private GuiStyle style;

    public PacketSendPreferencesToClient(ByteBuf buf) {
        buffX = buf.readInt();
        buffY = buf.readInt();
        style = GuiStyle.values()[buf.readInt()];
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(buffX);
        buf.writeInt(buffY);
        buf.writeInt(style.ordinal());
    }

    public PacketSendPreferencesToClient(int buffX, int buffY, GuiStyle style) {
        this.buffX = buffX;
        this.buffY = buffY;
        this.style = style;
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
