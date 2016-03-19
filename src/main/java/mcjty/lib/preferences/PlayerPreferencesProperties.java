package mcjty.lib.preferences;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class PlayerPreferencesProperties {

    public static Capability<PreferencesProperties> PREFERENCES_CAPABILITY;

    public static PreferencesProperties getProperties(EntityPlayer player) {
        return player.getCapability(PREFERENCES_CAPABILITY, null);
    }

    public static void tick(EntityPlayer player, SimpleNetworkWrapper network) {
        PreferencesProperties properties = getProperties(player);
        if (properties != null) {
            properties.tick(network);
        }
    }
}
