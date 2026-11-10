package mcjty.lib.container;

import io.netty.buffer.Unpooled;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;

public final class MenuTools {

    public static final StreamCodec<RegistryFriendlyByteBuf, RegistryFriendlyByteBuf> CODEC =
            StreamCodec.of(
                    (buf, data) -> {
                        byte[] bytes = new byte[data.readableBytes()];
                        data.getBytes(data.readerIndex(), bytes);
                        buf.writeByteArray(bytes);
                    },
                    buf ->
                            new RegistryFriendlyByteBuf(
                                    Unpooled.wrappedBuffer(buf.readByteArray(1048576)),
                                    buf.registryAccess()));

    public static void open(
            Player player, MenuProvider provider, Consumer<RegistryFriendlyByteBuf> extra) {

        player.openMenu(
                new ExtendedScreenHandlerFactory<RegistryFriendlyByteBuf>() {

                    public RegistryFriendlyByteBuf getScreenOpeningData(ServerPlayer serverPlayer) {
                        var data =
                                new RegistryFriendlyByteBuf(
                                        Unpooled.buffer(), serverPlayer.registryAccess());
                        extra.accept(data);
                        return data;
                    }

                    public Component getDisplayName() {
                        return provider.getDisplayName();
                    }

                    public AbstractContainerMenu createMenu(
                            int id, Inventory inventory, Player owner) {
                        return provider.createMenu(id, inventory, owner);
                    }
                });
    }
}
