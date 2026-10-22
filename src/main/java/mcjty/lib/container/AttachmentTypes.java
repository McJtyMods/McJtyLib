package mcjty.lib.container;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AttachmentTypes {

    private static final Map<Identifier,AttachmentType<?>> TYPES=new ConcurrentHashMap<>();

    public static void remember(AttachmentType<?> type) {
        var old=TYPES.putIfAbsent(type.identifier(),type);
        if(old!=null&&old!=type)throw new IllegalArgumentException("Duplicate attachment: "+type.identifier());
    }

    public static AttachmentType<?> get(Identifier id) {
        return TYPES.get(id);
    }

}
