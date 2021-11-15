package mcjty.lib.syncpositional;

import mcjty.lib.McJtyLib;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Use this class if you want to sync data from the server to the client based on position (i.e.
 * clients watching a chunk)
 */
public class PositionalDataSyncer {

    private final Map<ResourceLocation, Function<PacketBuffer, IPositionalData>> factories = new HashMap<>();
    private final Map<ResourceLocation, BiConsumer<GlobalPos, IPositionalData>> clientHandlers = new HashMap<>();
    private final Map<ChunkPos, Map<PositionalDataKey, Runnable>> watchHandlers = Collections.synchronizedMap(new HashMap<>());

    private final Map<PositionalDataKey, IPositionalData> syncTodo = new HashMap<>();

    private int timeout = 0;

    /**
     * Register a factory to create positional data. Call this in FMLCommonSetup
     */
    public void registerPositionalDataFactory(ResourceLocation id, Function<PacketBuffer, IPositionalData> factory) {
        factories.put(id, factory);
    }

    /**
     * Register a client side handler for receiving data. Call this is FMLClientSetup
     */
    public void registerClientHandler(ResourceLocation id, BiConsumer<GlobalPos, IPositionalData> handler) {
        clientHandlers.put(id, handler);
    }

    /**
     * Register a watch handler for a certain position. The given code will be executed whenever a new
     * entity watches the chunk that the give pos belongs too. This is usually called from within the
     * tile entity setup and should be unregistered when the tile entity is invalidated
     */
    public void registerWatchHandler(ResourceLocation id, GlobalPos pos, Runnable handler) {
        ChunkPos cp = new ChunkPos(pos.pos());
        Map<PositionalDataKey, Runnable> runnableMap = watchHandlers.computeIfAbsent(cp, chunkPos -> new HashMap<>());
        PositionalDataKey dataKey = new PositionalDataKey(id, pos);
        runnableMap.put(dataKey, handler);
    }

    public void unregisterWatchHandler(ResourceLocation id, GlobalPos pos) {
        ChunkPos cp = new ChunkPos(pos.pos());
        Map<PositionalDataKey, Runnable> runnableMap = watchHandlers.get(cp);
        if (runnableMap != null) {
            runnableMap.remove(new PositionalDataKey(id, pos));
        }
    }

    /**
     * Create a positional data element from a packet
     */
    @Nullable
    public IPositionalData create(ResourceLocation id, PacketBuffer buf) {
        return factories.getOrDefault(id, b -> null).apply(buf);
    }

    /**
     * Handle data on the client
     */
    public <T extends IPositionalData> void handle(GlobalPos pos, T data) {
        clientHandlers.getOrDefault(data.getId(), (p,d) -> {}).accept(pos, data);
    }

    /**
     * Publish new data for the given position
     */
    public void publish(World world, BlockPos pos, IPositionalData data) {
        PositionalDataKey key = new PositionalDataKey(data.getId(), GlobalPos.of(world.dimension(), pos));
        syncTodo.put(key, data);
    }

    /**
     * Forget any possible data for a certain type that may be stored with the given position
     */
    public void forget(World world, BlockPos pos, ResourceLocation id) {
        PositionalDataKey key = new PositionalDataKey(id, GlobalPos.of(world.dimension(), pos));
        syncTodo.remove(key);
    }

    /**
     * Send out all pending data
     */
    public void sendOutData(MinecraftServer server) {
        timeout--;
        if (timeout < 0) {
            timeout = 10;   // @todo configurable
            for (Map.Entry<PositionalDataKey, IPositionalData> entry : syncTodo.entrySet()) {
                GlobalPos pos = entry.getKey().getPos();
                McJtyLib.networkHandler.send(PacketDistributor.TRACKING_CHUNK.with(() -> {
                    ServerWorld level = server.getLevel(pos.dimension());
                    return (Chunk)(level.getChunk(pos.pos()));
                }), new PacketSendPositionalDataToClients(pos, entry.getValue()));
            }
            syncTodo.clear();
        }
    }

    /**
     * A new player arrived. Check if any watch handlers need to be modified
     */
    public void startWatching(ServerPlayerEntity player) {
        BlockPos blockPos = player.blockPosition();
        ChunkPos cp = new ChunkPos(blockPos);
        Map<PositionalDataKey, Runnable> runnableMap = watchHandlers.get(cp);
        if (runnableMap != null) {
            GlobalPos pos = GlobalPos.of(player.level.dimension(), blockPos);
            for (Map.Entry<PositionalDataKey, Runnable> entry : runnableMap.entrySet()) {
                if (pos.equals(entry.getKey().getPos())) {
                    entry.getValue().run();
                }
            }
        }
    }
}
