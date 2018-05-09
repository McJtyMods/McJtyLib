package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.typed.TypedMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * This is a packet that can be used to update the NBT on the held item of a player.
 */
public abstract class PacketUpdateNBTItem implements IMessage {
    public TypedMap args;

    @Override
    public void fromBytes(ByteBuf buf) {
        args = AbstractServerCommandTyped.readArguments(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        AbstractServerCommandTyped.writeArguments(buf, args);
    }

    public PacketUpdateNBTItem() {
    }

    public PacketUpdateNBTItem(TypedMap arguments) {
        this.args = arguments;
    }

    protected abstract boolean isValidItem(ItemStack stack);

}
