package mcjty.lib.network;

import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.LevelTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TypedMapTools {

    private static Map<Type<?>, ArgumentType> typeToIndex = null;

    private static void setupTypeMapping() {
        if (typeToIndex == null) {
            typeToIndex = new HashMap<>();
            registerMapping(Type.STRING, ArgumentType.TYPE_STRING);
            registerMapping(Type.UUID, ArgumentType.TYPE_UUID);
            registerMapping(Type.INTEGER, ArgumentType.TYPE_INTEGER);
            registerMapping(Type.BLOCKPOS, ArgumentType.TYPE_BLOCKPOS);
            registerMapping(Type.DIMENSION_TYPE, ArgumentType.TYPE_DIMENSION_TYPE);
            registerMapping(Type.BOOLEAN, ArgumentType.TYPE_BOOLEAN);
            registerMapping(Type.DOUBLE, ArgumentType.TYPE_DOUBLE);
            registerMapping(Type.FLOAT, ArgumentType.TYPE_FLOAT);
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

    public static TypedMap readArguments(RegistryFriendlyByteBuf buf) {
        TypedMap.Builder args = TypedMap.builder();
        int size = buf.readInt();
        if (size != 0) {
            for (int i = 0 ; i < size ; i++) {
                readArgument(buf, args::put);
            }
        }
        return args.build();
    }

    public static void readArgument(RegistryFriendlyByteBuf buf, BiConsumer<Key, Object> args) {
        String key = buf.readUtf(32767);
        ArgumentType type = ArgumentType.getType(buf.readByte());
        switch (type) {
            case TYPE_STRING -> args.accept(new Key<>(key, Type.STRING), NetworkTools.readStringUTF8(buf));
            case TYPE_UUID -> args.accept(new Key<>(key, Type.UUID), buf.readUUID());
            case TYPE_INTEGER -> args.accept(new Key<>(key, Type.INTEGER), buf.readInt());
            case TYPE_LONG -> args.accept(new Key<>(key, Type.LONG), buf.readLong());
            case TYPE_BOOLEAN -> args.accept(new Key<>(key, Type.BOOLEAN), buf.readBoolean());
            case TYPE_DOUBLE -> args.accept(new Key<>(key, Type.DOUBLE), buf.readDouble());
            case TYPE_FLOAT -> args.accept(new Key<>(key, Type.FLOAT), buf.readFloat());
            case TYPE_DIMENSION_TYPE -> {
                if (buf.readBoolean()) {
                    args.accept(new Key<>(key, Type.DIMENSION_TYPE), LevelTools.getId(buf.readResourceLocation()));
                } else {
                    args.accept(new Key<>(key, Type.DIMENSION_TYPE), null);
                }
            }
            case TYPE_BLOCKPOS -> {
                if (buf.readBoolean()) {
                    args.accept(new Key<>(key, Type.BLOCKPOS), buf.readBlockPos());
                } else {
                    args.accept(new Key<>(key, Type.BLOCKPOS), null);
                }
            }
            case TYPE_STACK -> {
                if (buf.readBoolean()) {
                    args.accept(new Key<>(key, Type.ITEMSTACK), NetworkTools.readItemStack(buf));
                } else {
                    args.accept(new Key<>(key, Type.ITEMSTACK), null);
                }
            }
            case TYPE_STRING_LIST -> {
                int s = buf.readInt();
                if (s == -1) {
                    args.accept(new Key<>(key, Type.STRING_LIST), null);
                } else {
                    List<String> list = new ArrayList<>(s);
                    for (int j = 0; j < s; j++) {
                        list.add(NetworkTools.readStringUTF8(buf));
                    }
                    args.accept(new Key<>(key, Type.STRING_LIST), list);
                }
            }
            case TYPE_ITEMSTACK_LIST -> {
                int s = buf.readInt();
                if (s == -1) {
                    args.accept(new Key<>(key, Type.ITEMSTACK_LIST), null);
                } else {
                    List<ItemStack> list = new ArrayList<>(s);
                    for (int j = 0; j < s; j++) {
                        list.add(NetworkTools.readItemStack(buf));
                    }
                    args.accept(new Key<>(key, Type.ITEMSTACK_LIST), list);
                }
            }
            case TYPE_POS_LIST -> {
                int s = buf.readInt();
                if (s == -1) {
                    args.accept(new Key<>(key, Type.POS_LIST), null);
                } else {
                    List<BlockPos> list = new ArrayList<>(s);
                    for (int j = 0; j < s; j++) {
                        list.add(buf.readBlockPos());
                    }
                    args.accept(new Key<>(key, Type.POS_LIST), list);
                }
            }
            default -> throw new RuntimeException("Unsupported type for key '" + key + "'!");
        }
    }

    public static void writeArguments(RegistryFriendlyByteBuf buf, TypedMap args) {
        buf.writeInt(args.size());
        for (Key key : args.getKeys()) {
            writeArgument(buf, key, args.get(key));
        }
    }

    public static <T> void writeArgument(RegistryFriendlyByteBuf buf, Key<T> key, T value) {
        buf.writeUtf(key.name());
        ArgumentType argumentType = getArgumentType(key.type());
        buf.writeByte(argumentType.ordinal());
        switch (argumentType) {
            case TYPE_STRING -> NetworkTools.writeStringUTF8(buf, (String) value);
            case TYPE_UUID -> buf.writeUUID((UUID) value);
            case TYPE_INTEGER -> buf.writeInt((Integer) value);
            case TYPE_DIMENSION_TYPE -> {
                ResourceKey<Level> type = (ResourceKey<Level>) value;
                if (type != null) {
                    buf.writeBoolean(true);
                    buf.writeResourceLocation(type.location());
                } else {
                    buf.writeBoolean(false);
                }
            }
            case TYPE_BLOCKPOS -> {
                BlockPos pos = (BlockPos) value;
                if (pos != null) {
                    buf.writeBoolean(true);
                    buf.writeBlockPos(pos);
                } else {
                    buf.writeBoolean(false);
                }
            }
            case TYPE_BOOLEAN -> buf.writeBoolean((Boolean) value);
            case TYPE_DOUBLE -> buf.writeDouble((Double) value);
            case TYPE_FLOAT -> buf.writeFloat((Float) value);
            case TYPE_STACK -> {
                ItemStack stack = (ItemStack) value;
                if (stack != null) {
                    buf.writeBoolean(true);
                    NetworkTools.writeItemStack(buf, stack);
                } else {
                    buf.writeBoolean(false);
                }
            }
            case TYPE_LONG -> buf.writeLong((Long) value);
            case TYPE_STRING_LIST -> {
                List<String> list = (List<String>) value;
                if (list != null) {
                    buf.writeInt(list.size());
                    for (String s : list) {
                        NetworkTools.writeStringUTF8(buf, s);
                    }
                } else {
                    buf.writeInt(-1);
                }
            }
            case TYPE_ITEMSTACK_LIST -> {
                List<ItemStack> list = (List<ItemStack>) value;
                if (list != null) {
                    buf.writeInt(list.size());
                    for (ItemStack s : list) {
                        NetworkTools.writeItemStack(buf, s);
                    }
                } else {
                    buf.writeInt(-1);
                }
            }
            case TYPE_POS_LIST -> {
                List<BlockPos> list = (List<BlockPos>) value;
                if (list != null) {
                    buf.writeInt(list.size());
                    for (BlockPos s : list) {
                        buf.writeBlockPos(s);
                    }
                } else {
                    buf.writeInt(-1);
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
        TYPE_POS_LIST(9),
        TYPE_UUID(10),
        TYPE_DIMENSION_TYPE(11),
        TYPE_FLOAT(12);

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
