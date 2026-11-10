package mcjty.lib.varia;

import mcjty.lib.mixin.HolderLookupAdapterAccessor;
import mcjty.lib.mixin.RegistryOpsAccessor;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;

public final class RegistryLookup {

    private RegistryLookup() {}

    public static HolderLookup.Provider from(RegistryOps<?> ops) {
        var lookup = ((RegistryOpsAccessor) ops).mcjtylib$lookupProvider();
        if (lookup instanceof HolderLookupAdapterAccessor adapter)
            return adapter.mcjtylib$lookupProvider();
        throw new IllegalArgumentException(
                "Machine state requires registry ops backed by a holder lookup provider");
    }
}
