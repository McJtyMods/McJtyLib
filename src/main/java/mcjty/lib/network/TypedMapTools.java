package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypedMapTools {

    private static Map<Type<?>, ArgumentType> typeToIndex = null;

    private static void setupTypeMapping() {
        if (typeToIndex == null) {
            typeToIndex = new HashMap<>();
            registerMapping(Type.STRING, ArgumentType.TYPE_STRING);
            registerMapping(Type.INTEGER, ArgumentType.TYPE_INTEGER);
            registerMapping(Type.BLOCKPOS, ArgumentType.TYPE_BLOCKPOS);
            registerMapping(Type.BOOLEAN, ArgumentType.TYPE_BOOLEAN);
            registerMapping(Type.DOUBLE, ArgumentType.TYPE_DOUBLE);
            registerMapping(Type.ITEMSTACK, ArgumentType.TYPE_STACK);
            registerMapping(Type.LONG, ArgumentType.TYPE_LONG);
            registerMapping(Type.STRING_LIST, ArgumentType.TYPE_STRING_LIST);
            registerMapping(Type.ITEMSTACK_LIST, ArgumentType.TYPE_ITEMSTACK_LIST);
            registerMapping(Type.POS_LIST, ArgumentType.TYPE_POS_LIST);
        }
    }

    private static void registerMapping(Type<?> type, ArgumentType argumentType) {
        typeToIndex.put(type, argumentType);
    }

    private static ArgumentType getArgumentType(Type<?> type) {
        setupTypeMapping();
        return typeToIndex.get(type);
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
            ArgumentType argumentType = getArgumentType(key.getType());
            buf.writeByte(argumentType.ordinal());
            switch (argumentType) {
                case TYPE_STRING:
                    NetworkTools.writeString(buf, (String) args.get(key));
                    break;
                case TYPE_INTEGER:
                    buf.writeInt((Integer) args.get(key));
                    break;
                case TYPE_BLOCKPOS: {
                    BlockPos pos = (BlockPos) args.get(key);
                    if (pos != null) {
                        buf.writeBoolean(true);
                        NetworkTools.writePos(buf, pos);
                    } else {
                        buf.writeBoolean(false);
                    }
                    break;
                }
                case TYPE_BOOLEAN:
                    buf.writeBoolean((Boolean) args.get(key));
                    break;
                case TYPE_DOUBLE:
                    buf.writeDouble((Double) args.get(key));
                    break;
                case TYPE_STACK: {
                    ItemStack stack = (ItemStack) args.get(key);
                    if (stack != null) {
                        buf.writeBoolean(true);
                        NetworkTools.writeItemStack(buf, stack);
                    } else {
                        buf.writeBoolean(false);
                    }
                    break;
                }
                case TYPE_LONG:
                    buf.writeLong((Long) args.get(key));
                    break;
                case TYPE_STRING_LIST: {
                    List<String> list = (List<String>) args.get(key);
                    if (list != null) {
                        buf.writeInt(list.size());
                        for (String s : list) {
                            NetworkTools.writeStringUTF8(buf, s);
                        }
                    } else {
                        buf.writeInt(-1);
                    }
                    break;
                }
                case TYPE_ITEMSTACK_LIST: {
                    List<ItemStack> list = (List<ItemStack>) args.get(key);
                    if (list != null) {
                        buf.writeInt(list.size());
                        for (ItemStack s : list) {
                            NetworkTools.writeItemStack(buf, s);
                        }
                    } else {
                        buf.writeInt(-1);
                    }
                    break;
                }
                case TYPE_POS_LIST: {
                    List<BlockPos> list = (List<BlockPos>) args.get(key);
                    if (list != null) {
                        buf.writeInt(list.size());
                        for (BlockPos s : list) {
                            NetworkTools.writePos(buf, s);
                        }
                    } else {
                        buf.writeInt(-1);
                    }
                    break;
                }
            }
        }
    }

    // @todo, remove this
    enum ArgumentType {
        TYPE_STRING(0),
        TYPE_INTEGER(1),
        TYPE_BLOCKPOS(2),
        TYPE_BOOLEAN(3),
        TYPE_DOUBLE(4),
        TYPE_STACK(5),
        TYPE_LONG(6),
        TYPE_STRING_LIST(7),
        TYPE_ITEMSTACK_LIST(8),
        TYPE_POS_LIST(9);

        private final int index;
        private static final Map<Integer, ArgumentType> mapping = new HashMap<>();

        static {
            for (ArgumentType type : ArgumentType.values()) {
                mapping.put(type.index, type);
            }
        }

        ArgumentType(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public static ArgumentType getType(int index) {
            return mapping.get(index);
        }
    }
}
