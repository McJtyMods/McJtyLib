package mcjty.lib.multipart;

import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.List;

public class PartsProperty implements IUnlistedProperty<List<PartBlockId>> {

    private final String name;

    public PartsProperty(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(List<PartBlockId> value) {
        return true;
    }

    @Override
    public Class<List<PartBlockId>> getType() {
        return (Class<List<PartBlockId>>) (Class) List.class;
    }

    @Override
    public String valueToString(List<PartBlockId> value) {
        return value.toString();
    }
}
