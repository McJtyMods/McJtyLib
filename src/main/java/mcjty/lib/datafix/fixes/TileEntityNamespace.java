package mcjty.lib.datafix.fixes;

import java.util.Map;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.datafix.IFixableData;

public class TileEntityNamespace implements IFixableData {
    private final Map<String, String> oldToNewIdMap;
    private final int fixVersion;

    public TileEntityNamespace(Map<String, String> oldToNewIdMap, int fixVersion) {
        this.oldToNewIdMap = oldToNewIdMap;
        this.fixVersion = fixVersion;
    }

    @Override
    public int getFixVersion() {
        return fixVersion;
    }

    @Override
    public CompoundNBT fixTagCompound(CompoundNBT compound) {
        String s = oldToNewIdMap.get(compound.getString("id"));

        if (s != null) {
            compound.setString("id", s);
        }

        return compound;
    }
}
