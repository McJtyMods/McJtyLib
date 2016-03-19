package mcjty.lib.preferences;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class PlayerPreferencesProperties implements IExtendedEntityProperties {
    public static final String ID = "McJtyLibPreferencesProperties";

    private PreferencesProperties preferencesProperties;

    public PlayerPreferencesProperties() {
        preferencesProperties = new PreferencesProperties();
    }

    public static PlayerPreferencesProperties getProperties(EntityPlayer player) {
        //@todo
//        IExtendedEntityProperties properties = player.getExtendedProperties(ID);
//        return (PlayerPreferencesProperties) properties;
        return null;
    }

    public void tick(SimpleNetworkWrapper network) {
        preferencesProperties.tick(network);
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        preferencesProperties.saveNBTData(compound);
    }


    @Override
    public void loadNBTData(NBTTagCompound compound) {
        preferencesProperties.loadNBTData(compound);
    }


    @Override
    public void init(Entity entity, World world) {
        preferencesProperties.setEntity(entity);
    }

    public PreferencesProperties getPreferencesProperties() {
        return preferencesProperties;
    }
}
