package mcjty.lib.varia;

import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Objects;

public class DimensionId {

    private final DimensionType id;

    private DimensionId(DimensionType id) {
        this.id = id;
    }

    public static DimensionId overworld() {
        return new DimensionId(DimensionType.OVERWORLD);
    }

    public static DimensionId fromId(DimensionType id) {
        return new DimensionId(id);
    }

    public static DimensionId fromPacket(PacketBuffer buf) {
        DimensionType key = DimensionType.getById(buf.readInt());
        return new DimensionId(key);
    }

    public static DimensionId fromWorld(World world) {
        return new DimensionId(world.getDimension().getType());
    }

    public static DimensionId fromResourceLocation(ResourceLocation location) {
        DimensionType key = DimensionType.byName(location);
        return new DimensionId(key);
    }

    public ResourceLocation getRegistryName() {
        return id.getRegistryName();
    }

    public boolean isOverworld() {
        return id.equals(DimensionType.OVERWORLD);
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(id.getId());
    }

    public ServerWorld getWorld() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server.getWorld(id);
    }

    public static boolean sameDimension(World world1, World world2) {
        return world1.getDimension().getType().equals(world2.getDimension().getType());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DimensionId that = (DimensionId) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
