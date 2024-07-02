package mcjty.lib.varia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

import java.util.Optional;

public class CodecTools {

    public static final StreamCodec<FriendlyByteBuf, Optional<BlockPos>> OPTIONAL_BLOCKPOS = StreamCodec.of(
            (buf, blockPos) -> {
                if (blockPos.isPresent()) {
                    buf.writeBoolean(true);
                    buf.writeBlockPos(blockPos.get());
                } else {
                    buf.writeBoolean(false);
                }
            },
            buf -> {
                if (buf.readBoolean()) {
                    return Optional.of(buf.readBlockPos());
                } else {
                    return Optional.empty();
                }
            }
    );

    public static <T> StreamCodec<RegistryFriendlyByteBuf, Optional<ResourceKey<T>>> optionalResourceKeyStreamCodec(ResourceKey<? extends Registry<T>> registry) {
        return StreamCodec.of(
                (buf, key) -> {
                    if (key.isPresent()) {
                        buf.writeBoolean(true);
                        buf.writeResourceLocation(key.get().location());
                    } else {
                        buf.writeBoolean(false);
                    }
                },
                buf -> {
                    if (buf.readBoolean()) {
                        return Optional.of(ResourceKey.create(registry, buf.readResourceLocation()));
                    } else {
                        return Optional.empty();
                    }
                }
        );
    }

}
