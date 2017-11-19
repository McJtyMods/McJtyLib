package mcjty.lib.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Map;

public class PacketUpdateNBTItemHandler<T extends PacketUpdateNBTItem> implements IMessageHandler<T, IMessage> {
    @Override
    public IMessage onMessage(T message, MessageContext ctx) {
        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
        return null;
    }

    private void handle(T message, MessageContext ctx) {
        EntityPlayerMP playerEntity = ctx.getServerHandler().player;
        ItemStack heldItem = playerEntity.getHeldItem(EnumHand.MAIN_HAND);
        if (heldItem.isEmpty()) {
            return;
        }
        // To avoid people messing with packets
        if (!message.isValidItem(heldItem)) {
            return;
        }
        NBTTagCompound tagCompound = heldItem.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
            heldItem.setTagCompound(tagCompound);
        }
        for (Map.Entry<String, Argument> entry : message.args.entrySet()) {
            String key = entry.getKey();
            switch (entry.getValue().getType()) {
                case TYPE_STRING:
                    tagCompound.setString(key, entry.getValue().getString());
                    break;
                case TYPE_INTEGER:
                    tagCompound.setInteger(key, entry.getValue().getInteger());
                    break;
                case TYPE_BLOCKPOS:
                    throw new RuntimeException("BlockPos not supported for PacketUpdateNBTItem!");
                case TYPE_BOOLEAN:
                    tagCompound.setBoolean(key, entry.getValue().getBoolean());
                    break;
                case TYPE_DOUBLE:
                    tagCompound.setDouble(key, entry.getValue().getDouble());
                    break;
            }
        }
    }

}
