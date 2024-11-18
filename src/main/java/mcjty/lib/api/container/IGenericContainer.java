package mcjty.lib.api.container;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;

public interface IGenericContainer {

    record DataListener<B extends ByteBuf, T>(AttachmentType<T> type, StreamCodec<B, T> streamCodec, Codec<T> codec) {}

    void addShortListener(DataSlot holder);

    void addIntegerListener(DataSlot holder);

    void addContainerDataListener(IContainerDataListener dataListener);

    void addDataListener(DataListener<?, ?> dataListener);

    void setupInventories(@Nullable IItemHandler itemHandler, Inventory inventory);

    AbstractContainerMenu getAsContainer();
}
