package mcjty.lib.multipart;

import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.List;
import java.util.Map;

public class PartsProperty implements IUnlistedProperty<Map<PartSlot, MultipartTE.Part>> {

    private final String name;

    public PartsProperty(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(Map<PartSlot, MultipartTE.Part> value) {
        return true;
    }

    @Override
    public Class<Map<PartSlot, MultipartTE.Part>> getType() {
        return (Class<Map<PartSlot, MultipartTE.Part>>) (Class) Map.class;
    }

    @Override
    public String valueToString(Map<PartSlot, MultipartTE.Part> value) {
        return value.toString();
    }
}
