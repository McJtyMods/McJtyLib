package mcjty.lib.thirteen;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class Context {

    private final MessageContext context;

    public Context(MessageContext context) {
        this.context = context;
    }

    public void enqueueWork(Runnable runnable) {
        FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(runnable);
    }

    @Nullable
    public EntityPlayerMP getSender() {
        if (context.getServerHandler() != null) {
            return context.getServerHandler().player;
        } else {
            return null;
        }
    }

    public void setPacketHandled(boolean handled) {

    }
}
