package mcjty.lib.api.information;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityPowerInformation {

    @CapabilityInject(IPowerInformation.class)
    public static Capability<IPowerInformation> POWER_INFORMATION_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IPowerInformation.class);
    }

}
