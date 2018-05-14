package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class TypedMapTools {

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
                    case TYPE_LONG:
                        args.put(new Key<>(key, Type.LONG), buf.readLong());
                        break;
                    case TYPE_BOOLEAN:
                        args.put(new Key<>(key, Type.BOOLEAN), buf.readBoolean());
                        break;
                    case TYPE_DOUBLE:
                        args.put(new Key<>(key, Type.DOUBLE), buf.readDouble());
                        break;
                    case TYPE_BLOCKPOS:
                        if (buf.readBoolean()) {
                            args.put(new Key<>(key, Type.BLOCKPOS), NetworkTools.readPos(buf));
                        } else {
                            args.put(new Key<>(key, Type.BLOCKPOS), null);
                        }
                        break;
                    case TYPE_STACK:
                        if (buf.readBoolean()) {
                            args.put(new Key<>(key, Type.ITEMSTACK), NetworkTools.readItemStack(buf));
                        } else {
                            args.put(new Key<>(key, Type.ITEMSTACK), null);
                        }
                        break;
                    case TYPE_STRING_LIST: {
                        int s = buf.readInt();
                        if (s == -1) {
                            args.put(new Key<>(key, Type.STRING_LIST), null);
                        } else {
                            List<String> list = new ArrayList<>(s);
                            for (int j = 0; j < s; j++) {
                                list.set(j, NetworkTools.readStringUTF8(buf));
                            }
                            args.put(new Key<>(key, Type.STRING_LIST), list);
                        }
                        break;
                    }
                    case TYPE_ITEMSTACK_LIST: {
                        int s = buf.readInt();
                        if (s == -1) {
                            args.put(new Key<>(key, Type.ITEMSTACK_LIST), null);
                        } else {
                            List<ItemStack> list = new ArrayList<>(s);
                            for (int j = 0; j < s; j++) {
                                list.set(j, NetworkTools.readItemStack(buf));
                            }
                            args.put(new Key<>(key, Type.ITEMSTACK_LIST), list);
                        }
                        break;
                    }
                    case TYPE_POS_LIST: {
                        int s = buf.readInt();
                        if (s == -1) {
                            args.put(new Key<>(key, Type.POS_LIST), null);
                        } else {
                            List<BlockPos> list = new ArrayList<>(s);
                            for (int j = 0; j < s; j++) {
                                list.set(j, NetworkTools.readPos(buf));
                            }
                            args.put(new Key<>(key, Type.POS_LIST), list);
                        }
                        break;
                    }
                    default:
                        throw new RuntimeException("Unsupported type for key '" + key + "'!");
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
            } else if (key.getType() == Type.LONG) {
                buf.writeByte(ArgumentType.TYPE_LONG.ordinal());
                buf.writeLong((Integer) args.get(key));
            } else if (key.getType() == Type.BOOLEAN) {
                buf.writeByte(ArgumentType.TYPE_BOOLEAN.ordinal());
                buf.writeBoolean((Boolean) args.get(key));
            } else if (key.getType() == Type.DOUBLE) {
                buf.writeByte(ArgumentType.TYPE_DOUBLE.ordinal());
                buf.writeDouble((Double) args.get(key));
            } else if (key.getType() == Type.BLOCKPOS) {
                buf.writeByte(ArgumentType.TYPE_BLOCKPOS.ordinal());
                BlockPos pos = (BlockPos) args.get(key);
                if (pos != null) {
                    buf.writeBoolean(true);
                    NetworkTools.writePos(buf, pos);
                } else {
                    buf.writeBoolean(false);
                }
            } else if (key.getType() == Type.ITEMSTACK) {
                buf.writeByte(ArgumentType.TYPE_STACK.ordinal());
                ItemStack stack = (ItemStack) args.get(key);
                if (stack != null) {
                    buf.writeBoolean(true);
                    NetworkTools.writeItemStack(buf, stack);
                } else {
                    buf.writeBoolean(false);
                }
            } else if (key.getType() == Type.STRING_LIST) {
                List<String> list = (List<String>) args.get(key);
                if (list != null) {
                    buf.writeInt(list.size());
                    for (String s : list) {
                        NetworkTools.writeStringUTF8(buf, s);
                    }
                } else {
                    buf.writeInt(-1);
                }
            } else if (key.getType() == Type.ITEMSTACK_LIST) {
                List<ItemStack> list = (List<ItemStack>) args.get(key);
                if (list != null) {
                    buf.writeInt(list.size());
                    for (ItemStack s : list) {
                        NetworkTools.writeItemStack(buf, s);
                    }
                } else {
                    buf.writeInt(-1);
                }
            } else if (key.getType() == Type.POS_LIST) {
                List<BlockPos> list = (List<BlockPos>) args.get(key);
                if (list != null) {
                    buf.writeInt(list.size());
                    for (BlockPos s : list) {
                        NetworkTools.writePos(buf, s);
                    }
                } else {
                    buf.writeInt(-1);
                }
            } else {
                throw new RuntimeException("Unsupported type for key " + key.getName() + "!");
            }
        }
    }
}
