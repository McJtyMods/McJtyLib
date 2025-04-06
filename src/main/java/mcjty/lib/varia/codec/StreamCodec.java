package mcjty.lib.varia.codec;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Function6;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;


public interface StreamCodec<B, V> extends StreamDecoder<B, V>, StreamEncoder<B, V> {
    static <B, V> StreamCodec<B, V> of(final StreamEncoder<B, V> encoder, final StreamDecoder<B, V> decoder) {
        return new StreamCodec<>() {
            public V decode(B buf) {
                return decoder.decode(buf);
            }

            public void encode(B buf, V val) {
                encoder.encode(buf, val);
            }
        };
    }

    static <B, V> StreamCodec<B, V> ofMember(final StreamMemberEncoder<B, V> encoder, final StreamDecoder<B, V> decoder) {
        return new StreamCodec<>() {
            public V decode(B buf) {
                return decoder.decode(buf);
            }

            public void encode(B buf, V val) {
                encoder.encode(val, buf);
            }
        };
    }

    static <B, V> StreamCodec<B, V> unit(final V expectedValue) {
        return new StreamCodec<>() {
            public V decode(B buf) {
                return expectedValue;
            }

            public void encode(B buf, V val) {
                if (!val.equals(expectedValue)) {
                    throw new IllegalStateException("Can't encode '" + val + "', expected '" + expectedValue + "'");
                }
            }
        };
    }

    default <O> StreamCodec<B, O> apply(CodecOperation<B, V, O> operation) {
        return operation.apply(this);
    }

    default <O> StreamCodec<B, O> map(final Function<? super V, ? extends O> factory, final Function<? super O, ? extends V> getter) {
        return new StreamCodec<>() {
            public O decode(B buf) {
                return factory.apply(StreamCodec.this.decode(buf));
            }

            public void encode(B buf, O val) {
                StreamCodec.this.encode(buf, getter.apply(val));
            }
        };
    }

    default <O extends FriendlyByteBuf> StreamCodec<O, V> mapStream(final Function<O, ? extends B> bufferFactory) {
        return new StreamCodec<>() {
            public V decode(O buf) {
                B b = bufferFactory.apply(buf);
                return StreamCodec.this.decode(b);
            }

            public void encode(O buf, V val) {
                B b = bufferFactory.apply(buf);
                StreamCodec.this.encode(b, val);
            }
        };
    }

    default <U> StreamCodec<B, U> dispatch(final Function<? super U, ? extends V> keyGetter, final Function<? super V, ? extends StreamCodec<? super B, ? extends U>> codecGetter) {
        return new StreamCodec<>() {
            public U decode(B buf) {
                V v = StreamCodec.this.decode(buf);
                StreamCodec<? super B, ? extends U> streamcodec = codecGetter.apply(v);
                return streamcodec.decode(buf);
            }

            public void encode(B buf, U val) {
                V v = keyGetter.apply(val);
                StreamCodec<B, U> streamcodec = (StreamCodec) codecGetter.apply(v);
                StreamCodec.this.encode(buf, v);
                streamcodec.encode(buf, val);
            }
        };
    }

