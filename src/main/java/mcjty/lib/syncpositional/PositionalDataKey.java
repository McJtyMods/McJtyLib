package mcjty.lib.syncpositional;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.GlobalPos;

import java.util.Objects;

/**
 * Package private, this key is not useful outside this package
 */
class PositionalDataKey {

    private final ResourceLocation id;
    private final GlobalPos pos;

    public PositionalDataKey(ResourceLocation id, GlobalPos pos) {
        this.id = id;
        this.pos = pos;
    }

    public ResourceLocation getId() {
        return id;
    }

    public GlobalPos getPos() {
        return pos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositionalDataKey dataKey = (PositionalDataKey) o;
        return Objects.equals(id, dataKey.id) && Objects.equals(pos, dataKey.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pos);
    }
}
