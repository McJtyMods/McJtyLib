package mcjty.lib.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public final class StreamCodecs {

    public static <E extends Enum<E>> StreamCodec<FriendlyByteBuf,E> enumCodec(Class<E> type) {
        return StreamCodec.of((buf,value)->buf.writeEnum(value),buf->buf.readEnum(type));
    }

}
