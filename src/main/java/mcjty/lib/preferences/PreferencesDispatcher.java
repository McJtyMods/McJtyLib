package mcjty.lib.preferences;

import mcjty.lib.McJtyLib;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

public class PreferencesDispatcher implements ICapabilityProvider, INBTSerializable<CompoundNBT> {

    public PreferencesDispatcher(){
        properties = new PreferencesProperties();
    }

    private final PreferencesProperties properties;

    @Override
    public boolean hasCapability(Capability<?> capability, Direction facing) {
        return capability == McJtyLib.PREFERENCES_CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, Direction facing) {
        return capability == McJtyLib.PREFERENCES_CAPABILITY ? McJtyLib.PREFERENCES_CAPABILITY.cast(properties) : null;
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
