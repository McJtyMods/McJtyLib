package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.typed.TypedMap;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

@Deprecated
public class AbstractServerCommandTyped implements IMessage {

    protected BlockPos pos;
    protected Integer dimensionId;
    protected String command;
    protected TypedMap params;

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = NetworkTools.readPos(buf);
        command = NetworkTools.readString(buf);
        params = TypedMapTools.readArguments(buf);
        if (buf.readBoolean()) {
            dimensionId = buf.readInt();
        } else {
            dimensionId = null;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writePos(buf, pos);
        NetworkTools.writeString(buf, command);
        TypedMapTools.writeArguments(buf, params);
        if (dimensionId != null) {
            buf.writeBoolean(true);
            buf.writeInt(dimensionId);
        } else {
            buf.writeBoolean(false);
        }
    }

    protected AbstractServerCommandTyped() {
    }

    protected AbstractServerCommandTyped(BlockPos pos, String command, TypedMap args) {
        this.pos = pos;
        this.command = command;
        this.params = args;
    }

}
