package mcjty.lib.varia;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import mcjty.lib.typed.Type;
import net.minecraft.nbt.*;

import java.util.HashMap;
import java.util.Map;

/**
 * A codec that can be used to encode a value to a map of named values
 */
public class NamedCodec<T> {

    private final Codec<T> codec;
    private final T value;

    private NamedCodec(Codec<T> codec, T value) {
        this.codec = codec;
        this.value = value;
    }

    public static <T> NamedCodec<T> map(Codec<T> codec, T value) {
        return new NamedCodec<>(codec, value);
    }

    private void scanTagForRead(Tag tag, String key, Map<String, Object> map) {
        if (tag == null) {
            return;
        }
        switch (tag.getId()) {
            case Tag.TAG_COMPOUND -> {
                for (String k : ((CompoundTag) tag).getAllKeys()) {
                    scanTagForRead(((CompoundTag) tag).get(k), k, map);
                }
            }
            case Tag.TAG_STRING -> map.put(key, tag.getAsString());
            case Tag.TAG_INT -> map.put(key, ((IntTag) tag).getAsInt());
            case Tag.TAG_BYTE -> map.put(key, ((ByteTag) tag).getAsByte());
            case Tag.TAG_SHORT -> map.put(key, ((ShortTag) tag).getAsShort());
            case Tag.TAG_LONG -> map.put(key, ((LongTag) tag).getAsLong());
            case Tag.TAG_FLOAT -> map.put(key, ((FloatTag) tag).getAsFloat());
            case Tag.TAG_DOUBLE -> map.put(key, ((DoubleTag) tag).getAsDouble());
        }
    }

