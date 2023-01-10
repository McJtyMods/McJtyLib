package mcjty.lib.api.container;

import net.minecraft.world.MenuProvider;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class CapabilityContainerProvider {

    public static final Capability<MenuProvider> CONTAINER_PROVIDER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(MenuProvider.class);
    }

}
