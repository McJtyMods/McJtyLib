package mcjty.lib.multipart;

import net.minecraft.core.BlockPos;

import javax.annotation.Nonnull;

public record PartPos(@Nonnull BlockPos pos, @Nonnull PartSlot slot) {

    public static PartPos create(@Nonnull BlockPos pos, @Nonnull PartSlot slot) {
        return new PartPos(pos, slot);
    }
}