    private boolean scanTagForWrite(CompoundTag parent, String key, Object v) {
        if (parent.contains(key)) {
            // Easy case, we have a key in our 'parent' already
            Tag tag = parent.get(key);
            switch (tag.getId()) {
                case Tag.TAG_STRING -> {
                    parent.putString(key, v.toString());
                    return true;
                }
                case Tag.TAG_INT -> {
                    parent.putInt(key, convertToInt(v));
                    return true;
                }
                case Tag.TAG_BYTE -> {
                    parent.putByte(key, convertToByte(v));
                    return true;
                }
                case Tag.TAG_SHORT -> {
                    parent.putShort(key, convertToShort(v));
                    return true;
                }
                case Tag.TAG_LONG -> {
                    parent.putLong(key, convertToLong(v));
                    return true;
                }
                case Tag.TAG_FLOAT -> {
                    parent.putFloat(key, convertToFloat(v));
                    return true;
                }
                case Tag.TAG_DOUBLE -> {
                    parent.putDouble(key, convertToDouble(v));
                    return true;
                }
            }
        }
        // There was none, we need to look for other compound tags
        for (String k : parent.getAllKeys()) {
            Tag tag = parent.get(k);
            switch (tag.getId()) {
                case Tag.TAG_COMPOUND -> {
                    if (scanTagForWrite((CompoundTag) tag, key, v)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Object get(String name) {
        DataResult<Tag> result = codec.encodeStart(NbtOps.INSTANCE, value);
        Tag tag = result.getOrThrow();
        Map<String, Object> map = new HashMap<>();
        scanTagForRead(tag, "", map);
        return map.get(name);
    }

    public T set(String name, Object v) {
        DataResult<Tag> result = codec.encodeStart(NbtOps.INSTANCE, value);
        Tag tag = result.getOrThrow();
        scanTagForWrite((CompoundTag) tag, name, v);
        Pair<T, Tag> resultOut = codec.decode(NbtOps.INSTANCE, tag).getOrThrow();
        return resultOut.getFirst();
    }

    private byte convertToByte(Object v) {
        if (v instanceof Byte) {
            return (Byte) v;
        } else if (v instanceof Integer) {
            return ((Integer) v).byteValue();
        } else if (v instanceof Long) {
            return ((Long) v).byteValue();
        } else if (v instanceof Short) {
            return ((Short) v).byteValue();
        } else if (v instanceof Float) {
            return ((Float) v).byteValue();
        } else if (v instanceof Double) {
            return ((Double) v).byteValue();
        } else if (v instanceof Boolean) {
            return (byte) (((Boolean) v) ? 1 : 0);
        } else if (v instanceof String) {
            try {
                return Byte.parseByte((String) v);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Cannot convert " + v + " to byte");
            }
        } else {
            throw new IllegalArgumentException("Cannot convert " + v + " to byte");
        }
    }

    private int convertToInt(Object v) {
        if (v instanceof Byte) {
            return (Byte) v;
        } else if (v instanceof Integer) {
            return (Integer) v;
        } else if (v instanceof Long) {
            return ((Long) v).intValue();
        } else if (v instanceof Short) {
            return (Short) v;
        } else if (v instanceof Float) {
            return (int) (float) v;
        } else if (v instanceof Double) {
            return (int) (double) v;
        } else if (v instanceof Boolean) {
            return ((Boolean) v) ? 1 : 0;
        } else if (v instanceof String) {
            try {
                return Integer.parseInt((String) v);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Cannot convert " + v + " to int");
            }
        } else {
            throw new IllegalArgumentException("Cannot convert " + v + " to int");
        }
    }

    private short convertToShort(Object v) {
        if (v instanceof Byte) {
            return (Byte) v;
        } else if (v instanceof Integer) {
            return ((Integer) v).shortValue();
        } else if (v instanceof Long) {
            return ((Long) v).shortValue();
        } else if (v instanceof Short) {
            return (Short) v;
        } else if (v instanceof Float) {
            return (short) (float) v;
        } else if (v instanceof Double) {
            return (short) (double) v;
        } else if (v instanceof Boolean) {
            return (short) (((Boolean) v) ? 1 : 0);
        } else if (v instanceof String) {
            try {
                return Short.parseShort((String) v);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Cannot convert " + v + " to short");
            }
        } else {
            throw new IllegalArgumentException("Cannot convert " + v + " to short");
        }
    }

    private long convertToLong(Object v) {
        if (v instanceof Byte) {
            return (Byte) v;
        } else if (v instanceof Integer) {
            return (Integer) v;
        } else if (v instanceof Long) {
            return (Long) v;
        } else if (v instanceof Short) {
            return (Short) v;
        } else if (v instanceof Float) {
            return (long) (float) v;
        } else if (v instanceof Double) {
            return (long) (double) v;
        } else if (v instanceof Boolean) {
            return ((Boolean) v) ? 1 : 0;
        } else if (v instanceof String) {
            try {
                return Long.parseLong((String) v);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Cannot convert " + v + " to long");
            }
        } else {
            throw new IllegalArgumentException("Cannot convert " + v + " to long");
        }
    }

    private float convertToFloat(Object v) {
        if (v instanceof Byte) {
            return (Byte) v;
        } else if (v instanceof Integer) {
            return (Integer) v;
        } else if (v instanceof Long) {
            return (Long) v;
        } else if (v instanceof Short) {
            return (Short) v;
        } else if (v instanceof Float) {
            return (Float) v;
        } else if (v instanceof Double) {
            return (float) (double) v;
        } else if (v instanceof Boolean) {
            return ((Boolean) v) ? 1 : 0;
        } else if (v instanceof String) {
            try {
                return Float.parseFloat((String) v);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Cannot convert " + v + " to float");
            }
        } else {
            throw new IllegalArgumentException("Cannot convert " + v + " to float");
        }
    }

    private double convertToDouble(Object v) {
        if (v instanceof Byte) {
            return (Byte) v;
        } else if (v instanceof Integer) {
            return (Integer) v;
        } else if (v instanceof Long) {
            return (Long) v;
        } else if (v instanceof Short) {
            return (Short) v;
        } else if (v instanceof Float) {
            return (Float) v;
        } else if (v instanceof Double) {
            return (Double) v;
        } else if (v instanceof Boolean) {
            return ((Boolean) v) ? 1 : 0;
        } else if (v instanceof String) {
            try {
                return Double.parseDouble((String) v);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Cannot convert " + v + " to double");
            }
        } else {
            throw new IllegalArgumentException("Cannot convert " + v + " to double");
        }
    }

    public Type<?> getType(String attributeName) {
        DataResult<Tag> result = codec.encodeStart(NbtOps.INSTANCE, value);
        Tag tag = result.getOrThrow();
        return scanTagForType((CompoundTag) tag, attributeName);
    }

    private Type<?> scanTagForType(CompoundTag tag, String key) {
        if (tag == null) {
            return Type.OBJECT;
        }
        if (tag.contains(key)) {
            // Easy case, we have a key in our 'parent' already
            Tag subTag = tag.get(key);
            switch (subTag.getId()) {
                case Tag.TAG_STRING -> {
                    return Type.STRING;
                }
                case Tag.TAG_INT, Tag.TAG_BYTE, Tag.TAG_SHORT -> {
                    return Type.INTEGER;
                }
                case Tag.TAG_LONG -> {
                    return Type.LONG;
                }
                case Tag.TAG_FLOAT -> {
                    return Type.FLOAT;
                }
                case Tag.TAG_DOUBLE -> {
                    return Type.DOUBLE;
                }
            }
        }

        // There was none, we need to look for other compound tags
        for (String k : tag.getAllKeys()) {
            Tag subTag = tag.get(k);
            switch (subTag.getId()) {
                case Tag.TAG_COMPOUND -> {
                    return scanTagForType((CompoundTag) subTag, key);
                }
            }
        }
        return Type.OBJECT;
    }

}