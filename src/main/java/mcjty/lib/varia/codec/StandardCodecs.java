package mcjty.lib.varia.codec;

import com.mojang.datafixers.util.Either;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.FriendlyByteBuf;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.IntFunction;

public interface StandardCodecs {

    StreamCodec<FriendlyByteBuf, Boolean> BOOL = new StreamCodec<>() {
        public Boolean decode(FriendlyByteBuf buf) {
            return buf.readBoolean();
        }
        public void encode(FriendlyByteBuf buf, Boolean val) {
            buf.writeBoolean(val);
        }
    };

    StreamCodec<FriendlyByteBuf, Byte> BYTE = new StreamCodec<>() {
        public Byte decode(FriendlyByteBuf buf) {
            return buf.readByte();
        }
        public void encode(FriendlyByteBuf buf, Byte val) {
            buf.writeByte(val);
        }
    };

    StreamCodec<FriendlyByteBuf, Short> SHORT = new StreamCodec<>() {
        public Short decode(FriendlyByteBuf buf) {
            return buf.readShort();
        }
        public void encode(FriendlyByteBuf buf, Short val) {
            buf.writeShort(val);
        }
    };

    StreamCodec<FriendlyByteBuf, Integer> UNSIGNED_SHORT = new StreamCodec<>() {
        public Integer decode(FriendlyByteBuf buf) {
            return buf.readUnsignedShort();
        }
        public void encode(FriendlyByteBuf buf, Integer val) {
            buf.writeShort(val);
        }
    };

    StreamCodec<FriendlyByteBuf, Integer> INT = new StreamCodec<>() {
        public Integer decode(FriendlyByteBuf buf) {
            return buf.readInt();
        }
        public void encode(FriendlyByteBuf buf, Integer val) {
            buf.writeInt(val);
        }
    };

    StreamCodec<FriendlyByteBuf, Float> FLOAT = new StreamCodec<>() {
        public Float decode(FriendlyByteBuf buf) {
            return buf.readFloat();
        }
        public void encode(FriendlyByteBuf buf, Float val) {
            buf.writeFloat(val);
        }
    };

    StreamCodec<FriendlyByteBuf, Double> DOUBLE = new StreamCodec<>() {
        public Double decode(FriendlyByteBuf buf) {
            return buf.readDouble();
        }
        public void encode(FriendlyByteBuf buf, Double val) {
            buf.writeDouble(val);
        }
    };

    StreamCodec<FriendlyByteBuf, byte[]> BYTE_ARRAY = new StreamCodec<>() {
        public byte[] decode(FriendlyByteBuf buffer) {
            return buffer.readByteArray();
        }
        public void encode(FriendlyByteBuf buffer, byte[] value) {
            buffer.writeByteArray(value);
        }
    };

    StreamCodec<FriendlyByteBuf, short[]> SHORT_ARRAY = new StreamCodec<>() {
        public short[] decode(FriendlyByteBuf buffer) {
            int cnt = buffer.readVarInt();
            if (cnt <= 0) {
                return new short[0];
            } else {
                short[] value = new short[cnt];
                for (int i = 0; i < cnt; i++) {
                    value[i] = buffer.readShort();
                }
                return value;
            }
        }
        public void encode(FriendlyByteBuf buffer, short[] value) {
            buffer.writeVarInt(value.length);
            for (short v : value) {
                buffer.writeShort(v);
            }
        }
    };

    StreamCodec<FriendlyByteBuf, Vector3f> VECTOR3F = new StreamCodec<>() {
        public Vector3f decode(FriendlyByteBuf buf) {
            return buf.readVector3f();
        }

        public void encode(FriendlyByteBuf buf, Vector3f value) {
            buf.writeVector3f(value);
        }
    };

    static StreamCodec<FriendlyByteBuf, byte[]> byteArray(final int maxSize) {
        return new StreamCodec<>() {
            public byte[] decode(FriendlyByteBuf buf) {
                return buf.readByteArray(maxSize);
            }

            public void encode(FriendlyByteBuf buf, byte[] array) {
                if (array.length > maxSize) {
                    throw new EncoderException("ByteArray with size " + array.length + " is bigger than allowed " + maxSize);
                } else {
                    buf.writeByteArray(array);
                }
            }
        };
    }

    StreamCodec<FriendlyByteBuf, String> STRING_UTF8 = stringUtf8(32767);
    static StreamCodec<FriendlyByteBuf, String> stringUtf8(final int maxLength) {
        return new StreamCodec<>() {
            public String decode(FriendlyByteBuf buf) {
                return buf.readUtf(maxLength);
            }

            public void encode(FriendlyByteBuf buf, String str) {
                buf.writeUtf(str, maxLength);
            }
        };
    }

    static <B extends FriendlyByteBuf, V> StreamCodec<B, Optional<V>> optional(final StreamCodec<B, V> codec) {
        return new StreamCodec<>() {
            public Optional<V> decode(B buf) {
                return buf.readBoolean() ? Optional.of(codec.decode(buf)) : Optional.empty();
            }

            public void encode(B buf, Optional<V> val) {
                if (val.isPresent()) {
                    buf.writeBoolean(true);
                    codec.encode(buf, val.get());
                } else {
                    buf.writeBoolean(false);
                }

            }
        };
    }

    static <B extends FriendlyByteBuf, V, C extends Collection<V>> StreamCodec<B, C> collection(IntFunction<C> factory, StreamCodec<? super B, V> codec) {
        return collection(factory, codec, Integer.MAX_VALUE);
    }

    static <B extends FriendlyByteBuf, V, C extends Collection<V>> StreamCodec<B, C> collection(final IntFunction<C> factory, final StreamCodec<? super B, V> codec, final int maxSize) {
        return new StreamCodec<>() {
            public C decode(B buf) {
                int size = buf.readInt();
                C c = factory.apply(Math.min(size, 65536));
                for (int i = 0; i < size; ++i) {
                    c.add(codec.decode(buf));
                }

                return c;
            }

            public void encode(B buf, C collection) {
                buf.writeInt(collection.size());
                Iterator it = collection.iterator();
                while (it.hasNext()) {
                    V v = (V) it.next();
                    codec.encode(buf, v);
                }

            }
        };
    }

    static <B extends FriendlyByteBuf, V, C extends Collection<V>> StreamCodec.CodecOperation<B, V, C> collection(IntFunction<C> factory) {
        return (buf) -> collection(factory, buf);
    }

    static <B extends FriendlyByteBuf, V> StreamCodec.CodecOperation<B, V, List<V>> list() {
        return (buf) -> collection(ArrayList::new, buf);
    }

    static <B extends FriendlyByteBuf, V> StreamCodec.CodecOperation<B, V, List<V>> list(int maxSize) {
        return (buf) -> collection(ArrayList::new, buf, maxSize);
    }

    static <B extends FriendlyByteBuf, L, R> StreamCodec<B, Either<L, R>> either(final StreamCodec<? super B, L> leftCodec, final StreamCodec<? super B, R> rightCodec) {
        return new StreamCodec<>() {
            public Either<L, R> decode(B buf) {
                return buf.readBoolean() ? Either.left(leftCodec.decode(buf)) : Either.right(rightCodec.decode(buf));
            }

            public void encode(B buf, Either<L, R> val) {
                val.ifLeft((left) -> {
                    buf.writeBoolean(true);
                    leftCodec.encode(buf, left);
                }).ifRight((right) -> {
                    buf.writeBoolean(false);
                    rightCodec.encode(buf, right);
                });
            }
        };
    }
}
