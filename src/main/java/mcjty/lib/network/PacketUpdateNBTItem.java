package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a packet that can be used to update the NBT on the held item of a player.
 */
public abstract class PacketUpdateNBTItem implements IMessage {
    public Map<String,Argument> args;

    @Override
    public void fromBytes(ByteBuf buf) {
        args = AbstractServerCommand.readArguments(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        AbstractServerCommand.writeArguments(buf, args);
    }

    public PacketUpdateNBTItem() {
    }

    public PacketUpdateNBTItem(Argument... arguments) {
        if (arguments == null) {
            this.args = null;
        } else {
            args = new HashMap<>(arguments.length);
            for (Argument arg : arguments) {
                args.put(arg.getName(), arg);
            }
        }
    }

    protected abstract boolean isValidItem(ItemStack stack);

}
