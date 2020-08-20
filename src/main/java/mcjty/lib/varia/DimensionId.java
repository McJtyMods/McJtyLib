package mcjty.lib.varia;

import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Objects;

public class DimensionId {

    private final RegistryKey<World> id;

    private final static Lazy<DimensionId> OVERWORLD = Lazy.of(() -> new DimensionId(World.field_234918_g_));

    private DimensionId(RegistryKey<World> id) {
        this.id = id;
    }

    public static DimensionId overworld() {
        return OVERWORLD.get();
    }

    public static DimensionId fromId(RegistryKey<World> id) {
        return new DimensionId(id);
    }

    public static DimensionId fromPacket(PacketBuffer buf) {
        RegistryKey<World> key = RegistryKey.func_240903_a_(Registry.WORLD_KEY, buf.readResourceLocation());
        return new DimensionId(key);
    }

    public static DimensionId fromWorld(World world) {
        return new DimensionId(world.func_234923_W_());
    }

    public static DimensionId fromResourceLocation(ResourceLocation location) {
        RegistryKey<World> key = RegistryKey.func_240903_a_(Registry.WORLD_KEY, location);
        return new DimensionId(key);
    }

    public ResourceLocation getRegistryName() {
        return id.getRegistryName();
    }

    // Is this a good way to get the dimension name?
    public String getName() { return id.getRegistryName().getPath(); }

    public boolean isOverworld() {
        return id.equals(World.field_234918_g_);
    }

    public void toBytes(PacketBuffer buf) {
        // @todo use numerical ID
        buf.writeResourceLocation(id.getRegistryName());
    }

    public ServerWorld loadWorld() {
        // Worlds in 1.16 are always loaded
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server.getWorld(id);
    }

    // Do not load the world if it is not there
    public ServerWorld getWorld() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server.getWorld(id);
    }

    public ServerWorld loadWorld(World otherWorld) {
        // Worlds in 1.16 are always loaded
        return otherWorld.getServer().getWorld(id);
    }

    public static boolean sameDimension(World world1, World world2) {
        return world1.func_234923_W_().equals(world2.func_234923_W_());
    }

    public boolean sameDimension(World world) {
        return id.equals(world.func_234923_W_());
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
