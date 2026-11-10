package mcjty.lib.client;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.client.renderer.RenderType;

public record ChunkRenderTypeSet(Set<RenderType> types) implements Iterable<RenderType> {

    public static ChunkRenderTypeSet of(RenderType... types) {
        return new ChunkRenderTypeSet(Set.of(types));
    }

    public static ChunkRenderTypeSet union(ChunkRenderTypeSet first, ChunkRenderTypeSet second) {
        Set<RenderType> types = new LinkedHashSet<>(first.types);
        types.addAll(second.types);
        return new ChunkRenderTypeSet(Set.copyOf(types));
    }

    public boolean contains(RenderType type) {
        return types.contains(type);
    }

    @Override
    public Iterator<RenderType> iterator() {
        return types.iterator();
    }
}
