package mcjty.lib.varia;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

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
                    scanTagForRead(((CompoundTag) tag).get(key), k, map);
                }
            }
            case Tag.TAG_STRING -> map.put(key, tag.getAsString());
            case Tag.TAG_INT -> map.put(key, ((IntTag) tag).getAsInt());
            case Tag.TAG_BYTE -> map.put(key, ((IntTag) tag).getAsByte());
            case Tag.TAG_SHORT -> map.put(key, ((IntTag) tag).getAsShort());
            case Tag.TAG_LONG -> map.put(key, ((IntTag) tag).getAsLong());
            case Tag.TAG_FLOAT -> map.put(key, ((IntTag) tag).getAsFloat());
            case Tag.TAG_DOUBLE -> map.put(key, ((IntTag) tag).getAsDouble());
        }
    }

    private boolean scanTagForWrite(Tag tag, String key, String name, Object v) {
        if (tag == null) {
            return false;
        }
        switch (tag.getId()) {
            case Tag.TAG_COMPOUND -> {
                for (String k : ((CompoundTag) tag).getAllKeys()) {
                    boolean rc = scanTagForWrite(((CompoundTag) tag).get(key), k, name, v);
                    if (rc) {
                        return true;
                    }
                }
            }
            case Tag.TAG_STRING -> {
                if (key.equals(name)) {
                    ((CompoundTag) tag).putString(key, (String) v);
                    return true;
                }
            }
            case Tag.TAG_INT -> {
                if (key.equals(name)) {
                    ((CompoundTag) tag).putInt(key, (Integer) v);
                    return true;
                }
            }
            case Tag.TAG_BYTE -> {
                if (key.equals(name)) {
                    ((CompoundTag) tag).putByte(key, (Byte) v);
                    return true;
                }
            }
            case Tag.TAG_SHORT -> {
                if (key.equals(name)) {
                    ((CompoundTag) tag).putShort(key, (Short) v);
                    return true;
                }
            }
            case Tag.TAG_LONG -> {
                if (key.equals(name)) {
                    ((CompoundTag) tag).putLong(key, (Long) v);
                    return true;
                }
            }
            case Tag.TAG_FLOAT -> {
                if (key.equals(name)) {
                    ((CompoundTag) tag).putFloat(key, (Float) v);
                    return true;
                }
            }
            case Tag.TAG_DOUBLE -> {
                if (key.equals(name)) {
                    ((CompoundTag) tag).putDouble(key, (Double) v);
                    return true;
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
        scanTagForWrite(tag, "", name, v);
        Pair<T, Tag> resultOut = codec.decode(NbtOps.INSTANCE, tag).getOrThrow();
        return resultOut.getFirst();
    }
}
