package mcjty.lib.network;

import mcjty.lib.varia.Logging;
import mcjty.typed.TypedMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This is a packet that can be used to send a command from the client side (typically the GUI) to
 * a tile entity on the server side that implements CommandHandler. This will call 'execute()' on
 * that command handler.
 */
public class PacketServerCommandTyped extends AbstractServerCommandTyped {

    public PacketServerCommandTyped() {
    }

    public PacketServerCommandTyped(BlockPos pos, String command, TypedMap params) {
        super(pos, command, params);
        this.dimensionId = null;
    }

    public PacketServerCommandTyped(BlockPos pos, Integer dimensionId, String command, TypedMap params) {
        super(pos, command, params);
        this.dimensionId = dimensionId;
    }

    public static class Handler implements IMessageHandler<PacketServerCommandTyped, IMessage> {
        @Override
        public IMessage onMessage(PacketServerCommandTyped message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketServerCommandTyped message, MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().player;
            World world;
            if (message.dimensionId == null) {
                world = playerEntity.getEntityWorld();
            } else {
                world = DimensionManager.getWorld(message.dimensionId);
            }
            if (world == null) {
                return;
            }
            TileEntity te = world.getTileEntity(message.pos);
            if(!(te instanceof CommandHandler)) {
                Logging.log("createStartScanPacket: TileEntity is not a CommandHandler!");
                return;
            }
            CommandHandler commandHandler = (CommandHandler) te;
            if (!commandHandler.execute(playerEntity, message.command, message.params)) {
                Logging.log("Command " + message.command + " was not handled!");
            }
        }
    }
}
