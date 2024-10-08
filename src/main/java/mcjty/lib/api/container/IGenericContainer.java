package mcjty.lib.api.container;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;

public interface IGenericContainer {

    void addShortListener(DataSlot holder);

    void addIntegerListener(DataSlot holder);

    void addContainerDataListener(IContainerDataListener dataListener);

    void addDataListener(AttachmentType<?> type, StreamCodec<? extends ByteBuf, ?> codec);

    void setupInventories(@Nullable IItemHandler itemHandler, Inventory inventory);

    AbstractContainerMenu getAsContainer();
}
