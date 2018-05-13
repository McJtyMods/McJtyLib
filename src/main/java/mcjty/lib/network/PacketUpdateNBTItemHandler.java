package mcjty.lib.network;

import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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
        for (Key<?> akey : message.args.getKeys()) {
            String key = akey.getName();
            if (Type.STRING.equals(akey.getType())) {
                tagCompound.setString(key, (String) message.args.get(akey));
            } else if (Type.INTEGER.equals(akey.getType())) {
                tagCompound.setInteger(key, (Integer) message.args.get(akey));
            } else if (Type.LONG.equals(akey.getType())) {
                tagCompound.setLong(key, (Long) message.args.get(akey));
            } else if (Type.DOUBLE.equals(akey.getType())) {
                tagCompound.setDouble(key, (Double) message.args.get(akey));
            } else if (Type.BOOLEAN.equals(akey.getType())) {
                tagCompound.setBoolean(key, (Boolean) message.args.get(akey));
            } else if (Type.BLOCKPOS.equals(akey.getType())) {
                throw new RuntimeException("BlockPos not supported for PacketUpdateNBTItem!");
            } else if (Type.ITEMSTACK.equals(akey.getType())) {
                throw new RuntimeException("ItemStack not supported for PacketUpdateNBTItem!");
            } else {
                throw new RuntimeException(akey.getType().getType().getSimpleName() + " not supported for PacketUpdateNBTItem!");
            }
        }
    }

}
