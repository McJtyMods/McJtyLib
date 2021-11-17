package mcjty.lib.typed;

import mcjty.lib.bindings.IValue;
import mcjty.lib.network.NetworkTools;
import mcjty.lib.varia.LevelTools;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A Type object represents a given type.
 */
public final class Type<V> {

    // Basic
    public static final Type<Integer> INTEGER = create(Integer.class, (v, buf) -> buf.writeInt(v), PacketBuffer::readInt);
    public static final Type<Double> DOUBLE = create(Double.class, (v, buf) -> buf.writeDouble(v), PacketBuffer::readDouble);
    public static final Type<Long> LONG = create(Long.class, (v, buf) -> buf.writeLong(v), PacketBuffer::readLong);
    public static final Type<String> STRING = create(String.class, (v, buf) -> buf.writeUtf(v), buf -> buf.readUtf(32767));
    public static final Type<UUID> UUID = create(UUID.class, (v, buf) -> buf.writeUUID(v), PacketBuffer::readUUID);
    public static final Type<Boolean> BOOLEAN = create(Boolean.class, (v, buf) -> buf.writeBoolean(v), PacketBuffer::readBoolean);
    public static final Type<BlockPos> BLOCKPOS = create(BlockPos.class, (v, buf) -> buf.writeBlockPos(v), PacketBuffer::readBlockPos);
    public static final Type<ItemStack> ITEMSTACK = create(ItemStack.class, (v, buf) -> buf.writeItem(v), PacketBuffer::readItem);
    public static final Type<RegistryKey<World>> DIMENSION_TYPE = create(RegistryKey.class, (v, buf) -> buf.writeResourceLocation(v.location()), buf -> LevelTools.getId(buf.readResourceLocation()));

    public static final Type<List<String>> STRING_LIST = create(List.class, (v, buf) -> NetworkTools.writeStringList(buf, v), NetworkTools::readStringList);
    public static final Type<List<ItemStack>> ITEMSTACK_LIST = create(List.class, (v, buf) -> NetworkTools.writeItemStackList(buf, v), NetworkTools::readItemStackList);
    public static final Type<List<BlockPos>> POS_LIST = create(List.class, (v, buf) -> NetworkTools.writeBlockPosList(buf, v), NetworkTools::readBlockPosList);

    @Nonnull private final Class<V> type;
    @Nullable private final BiConsumer<V, PacketBuffer> serializer;
    @Nullable private final Function<PacketBuffer, V> deserializer;

    private Type(@Nonnull final Class<V> type, @Nullable BiConsumer<V, PacketBuffer> serializer, @Nullable Function<PacketBuffer, V> deserializer) {
        this.type = type;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    @Nonnull
    public static <V> Type<V> create(@Nonnull final Class<? super V> type, BiConsumer<V, PacketBuffer> serializer, Function<PacketBuffer, V> deserializer) {
        return new Type<>((Class<V>) type, serializer, deserializer);
    }

    @Nonnull
    public static <V> Type<V> create(@Nonnull final Class<? super V> type) {
        return new Type<>((Class<V>) type, null, null);
    }

    @Nonnull
    public Class<V> getType() {
        return type;
    }

    public void serialize(PacketBuffer buf, Object value) {
        serializer.accept((V) value, buf);
    }

    public void deserialize(PacketBuffer buf, IValue<V> value) {
        V v = deserializer.apply(buf);
        value.setter().accept(v);
    }

    @Nullable
    public BiConsumer<V, PacketBuffer> getSerializer() {
        return serializer;
    }

    @Nullable
    public Function<PacketBuffer, V> getDeserializer() {
        return deserializer;
    }

    public boolean isA(Object b) {
        return type.isInstance(b);
    }

    @Nonnull
    public List<V> convert(@Nonnull List<?> list) {
        for(Object o : list) {
            if(o != null && !type.isInstance(o)) {
                throw new ClassCastException("Cannot cast List<? super " + o.getClass().getName() + "> to List<" + type.getName() + ">");
            }
        }
        return (List<V>) list;
    }

    public V convert(Object o) {
        return type.cast(o);
    }

    @Override
    public String toString() {
        return "Type(" + getType().getSimpleName() + ')';
    }
}
