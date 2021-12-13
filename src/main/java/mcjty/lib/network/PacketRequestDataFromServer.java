package mcjty.lib.network;

import mcjty.lib.blockcommands.ICommand;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.LevelTools;
import mcjty.lib.varia.Logging;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

/**
 * This is a packet that can be used to send a command from the client side (typically the GUI) to
 * a tile entity on the server that has a ResultCommand annotated with @ServerCommand
 */
public class PacketRequestDataFromServer {
    protected BlockPos pos;
    private ResourceKey<Level> type;
    protected String command;
    protected TypedMap params;
    private boolean dummy;

    public PacketRequestDataFromServer(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
        type = LevelTools.getId(buf.readResourceLocation());
        command = buf.readUtf(32767);
        params = TypedMapTools.readArguments(buf);
        dummy = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeResourceLocation(type.location());
        buf.writeUtf(command);
        TypedMapTools.writeArguments(buf, params);
        buf.writeBoolean(dummy);
    }

    public PacketRequestDataFromServer(ResourceKey<Level> type, BlockPos pos, String command, TypedMap params, boolean dummy) {
        this.type = type;
        this.pos = pos;
        this.command = command;
        this.params = params;
        this.dummy = dummy;
    }

    public PacketRequestDataFromServer(ResourceKey<Level> type, BlockPos pos, ICommand command, TypedMap params, boolean dummy) {
        this.type = type;
        this.pos = pos;
        this.command = command.getName();
        this.params = params;
        this.dummy = dummy;
    }

    public void handle(SimpleChannel channel, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Level world = LevelTools.getLevel(ctx.getSender().getCommandSenderWorld(), type);
            if (world.hasChunkAt(pos)) {
                BlockEntity te = world.getBlockEntity(pos);

                if (te instanceof GenericTileEntity) {
                    TypedMap result = ((GenericTileEntity) te).executeServerCommandWR(command, ctx.getSender(), params);
                    if (result != null) {
                        PacketDataFromServer msg = new PacketDataFromServer(dummy ? null : pos, command, result);
                        channel.sendTo(msg, ctx.getSender().connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                        return;
                    }
                }

                Logging.log("Command " + command + " was not handled!");
            }
        });
        ctx.setPacketHandled(true);
    }
}
