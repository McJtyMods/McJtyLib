package mcjty.lib.varia;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

import java.util.Objects;

public class DimensionId {

    private final ResourceKey<Level> id;

    private static final Lazy<DimensionId> OVERWORLD = Lazy.of(() -> new DimensionId(Level.OVERWORLD));
    private static final Lazy<DimensionId> NETHER = Lazy.of(() -> new DimensionId(Level.NETHER));
    private static final Lazy<DimensionId> END = Lazy.of(() -> new DimensionId(Level.END));

    private DimensionId(ResourceKey<Level> id) {
        this.id = id;
    }

    public static DimensionId overworld() {
        return OVERWORLD.get();
    }

    public static DimensionId nether() {
        return NETHER.get();
    }

    public static DimensionId end() {
        return END.get();
    }

    public static DimensionId fromId(ResourceKey<Level> id) {
        return new DimensionId(id);
    }

    public static DimensionId fromPacket(FriendlyByteBuf buf) {
        ResourceKey<Level> key = ResourceKey.create(Registry.DIMENSION_REGISTRY, buf.readResourceLocation());
        return new DimensionId(key);
    }

    public static DimensionId fromWorld(Level world) {
        return new DimensionId(world.dimension());
    }

    public static DimensionId fromResourceLocation(ResourceLocation location) {
        ResourceKey<Level> key = ResourceKey.create(Registry.DIMENSION_REGISTRY, location);
        return new DimensionId(key);
    }

    public ResourceKey<Level> getId() {
        return id;
    }

    public ResourceLocation getRegistryName() {
        return id.location();
    }

    // Is this a good way to get the dimension name?
    public String getName() { return id.location().getPath(); }

    public boolean isOverworld() {
        return id.equals(Level.OVERWORLD);
    }

    public void toBytes(FriendlyByteBuf buf) {
        // @todo use numerical ID
        buf.writeResourceLocation(id.location());
    }

    public ServerLevel loadWorld() {
        // Worlds in 1.16 are always loaded
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server.getLevel(id);
    }

    // Do not load the world if it is not there
    public ServerLevel getWorld() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server.getLevel(id);
    }

    public ServerLevel loadWorld(Level otherWorld) {
        // Worlds in 1.16 are always loaded
        return otherWorld.getServer().getLevel(id);
    }

    public static boolean sameDimension(Level world1, Level world2) {
        return world1.dimension().equals(world2.dimension());
    }

    public boolean sameDimension(Level world) {
        return id.equals(world.dimension());
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
