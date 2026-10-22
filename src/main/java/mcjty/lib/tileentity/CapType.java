package mcjty.lib.tileentity;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.information.CapabilityPowerInformation;
import mcjty.lib.api.infusable.CapabilityInfusable;
import mcjty.lib.api.module.CapabilityModuleSupport;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;

public enum CapType {
    ITEMS(Capabilities.Item.BLOCK),
    ITEMS_AUTOMATION(Capabilities.Item.BLOCK),
    CONTAINER(CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY),
    ENERGY(Capabilities.Energy.BLOCK),
    INFUSABLE(CapabilityInfusable.INFUSABLE_CAPABILITY),
    MODULE(CapabilityModuleSupport.MODULE_CAPABILITY),
    POWER_INFO(CapabilityPowerInformation.POWER_INFORMATION_CAPABILITY),
    FLUIDS(Capabilities.Fluid.BLOCK);

    private final BlockCapability capability;

    CapType(BlockCapability capability) {
        this.capability = capability;
    }

    public BlockCapability getCapability() {
        return capability;
    }
}
