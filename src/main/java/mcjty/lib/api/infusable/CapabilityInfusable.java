package mcjty.lib.api.infusable;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class CapabilityInfusable {

    public static Capability<IInfusable> INFUSABLE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IInfusable.class);
    }

}
