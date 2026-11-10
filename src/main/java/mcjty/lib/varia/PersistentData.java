package mcjty.lib.varia;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public final class PersistentData {

    private static final AttachmentType<CompoundTag> DATA =
            AttachmentRegistry.create(
                    ResourceLocation.fromNamespaceAndPath("mcjtylib", "persistent_data"),
                    builder ->
                            builder.initializer(CompoundTag::new)
                                    .persistent(CompoundTag.CODEC)
                                    .copyOnDeath());

    private PersistentData() {}

    public static CompoundTag get(AttachmentTarget target) {
        return target.getAttachedOrCreate(DATA);
    }
}
