package mcjty.lib.preferences;

import mcjty.lib.setup.ModSetup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PreferencesDispatcher implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    private final PreferencesProperties properties = createProperties();
    private final LazyOptional<PreferencesProperties> propertiesCap = LazyOptional.of(() -> properties);

    private <T> PreferencesProperties createProperties() {
        return new PreferencesProperties();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        if (cap == ModSetup.PREFERENCES_CAPABILITY) {
            return propertiesCap.cast();
        }
        return LazyOptional.empty();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return getCapability(cap);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        properties.saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        properties.loadNBTData(nbt);
    }

}
