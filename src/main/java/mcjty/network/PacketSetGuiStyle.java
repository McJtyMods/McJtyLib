package mcjty.network;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mcjty.base.ModBase;
import mcjty.gui.GuiStyle;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Change the GUI style.
 */
public class PacketSetGuiStyle implements IMessage, IMessageHandler<PacketSetGuiStyle, IMessage> {

    private String modId;
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

    public PacketSetGuiStyle(ModBase modBase, String style) {
        this.modId = modBase.getModId();
        this.style = style;
    }

    @Override
    public IMessage onMessage(PacketSetGuiStyle message, MessageContext ctx) {
        EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;
        ModContainer modContainer = Loader.instance().getIndexedModList().get(message.modId);
        ((ModBase)modContainer).setGuiStyle(playerEntity, GuiStyle.getStyle(message.style));
        return null;
    }

}
