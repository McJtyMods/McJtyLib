package mcjty.lib.multipart;

import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.List;

public class PartsProperty implements IUnlistedProperty<List> {

    private final String name;

    public PartsProperty(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(List value) {
        return true;
    }

    @Override
    public Class<List> getType() {
        return List.class;
    }

    @Override
    public String valueToString(List value) {
        return value.toString();
    }
}
