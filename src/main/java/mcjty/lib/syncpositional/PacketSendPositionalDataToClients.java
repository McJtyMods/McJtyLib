package mcjty.lib.syncpositional;

import mcjty.lib.McJtyLib;
import mcjty.lib.network.CustomPacketPayload;
import mcjty.lib.network.PlayPayloadContext;
import mcjty.lib.varia.LevelTools;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

/**
 * This packet is used to sync positional data from server to all affected clients
 */
public record PacketSendPositionalDataToClients(GlobalPos pos, IPositionalData data) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(McJtyLib.MODID, "sendpositionaldata");

    public static PacketSendPositionalDataToClients create(FriendlyByteBuf buf) {
        ResourceKey<Level> dimension = LevelTools.getId(buf.readResourceLocation());
        GlobalPos pos = GlobalPos.of(dimension, buf.readBlockPos());
        ResourceLocation id = buf.readResourceLocation();
        IPositionalData data = McJtyLib.SYNCER.create(id, buf);
        return new PacketSendPositionalDataToClients(pos, data);
    }

    public static PacketSendPositionalDataToClients create(GlobalPos pos, IPositionalData value) {
        return new PacketSendPositionalDataToClients(pos, value);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(pos.dimension().location());
        buf.writeBlockPos(pos.pos());
        buf.writeResourceLocation(data.getId());
        data.toBytes(buf);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            McJtyLib.SYNCER.handle(pos, data);
        });
    }
}
