package mcjty.lib.api.infusable;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityInfusable {

    @CapabilityInject(IInfusable.class)
    public static Capability<IInfusable> INFUSABLE_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IInfusable.class);
    }

}
