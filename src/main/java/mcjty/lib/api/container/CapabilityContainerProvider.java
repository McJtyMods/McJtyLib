package mcjty.lib.api.container;

import net.minecraft.world.MenuProvider;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityContainerProvider {

    @CapabilityInject(MenuProvider.class)
    public static Capability<MenuProvider> CONTAINER_PROVIDER_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(MenuProvider.class);
    }

}
