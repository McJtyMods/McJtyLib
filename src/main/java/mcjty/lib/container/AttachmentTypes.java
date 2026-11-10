package mcjty.lib.container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.ResourceLocation;

public final class AttachmentTypes {

    private static final Map<ResourceLocation, AttachmentType<?>> TYPES = new ConcurrentHashMap<>();

    public static void remember(AttachmentType<?> type) {
        var old = TYPES.putIfAbsent(type.identifier(), type);
        if (old != null && old != type)
            throw new IllegalArgumentException("Duplicate attachment: " + type.identifier());
    }

    public static AttachmentType<?> get(ResourceLocation id) {
        return TYPES.get(id);
    }
}
