package mcjty.lib.varia;

import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Objects;

public class DimensionId {

    private final RegistryKey<World> id;

    public DimensionId(RegistryKey<World> id) {
        this.id = id;
    }

    public DimensionId(PacketBuffer buf) {
        id = RegistryKey.func_240903_a_(Registry.WORLD_KEY, buf.readResourceLocation());
    }

    public ResourceLocation getRegistryName() {
        return id.getRegistryName();
    }

    public void toBytes(PacketBuffer buf) {
        // @todo use numerical ID
        buf.writeResourceLocation(id.getRegistryName());
    }

    public ServerWorld getWorld() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server.getWorld(id);
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
