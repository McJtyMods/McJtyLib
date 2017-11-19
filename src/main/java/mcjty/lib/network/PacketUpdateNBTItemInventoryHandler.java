package mcjty.lib.network;

import mcjty.lib.tools.ItemStackTools;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateNBTItemInventoryHandler<T extends PacketUpdateNBTItemInventory> implements IMessageHandler<T, IMessage> {
    @Override
    public IMessage onMessage(T message, MessageContext ctx) {
        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
        return null;
    }

    private void handle(T message, MessageContext ctx) {
        World world = ctx.getServerHandler().player.getEntityWorld();
        TileEntity te = world.getTileEntity(message.pos);
        if (te instanceof IInventory) {
            if (!message.isValidBlock(world, message.pos, te)) {
                return;
            }
            IInventory inv = (IInventory) te;
            ItemStack stack = inv.getStackInSlot(message.slotIndex);
            if (ItemStackTools.isValid(stack)) {
                stack.setTagCompound(message.tagCompound);
            }
        }
    }

}
