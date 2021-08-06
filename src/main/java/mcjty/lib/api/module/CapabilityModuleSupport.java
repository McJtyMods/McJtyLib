package mcjty.lib.api.module;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityModuleSupport {

    @CapabilityInject(IModuleSupport.class)
    public static Capability<IModuleSupport> MODULE_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IModuleSupport.class);
    }

}
