package mcjty.lib.tileentity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.RedstoneMode;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;
import java.util.UUID;

public record BaseBEData(String ownerName, UUID owner, int securityChannel, RedstoneMode rsMode) {

    public static final Codec<BaseBEData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("ownerName").forGetter(BaseBEData::ownerName),
            UUIDUtil.CODEC.optionalFieldOf("owner").forGetter(d -> Optional.ofNullable(d.owner())),
            Codec.INT.fieldOf("securityChannel").forGetter(BaseBEData::securityChannel),
            RedstoneMode.CODEC.fieldOf("rsMode").forGetter(BaseBEData::rsMode)
    ).apply(instance, (owner, uuid, channel, redstoneMode) -> new BaseBEData(owner, uuid.orElse(null), channel, redstoneMode)));

    public static final StreamCodec<FriendlyByteBuf, BaseBEData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, BaseBEData::ownerName,
            UUIDUtil.STREAM_CODEC, BaseBEData::owner,
            ByteBufCodecs.INT, BaseBEData::securityChannel,
            RedstoneMode.STREAM_CODEC, BaseBEData::rsMode,
            BaseBEData::new
    );

    public BaseBEData withRedstoneMode(RedstoneMode mode) {
        return new BaseBEData(ownerName, owner, securityChannel, mode);
    }

    public BaseBEData withOwner(UUID owner, String ownerName) {
        return new BaseBEData(ownerName, owner, securityChannel, rsMode);
    }

    public BaseBEData withSecurityChannel(int securityChannel) {
        return new BaseBEData(ownerName, owner, securityChannel, rsMode);
    }
}
