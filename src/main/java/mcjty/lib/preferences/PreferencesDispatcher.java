package mcjty.lib.preferences;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

public class PreferencesDispatcher implements ICapabilityProvider, INBTSerializable<NBTTagCompound> {

    private PreferencesProperties properties = new PreferencesProperties();

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == PlayerPreferencesProperties.PREFERENCES_CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == PlayerPreferencesProperties.PREFERENCES_CAPABILITY ? (T) properties : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        properties.saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        properties.loadNBTData(nbt);
    }
}