    static <B, C, T1> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> codec, final Function<C, T1> getter, final Function<T1, C> factory) {
        return new StreamCodec<>() {
            public C decode(B buf) {
                T1 t1 = codec.decode(buf);
                return factory.apply(t1);
            }

            public void encode(B buf, C val) {
                codec.encode(buf, getter.apply(val));
            }
        };
    }

    static <B, C, T1, T2> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> codec1, final Function<C, T1> getter1, final StreamCodec<? super B, T2> codec2, final Function<C, T2> getter2, final BiFunction<T1, T2, C> factory) {
        return new StreamCodec<>() {
            public C decode(B buf) {
                T1 t1 = codec1.decode(buf);
                T2 t2 = codec2.decode(buf);
                return factory.apply(t1, t2);
            }

            public void encode(B buf, C val) {
                codec1.encode(buf, getter1.apply(val));
                codec2.encode(buf, getter2.apply(val));
            }
        };
    }

    static <B, C, T1, T2, T3> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> codec1, final Function<C, T1> getter1, final StreamCodec<? super B, T2> codec2, final Function<C, T2> getter2, final StreamCodec<? super B, T3> codec3, final Function<C, T3> getter3, final Function3<T1, T2, T3, C> factory) {
        return new StreamCodec<>() {
            public C decode(B buf) {
                T1 t1 = codec1.decode(buf);
                T2 t2 = codec2.decode(buf);
                T3 t3 = codec3.decode(buf);
                return factory.apply(t1, t2, t3);
            }

            public void encode(B buf, C val) {
                codec1.encode(buf, getter1.apply(val));
                codec2.encode(buf, getter2.apply(val));
                codec3.encode(buf, getter3.apply(val));
            }
        };
    }

    static <B, C, T1, T2, T3, T4> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> codec1, final Function<C, T1> getter1, final StreamCodec<? super B, T2> codec2, final Function<C, T2> getter2, final StreamCodec<? super B, T3> codec3, final Function<C, T3> getter3, final StreamCodec<? super B, T4> codec4, final Function<C, T4> getter4, final Function4<T1, T2, T3, T4, C> factory) {
        return new StreamCodec<>() {
            public C decode(B buf) {
                T1 t1 = codec1.decode(buf);
                T2 t2 = codec2.decode(buf);
                T3 t3 = codec3.decode(buf);
                T4 t4 = codec4.decode(buf);
                return factory.apply(t1, t2, t3, t4);
            }

            public void encode(B buf, C val) {
                codec1.encode(buf, getter1.apply(val));
                codec2.encode(buf, getter2.apply(val));
                codec3.encode(buf, getter3.apply(val));
                codec4.encode(buf, getter4.apply(val));
            }
        };
    }

    static <B, C, T1, T2, T3, T4, T5> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> codec1, final Function<C, T1> getter1, final StreamCodec<? super B, T2> codec2, final Function<C, T2> getter2, final StreamCodec<? super B, T3> codec3, final Function<C, T3> getter3, final StreamCodec<? super B, T4> codec4, final Function<C, T4> getter4, final StreamCodec<? super B, T5> codec5, final Function<C, T5> getter5, final Function5<T1, T2, T3, T4, T5, C> factory) {
        return new StreamCodec<>() {
            public C decode(B buf) {
                T1 t1 = codec1.decode(buf);
                T2 t2 = codec2.decode(buf);
                T3 t3 = codec3.decode(buf);
                T4 t4 = codec4.decode(buf);
                T5 t5 = codec5.decode(buf);
                return factory.apply(t1, t2, t3, t4, t5);
            }

            public void encode(B buf, C val) {
                codec1.encode(buf, getter1.apply(val));
                codec2.encode(buf, getter2.apply(val));
                codec3.encode(buf, getter3.apply(val));
                codec4.encode(buf, getter4.apply(val));
                codec5.encode(buf, getter5.apply(val));
            }
        };
    }

    static <B, C, T1, T2, T3, T4, T5, T6> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> codec1, final Function<C, T1> getter1, final StreamCodec<? super B, T2> codec2, final Function<C, T2> getter2, final StreamCodec<? super B, T3> codec3, final Function<C, T3> getter3, final StreamCodec<? super B, T4> codec4, final Function<C, T4> getter4, final StreamCodec<? super B, T5> codec5, final Function<C, T5> getter5, final StreamCodec<? super B, T6> codec6, final Function<C, T6> getter6, final Function6<T1, T2, T3, T4, T5, T6, C> factory) {
        return new StreamCodec<>() {
            public C decode(B buf) {
                T1 t1 = codec1.decode(buf);
                T2 t2 = codec2.decode(buf);
                T3 t3 = codec3.decode(buf);
                T4 t4 = codec4.decode(buf);
                T5 t5 = codec5.decode(buf);
                T6 t6 = codec6.decode(buf);
                return factory.apply(t1, t2, t3, t4, t5, t6);
            }

            public void encode(B buf, C val) {
                codec1.encode(buf, getter1.apply(val));
                codec2.encode(buf, getter2.apply(val));
                codec3.encode(buf, getter3.apply(val));
                codec4.encode(buf, getter4.apply(val));
                codec5.encode(buf, getter5.apply(val));
                codec6.encode(buf, getter6.apply(val));
            }
        };
    }

    static <B, T> StreamCodec<B, T> recursive(final UnaryOperator<StreamCodec<B, T>> modifier) {
        return new StreamCodec<>() {
            private final Supplier<StreamCodec<B, T>> inner = Suppliers.memoize(() -> modifier.apply(this));

            public T decode(B buf) {
                return (T) ((StreamCodec) this.inner.get()).decode(buf);
            }

            public void encode(B buf, T val) {
                this.inner.get().encode(buf, val);
            }
        };
    }

    default <S extends B> StreamCodec<S, V> cast() {
        return (StreamCodec<S, V>) this;
    }

    @FunctionalInterface
    interface CodecOperation<B, S, T> {
        StreamCodec<B, T> apply(StreamCodec<B, S> var1);
    }

}
