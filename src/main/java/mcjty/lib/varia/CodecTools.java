package mcjty.lib.varia;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.state.BlockState;

public class CodecTools {

    public static final StreamCodec<FriendlyByteBuf, BlockState> BLOCK_STATE_STREAM_CODEC = new StreamCodec<ByteBuf, BlockState>() {
        public BlockState decode(ByteBuf buf) {
            return FriendlyByteBuf.readBlockPos(buf);
        }

        public void encode(ByteBuf buf, BlockState state) {
            FriendlyByteBuf.writeBlockPos(buf, state);
        }
    };

}
