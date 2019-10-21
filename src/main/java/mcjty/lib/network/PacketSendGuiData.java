package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Request information from the tile entity which is needed for the GUI
 */
public class PacketSendGuiData {
    private int dimId;
    private BlockPos pos;
    private Object[] data;

    private static class Entry<T> {
        private final Class<T> clazz;
        private final Function<PacketBuffer, ? extends T> func;
        private final Consumer<Pair<PacketBuffer, ? extends T>> consumer;

        private Entry(Class<T> clazz, Function<PacketBuffer, ? extends T> func, Consumer<Pair<PacketBuffer, ? extends T>> consumer) {
            this.clazz = clazz;
            this.func = func;
            this.consumer = consumer;
        }

        public static <T> Entry<T> of(Class<T> clazz, Function<PacketBuffer, ? extends T> func, Consumer<Pair<PacketBuffer, ? extends T>> consumer) {
            return new Entry<>(clazz, func, consumer);
        }

        public boolean match(Object o) {
            return clazz.isInstance(o);
        }

        public T cast(Object o) {
            return clazz.cast(o);
        }
    }


    private static final Map<Integer,Entry<?>> CLASS_MAP = new HashMap<>();
    static {
        int id = 0;
        CLASS_MAP.put(id++, Entry.of(Integer.class, PacketBuffer::readInt, p -> p.getLeft().writeInt(p.getRight())));
        CLASS_MAP.put(id++, Entry.of(String.class, NetworkTools::readString, p -> NetworkTools.writeString(p.getLeft(), p.getRight())));
        CLASS_MAP.put(id++, Entry.of(Float.class, PacketBuffer::readFloat, p -> p.getLeft().writeFloat(p.getRight())));
        CLASS_MAP.put(id++, Entry.of(Boolean.class, PacketBuffer::readBoolean, p -> p.getLeft().writeBoolean(p.getRight())));
        CLASS_MAP.put(id++, Entry.of(Byte.class, PacketBuffer::readByte, p -> p.getLeft().writeByte(p.getRight())));
        CLASS_MAP.put(id++, Entry.of(Long.class, PacketBuffer::readLong, p -> p.getLeft().writeLong(p.getRight())));
        CLASS_MAP.put(id++, Entry.of(BlockPos.class, PacketBuffer::readBlockPos , p -> p.getLeft().writeBlockPos(p.getRight())));
    }

    public void fromBytes(PacketBuffer buf) {
        dimId = buf.readInt();
        pos = buf.readBlockPos();
        int size = buf.readShort();
        data = new Object[size];
        for (int i = 0 ; i < size ; i++) {
            int type = buf.readByte();
            data[i] = CLASS_MAP.get(type).func.apply(buf);
        }
    }

    private static <T> void acceptCasted(Entry<T> triple, PacketBuffer buf, Object o) {
        triple.consumer.accept(Pair.of(buf, triple.cast(o)));
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(dimId);
        buf.writeBlockPos(pos);
        buf.writeShort(data.length);
        for (Object o : data) {
            boolean ok = false;
            for (Map.Entry<Integer, Entry<?>> entry : CLASS_MAP.entrySet()) {
                Entry<?> triple = entry.getValue();
                if (triple.match(o)) {
                    buf.writeByte(entry.getKey());
                    acceptCasted(triple, buf, o);
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                throw new RuntimeException("Unsupported type in getDataForGUI!");
            }
        }

    }

    public PacketSendGuiData() {
    }

    public PacketSendGuiData(PacketBuffer buf) {
        fromBytes(buf);
    }

    public PacketSendGuiData(World world, BlockPos pos) {
        this.dimId = world.getDimension().getType().getId();
        this.pos = pos;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof GenericTileEntity) {
            GenericTileEntity genericTileEntity = (GenericTileEntity) te;
            data = genericTileEntity.getDataForGUI();
        } else {
            data = new Object[0];
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            World world = McJtyLib.proxy.getClientWorld();
            if (world.getDimension().getType().getId() == dimId) {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof GenericTileEntity) {
                    GenericTileEntity tileEntity = (GenericTileEntity) te;
                    tileEntity.syncDataForGUI(data);
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
