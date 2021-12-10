package mcjty.lib.multipart;

import net.minecraft.core.BlockPos;

import javax.annotation.Nonnull;
import java.util.Objects;

public class PartPos {
    @Nonnull private final BlockPos pos;
    @Nonnull private final PartSlot slot;

    public PartPos(@Nonnull BlockPos pos, @Nonnull PartSlot slot) {
        this.pos = pos;
        this.slot = slot;
    }

    public static PartPos create(@Nonnull BlockPos pos, @Nonnull PartSlot slot) {
        return new PartPos(pos, slot);
    }

    @Nonnull
    public BlockPos getPos() {
        return pos;
    }

    @Nonnull
    public PartSlot getSlot() {
        return slot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartPos partPos = (PartPos) o;
        return Objects.equals(pos, partPos.pos) &&
                slot == partPos.slot;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, slot);
    }
}
