package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class AbstractServerCommandTyped implements IMessage {

    protected BlockPos pos;
    protected Integer dimensionId;
    protected String command;
    protected TypedMap params;

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = NetworkTools.readPos(buf);
        command = NetworkTools.readString(buf);
        params = readArguments(buf);
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
        writeArguments(buf, params);
        if (dimensionId != null) {
            buf.writeBoolean(true);
            buf.writeInt(dimensionId);
        } else {
            buf.writeBoolean(false);
        }
    }

    public static TypedMap readArguments(ByteBuf buf) {
        TypedMap.Builder args = TypedMap.builder();
        int size = buf.readInt();
        if (size != 0) {
            for (int i = 0 ; i < size ; i++) {
                String key = NetworkTools.readString(buf);
                ArgumentType type = ArgumentType.getType(buf.readByte());
                switch (type) {
                    case TYPE_STRING:
                        args.put(new Key<>(key, Type.STRING), NetworkTools.readString(buf));
                        break;
                    case TYPE_INTEGER:
                        args.put(new Key<>(key, Type.INTEGER), buf.readInt());
                        break;
                    case TYPE_BOOLEAN:
                        args.put(new Key<>(key, Type.BOOLEAN), buf.readBoolean());
                        break;
                    case TYPE_DOUBLE:
                        args.put(new Key<>(key, Type.DOUBLE), buf.readDouble());
                        break;
                    case TYPE_BLOCKPOS:
                        args.put(new Key<>(key, Type.BLOCKPOS), NetworkTools.readPos(buf));
                        break;
                    case TYPE_STACK:
                        args.put(new Key<>(key, Type.ITEMSTACK), NetworkTools.readItemStack(buf));
                        break;
                }
            }
        }
        return args.build();
    }

    public static void writeArguments(ByteBuf buf, TypedMap args) {
        buf.writeInt(args.size());
        for (Key key : args.getKeys()) {
            NetworkTools.writeString(buf, key.getName());
            if (key.getType() == Type.STRING) {
                buf.writeByte(ArgumentType.TYPE_STRING.ordinal());
                NetworkTools.writeString(buf, (String) args.get(key));
            } else if (key.getType() == Type.INTEGER) {
                buf.writeByte(ArgumentType.TYPE_INTEGER.ordinal());
                buf.writeInt((Integer) args.get(key));
            } else if (key.getType() == Type.BOOLEAN) {
                buf.writeByte(ArgumentType.TYPE_BOOLEAN.ordinal());
                buf.writeBoolean((Boolean) args.get(key));
            } else if (key.getType() == Type.DOUBLE) {
                buf.writeByte(ArgumentType.TYPE_DOUBLE.ordinal());
                buf.writeDouble((Double) args.get(key));
            } else if (key.getType() == Type.BLOCKPOS) {
                buf.writeByte(ArgumentType.TYPE_BLOCKPOS.ordinal());
                NetworkTools.writePos(buf, (BlockPos) args.get(key));
            } else if (key.getType() == Type.ITEMSTACK) {
                buf.writeByte(ArgumentType.TYPE_STACK.ordinal());
                NetworkTools.writeItemStack(buf, (ItemStack) args.get(key));
            } else {
                throw new RuntimeException("Unsupported type for key " + key.getName() + "!");
            }
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
