package mcjty.lib.api.module;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class CapabilityModuleSupport {

    public static Capability<IModuleSupport> MODULE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IModuleSupport.class);
    }

}
