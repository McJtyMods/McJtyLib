package mcjty.lib.preferences;

import mcjty.lib.setup.ModSetup;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PreferencesDispatcher implements ICapabilityProvider, INBTSerializable<CompoundNBT> {

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
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        properties.saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        properties.loadNBTData(nbt);
    }

}
