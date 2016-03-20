package mcjty.lib.preferences;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class PlayerPreferencesProperties {

    @CapabilityInject(PreferencesProperties.class)
    public static Capability<PreferencesProperties> PREFERENCES_CAPABILITY;

    public static PreferencesProperties getProperties(EntityPlayer player) {
        return player.getCapability(PREFERENCES_CAPABILITY, null);
    }

    public static void tick(EntityPlayerMP player, SimpleNetworkWrapper network) {
        PreferencesProperties properties = getProperties(player);
        if (properties != null) {
            properties.tick(player, network);
        }
    }
}
